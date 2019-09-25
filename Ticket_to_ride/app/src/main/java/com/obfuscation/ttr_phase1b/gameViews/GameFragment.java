package com.obfuscation.ttr_phase1b.gameViews;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.obfuscation.ttr_phase1b.R;
import com.obfuscation.ttr_phase1b.activity.IPresenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import communication.Card;
import communication.City;
import communication.GameColor;
import communication.GameMap;
import communication.Player;
import communication.Route;
import communication.Ticket;
import gamePresenters.IGamePresenter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import static communication.GameColor.*;


public class GameFragment extends Fragment implements IGameView, OnMapReadyCallback {

    private static final String TAG = "GameFrag";

    private IGamePresenter mPresenter;

    private boolean mIsSetup;
    private boolean mIsTurn;
    private boolean mLastTurn;
    private List<Card> mCards;
    private List<Card> mFaceCards;
    private List<Ticket> mTickets;

    private TextView mTurnText;
    private ImageView[] mFaceCardViews;
    private ImageView mDeck;
    private TextView mDeckSize;
    private TextView[] mCardViews;

    private FloatingActionButton mHistoryButton;
    private FloatingActionButton mPlayersButton;
    private FloatingActionButton mTicketsButton;
    private FloatingActionButton mChatButton;

    private TextView mTicketDeckSize;

    private LinearLayout mBoard;
    private TextView mTicketsView;
    private TextView mPointsView;
    private TextView mTrainsView;

    private Player mPlayer;
    private GameMap mMap;

    private Map<Route, Polyline> mRouteLines;
    private Map<LatLng, Route> mRoutes;
    private Map<Route, Marker> mRoutes2;

    MapView mMapView;
    private GoogleMap googleMap;

    private LatLng selected;

    private Map<GameColor, Integer> cardMap;
    private Map<GameColor, Integer> colorMap;

    private Marker currentMarker;
    private boolean first = true;

    public GameFragment() {
        mIsTurn = false;
        mCards = null;
        mFaceCardViews = null;
        mTickets = null;
        mIsSetup = false;
        mLastTurn = false;
    }

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.game_fragment, container, false);

        initCardMap();
        initColorMap();

        selected = null;

        mHistoryButton = rootView.findViewById(R.id.history_button);
        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "View history");
                mPresenter.showHistory();
            }
        });

        mPlayersButton = rootView.findViewById(R.id.players_button);
        mPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "View players");
//                PlayerInfoDialogFragment playerInfoFrag = PlayerInfoDialogFragment.newInstance();
//                playerInfoFrag.show(getFragmentManager(), "PlayerinfoDialogFragment");
//                mPresenter.showPlayerInfo(playerInfoFrag);
                mPresenter.showPlayerInfo(null);
            }
        });

        mTicketsButton = rootView.findViewById(R.id.tickets_button);
        mTicketsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "selecting tickets");
                mPresenter.selectTickets();
            }
        });

        mChatButton = rootView.findViewById(R.id.chat_button);
        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "view chat");
                mPresenter.showChat();
            }
        });

        mBoard = rootView.findViewById(R.id.bottom_board);
        mTicketsView = rootView.findViewById(R.id.txt_tickets);
        mTicketsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "view tickets");
                mPresenter.showTickets();
            }
        });
        mPointsView = rootView.findViewById(R.id.txt_points);
        mTrainsView = rootView.findViewById(R.id.txt_trains);
        mTurnText = rootView.findViewById(R.id.turn_text);

        initCardViews(rootView);


        //Gets MapView from xml layout
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

//        mMapView.getMapAsync(this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        mMapView.onResume();

        mIsSetup = true;

        mPresenter.updateInfo(true);

        Log.d(TAG, "onCreateView: done mIsSetup");

        return rootView;
    }

    @Override
    public void updateUI() {
        if(!mIsSetup){
            return;
        }

        if(mPlayer != null) {
            Log.d(TAG, "updateUI: player: " + mPlayer);
            setColor();
            setPoints(mPlayer.getPoint());
            setTrains(mPlayer.getTrainNum());
        }if(mCards != null) {
            updateCards();
        }if(mFaceCards != null) {
            Log.d(TAG, "updateUI: update cards");
            updateFaceCards();
        }if(mTickets != null) {
            updateTickets();
        }if(mMap != null) {
//            mMapView.getMapAsync(this);
        }if(mIsTurn) {
            mTurnText.setText("Your Turn");
            if(mLastTurn) {
                mTurnText.setText("Your Last Turn!");
            }
        }else {
            mTurnText.setText("");
            if(mLastTurn) {
                mTurnText.setText("Last Round!");
            }
        }
    }

    private void selectRoute(Route route) {
        mPresenter.claimRoute(route, mPlayer);
    }

    private void updateCards() {
        int[] cardCnts = new int[mCardViews.length];
        for(int i = 0; i < mCards.size(); i++) {
            switch (mCards.get(i).getColor()) {
                case ORANGE:
                    cardCnts[0]++;
                    break;
                case GREEN:
                    cardCnts[1]++;
                    break;
                case PURPLE:
                    cardCnts[2]++;
                    break;
                case WHITE:
                    cardCnts[3]++;
                    break;
                case LOCOMOTIVE:
                    cardCnts[4]++;
                    break;
                case RED:
                    cardCnts[5]++;
                    break;
                case YELLOW:
                    cardCnts[6]++;
                    break;
                case BLUE:
                    cardCnts[7]++;
                    break;
                case BLACK:
                    cardCnts[8]++;
                    break;
            }
        }
        for(int i = 0; i < mCardViews.length; i++) {
            mCardViews[i].setText("" + cardCnts[i]);
        }
    }

    private void updateFaceCards() {
        int i = 0;
        while (i < mFaceCards.size()) {
            try {
                Card card = mFaceCards.get(i);
                ImageView faceCardView = mFaceCardViews[i];

                faceCardView.setImageResource(cardMap.get(card.getColor()));
            }
            catch (ArrayIndexOutOfBoundsException e) {
                ImageView faceCardView = mFaceCardViews[i];

                faceCardView.setImageResource(cardMap.get(GREY));
            }

            i++;
        }

        while (i < 5) {
            ImageView faceCardView = mFaceCardViews[i];
            faceCardView.setImageResource(R.drawable.card_blank);

            ++i;
        }
    }

    private void updateTickets() {
        mTicketsView.setText("" + mTickets.size());
    }

    private void setColor() {
        if(!mIsSetup){
            return;
        }
        GameColor color = mPlayer.getPlayerColor();
        ColorStateList stateList = null;
        int colorID = colorMap.get(mPlayer.getPlayerColor());
        int board = 0;

        switch(color) {
            case PLAYER_RED:
                board = R.drawable.board_r;
                break;
            case PLAYER_YELLOW:
                board = R.drawable.board_y;
                break;
            case PLAYER_PURPLE:
                board = R.drawable.board_p;
                break;
            case PLAYER_BLACK:
                board = R.drawable.board_bla;
                break;
            case PLAYER_BLUE:
                board = R.drawable.board_blu;
                break;
        }

        mHistoryButton.setBackgroundTintList(ColorStateList.valueOf(colorID));
        mPlayersButton.setBackgroundTintList(ColorStateList.valueOf(colorID));
        mTicketsButton.setBackgroundTintList(ColorStateList.valueOf(colorID));
        mChatButton.setBackgroundTintList(ColorStateList.valueOf(colorID));

        mBoard.setBackgroundResource(board);
    }

    @Override
    public void updateRoute(Route r) {
        if(mRouteLines == null) {
            return;
        }
       // googleMap.clear();
        String name = r.getClaimedBy().getPlayerName();
        int color = colorMap.get(r.getClaimedBy().getPlayerColor());
//        boolean claimed = (r.getClaimedBy() != null);
        boolean claimed = true;
        Polyline poly = mRouteLines.get(r);

        if (poly != null) {
            poly.remove();
        }

        LatLng start = new LatLng(r.getStartPos()[0], r.getStartPos()[1]);
        LatLng mid = new LatLng(r.getMidPoint()[0], r.getMidPoint()[1]);
        LatLng end = new LatLng(r.getEndPos()[0], r.getEndPos()[1]);

        PolylineOptions p = new PolylineOptions()
                .add(start, mid, end)
                .color(color)
                .clickable(true);

        mRouteLines.remove(r);
        mRouteLines.put(r, googleMap.addPolyline(p));

        Marker m = mRoutes2.get(r);
        if (m != null) {
            m.remove();
        }
        if (r != null) {
            mRoutes2.remove(r);
        }
//        for (Route route : mRoutes2.keySet()) {
//            mRoutes2.get(route).remove();
//        }
        if(claimed) {
            mRoutes2.put(r,
                    googleMap.addMarker(new MarkerOptions()
                            .position(mid)
                            .title(r.getClaimedBy().getPlayerName())
                            .snippet(new StringBuilder(r.getLength() + " points").toString())
                    ));
        }
        else {
            mRoutes2.put(r,
                    googleMap.addMarker(new MarkerOptions()
                            .position(mid)
                            .title(r.getLength().toString())
                            .snippet("unclaimed")
                    ));
        }

        mRoutes.remove(mid);
        mRoutes.put(mid, r);
    }

    private void initCardViews(View rootView) {
        Log.d(TAG, "initCardViews");
        mFaceCardViews = new ImageView[5];
        mFaceCardViews[0] = rootView.findViewById(R.id.card1);
        mFaceCardViews[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + mFaceCards.get(0).getColor());
                mPresenter.chooseCard(0);
            }
        });

        mFaceCardViews[1] = rootView.findViewById(R.id.card2);
        mFaceCardViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + mFaceCards.get(1).getColor());
                mPresenter.chooseCard(1);
            }
        });

        mFaceCardViews[2] = rootView.findViewById(R.id.card3);
        mFaceCardViews[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + mFaceCards.get(2).getColor());
                mPresenter.chooseCard(2);
            }
        });

        mFaceCardViews[3] = rootView.findViewById(R.id.card4);
        mFaceCardViews[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + mFaceCards.get(3).getColor());
                mPresenter.chooseCard(3);
            }
        });

        mFaceCardViews[4] = rootView.findViewById(R.id.card5);
        mFaceCardViews[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + mFaceCards.get(4).getColor());
                mPresenter.chooseCard(4);
            }
        });

        mDeck = rootView.findViewById(R.id.deck);
        mDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.chooseCard(-1);
            }
        });
        mDeck.setBackgroundResource(R.drawable.card_deck2);

        mDeckSize = rootView.findViewById(R.id.txt_deck);
        mTicketDeckSize = rootView.findViewById(R.id.txt_tickets_deck);

        mCardViews = new TextView[9];
        mCardViews[0] = rootView.findViewById(R.id.txt_cards_orange);
        mCardViews[1] = rootView.findViewById(R.id.txt_cards_green);
        mCardViews[2] = rootView.findViewById(R.id.txt_cards_purple);
        mCardViews[3] = rootView.findViewById(R.id.txt_cards_white);
        mCardViews[4] = rootView.findViewById(R.id.txt_cards_locomotive);
        mCardViews[5] = rootView.findViewById(R.id.txt_cards_red);
        mCardViews[6] = rootView.findViewById(R.id.txt_cards_yellow);
        mCardViews[7] = rootView.findViewById(R.id.txt_cards_blue);
        mCardViews[8] = rootView.findViewById(R.id.txt_cards_black);
    }

    private void initCardMap() {
        cardMap = new HashMap<>();
        cardMap.put(PURPLE, R.drawable.card_pur);
        cardMap.put(BLUE, R.drawable.card_blu);
        cardMap.put(ORANGE, R.drawable.card_ora);
        cardMap.put(WHITE, R.drawable.card_whi);
        cardMap.put(GREEN, R.drawable.card_gre);
        cardMap.put(YELLOW, R.drawable.card_yel);
        cardMap.put(BLACK, R.drawable.card_bla);
        cardMap.put(RED, R.drawable.card_red);
        cardMap.put(LOCOMOTIVE, R.drawable.card_loc);
        cardMap.put(GREY, R.drawable.card_blank);
    }

    private void initColorMap() {
        colorMap = new HashMap<>();
        colorMap.put(PURPLE,    getResources().getColor(R.color.trainPurple));
        colorMap.put(BLUE,      getResources().getColor(R.color.trainBlue));
        colorMap.put(ORANGE,    getResources().getColor(R.color.trainOrange));
        colorMap.put(WHITE,     getResources().getColor(R.color.trainWhite));
        colorMap.put(GREEN,     getResources().getColor(R.color.trainGreen));
        colorMap.put(YELLOW,    getResources().getColor(R.color.trainYellow));
        colorMap.put(BLACK,     getResources().getColor(R.color.trainBlack));
        colorMap.put(RED,       getResources().getColor(R.color.trainRed));
        colorMap.put(GREY,      getResources().getColor(R.color.trainGrey));

        colorMap.put(PLAYER_BLUE,   getResources().getColor(R.color.playerBlue));
        colorMap.put(PLAYER_RED,    getResources().getColor(R.color.playerRed));
        colorMap.put(PLAYER_PURPLE, getResources().getColor(R.color.playerPurple));
        colorMap.put(PLAYER_YELLOW, getResources().getColor(R.color.playerYellow));
        colorMap.put(PLAYER_BLACK,  getResources().getColor(R.color.playerBlack));

//        Log.d(TAG, "trainPurple: " + R.color.trainPurple);
//        Log.d(TAG, "black: " + getResources().getColor(R.color.trainPurple));
//        Log.d(TAG, "trainPurple: " + ContextCompat.getColor(getContext(), R.color.trainPurple));
//        Log.d(TAG, "black: " + R.color.playerBlack);
//        Log.d(TAG, "black: " + getResources().getColor(R.color.playerBlack));
//        Log.d(TAG, "black: " + ContextCompat.getColor(getContext(), R.color.playerBlack));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (true) {
            first = false;
            this.googleMap = googleMap;

            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getActivity(), R.raw.map_style));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }


            // Drop markers on all the cities

            mRouteLines = new HashMap<>();
            mRoutes = new HashMap<>();
            mRoutes2 = new HashMap<>();

            initCities(googleMap);

            initRoutes(googleMap);

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    if (mRoutes.containsKey(marker.getPosition())) {
                        if (marker.getPosition().equals(selected)) {
                            selectRoute(mRoutes.get(marker.getPosition()));
                            currentMarker = marker;
                        }
                    }

                    selected = marker.getPosition();

                    return false;
                }
            });


            // For zooming automatically to the location of the marker
            LatLng ny = new LatLng(40, -73);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(ny).zoom(5).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void initCities(GoogleMap googleMap) {

        if(mMap != null) {
            List<City> cities = mMap.getCities();
            LatLng latLng = null;

            BitmapDrawable bmdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.location);
            Bitmap smallMarker = Bitmap.createScaledBitmap(bmdraw.getBitmap(), 100, 100, false);

            for(int i = 0; i < cities.size(); i++) {
                City city = cities.get(i);

                latLng = new LatLng(city.getLat(), city.getLng());
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(city.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_city2)));

            }
        }

//        LatLng ny = new LatLng(41, -74);
//        googleMap.addMarker(new MarkerOptions().position(ny).title("New York").snippet("Aka \"Not Old York\""));
    }

    private void initRoutes(GoogleMap googleMap) {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            List<Route> routes = mMap.getRoutes();
            Route r = null;

            boolean claimed;

            for (int i = 0; i < routes.size(); i++) {
                r = routes.get(i);

                int color;
                claimed = (r.getClaimedBy() != null);
                LatLng start;
                LatLng mid;
                LatLng end;

                if (claimed) {
                    Log.d(TAG, "initRoutes: claimer: " + r.getClaimedBy() + ", color: " + r.getClaimedBy().getPlayerColor());
                    try {
                        color = colorMap.get(r.getClaimedBy().getPlayerColor());
                    } catch (NullPointerException e) {
                        color = colorMap.get(GREY);
                    }

                    start = new LatLng(r.getStartPos()[0], r.getStartPos()[1]);
                    mid = new LatLng(r.getMidPoint()[0], r.getMidPoint()[1]);
                    end = new LatLng(r.getEndPos()[0], r.getEndPos()[1]);


                    PolylineOptions p = new PolylineOptions()
                            .add(start, mid, end)
                            .color(color)
                            .clickable(true);

                    mRouteLines.put(r, googleMap.addPolyline(p));
                    mRoutes2.put(r,
                            googleMap.addMarker(new MarkerOptions()
                                    .position(mid)
                                    .title(r.getClaimedBy().getPlayerName())
                                    .snippet(new StringBuilder(r.getLength() + " points").toString())
                            ));
                } else {
                    try {
                        color = colorMap.get(r.getColor());
                    } catch (NullPointerException e) {
                        color = colorMap.get(GREY);
                    }

                    start = new LatLng(r.getStartPos()[0], r.getStartPos()[1]);
                    mid = new LatLng(r.getMidPoint()[0], r.getMidPoint()[1]);
                    end = new LatLng(r.getEndPos()[0], r.getEndPos()[1]);

                    List<PatternItem> pattern = Arrays.asList(
                            new Dash(30), new Gap(20));

                    PolylineOptions p = new PolylineOptions()
                            .add(start, mid, end)
                            .color(color)
                            .clickable(true).pattern(pattern);

                    mRouteLines.put(r, googleMap.addPolyline(p));
                    mRoutes2.put(r,
                            googleMap.addMarker(new MarkerOptions()
                                    .position(mid)
                                    .title(r.getLength().toString())
                                    .snippet("unclaimed")
                            ));
                }

                mRoutes.put(mid, r);
            }
    }

    @Override
    public List<Card> chooseCards(int number) {
        return null;
    }

    @Override
    public void setMap(GameMap map) {
        mMap = map;
    }

    @Override
    public void setPlayer(Player player) {
        mPlayer = player;
    }

    @Override
    public void setCards(List<Card> cards) {
        mCards = cards;
    }

    @Override
    public void setFaceCards(List<Card> cards) {
        mFaceCards = cards;
    }

    @Override
    public void setTickets(List<Ticket> tickets) {
        mTickets = tickets;
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        mPresenter = (IGamePresenter) presenter;
    }

    @Override
    public void setPoints(int points) {
        if(!mIsSetup){
            return;
        }
        mPointsView.setText("" + points);
    }

    @Override
    public void setTrains(int trains) {
        if(!mIsSetup){
            return;
        }
        mTrainsView.setText("" + trains);
    }

    @Override
    public void setDeckSize(int size) {
        if(!mIsSetup){
            return;
        }
        mDeckSize.setText("" + size);
    }

    @Override
    public void setTicketDeckSize(int size) {
        if(!mIsSetup){
            return;
        }
        mTicketDeckSize.setText("" + size);
    }

    @Override
    public void setTurn(boolean turn) {
        mIsTurn = turn;
    }

    @Override
    public void setLastTurn(boolean lastTurn) {
        mLastTurn = lastTurn;
    }

    @Override
    public void sendToast(String toast) {
        Log.d(TAG, "sendToast: " + toast);
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}

