package com.obfuscation.ttr_phase1b.gameViews;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.obfuscation.ttr_phase1b.R;
import com.obfuscation.ttr_phase1b.activity.IPresenter;

import java.util.List;

import communication.GameColor;
import communication.Player;
import communication.PlayerOpponent;
import gamePresenters.IGamePresenter;
import gamePresenters.IPInfoPresenter;


public class PlayerInfoDialogFragment extends Fragment implements IPlayerInfoView {

    private static final String TAG = "PlayerDiaFrag";

    private IPInfoPresenter mPresenter;

    private List<PlayerOpponent> mPlayers;

    private Button mBackButton;
    private RecyclerView mPlayerRecycler;
    private PlayerAdapter mPlayerAdapter;

    private PlayerInfoDialogFragment self;

    public PlayerInfoDialogFragment() {
        self = this;
    }

    public static PlayerInfoDialogFragment newInstance() {
        PlayerInfoDialogFragment fragment = new PlayerInfoDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_info_dialog, container, false);

        mBackButton = view.findViewById(R.id.playerinfo_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now sending message");
                mPresenter.onClose(self);
            }
        });

        mPlayerRecycler = view.findViewById(R.id.playerinfo_recycler_view);
        mPlayerRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(mPresenter != null) {
            mPlayers = mPresenter.getPlayers();
        }

        updateUI();

        return view;
    }

    @Override
    public void setPlayers(List<PlayerOpponent> players) {
        mPlayers = players;
    }

    @Override
    public void updateUI() {
        Log.d(TAG, "getting updated: " + mPlayerRecycler);

        if(mPlayerRecycler != null && mPlayers != null) {
            Log.d(TAG+"_updateUI", "mPlayers: " + mPlayers);
            mPlayerAdapter = new PlayerAdapter(mPlayers);
            mPlayerRecycler.setAdapter(mPlayerAdapter);
        }
    }

    @Override
    public void sendToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT);
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        mPresenter = (IPInfoPresenter) presenter;
    }

    private class PlayerHolder extends RecyclerView.ViewHolder {

        private TextView mPlayerNameView;
        private TextView mPlayerPointView;
        private TextView mPlayerTrainsView;
        private TextView mPlayerCardsView;
        private TextView mPlayerTicketsView;
        private LinearLayout mLayout;

        public PlayerHolder(View view) {
            super(view);

            mPlayerNameView = view.findViewById(R.id.player_name);
            mPlayerPointView = view.findViewById(R.id.player_points);
            mPlayerCardsView = view.findViewById(R.id.player_cards);
            mPlayerTrainsView = view.findViewById(R.id.player_trains);
            mPlayerTicketsView = view.findViewById(R.id.player_tickets);
            mLayout = view.findViewById(R.id.player_info_layout);
        }

        public void bind(Player player) {
            mPlayerNameView.setText("" + player.getPlayerName());
            mPlayerPointView.setText("" + player.getPoint());
            mPlayerCardsView.setText("" + player.getCardNum());
            mPlayerTrainsView.setText("" + player.getTrainNum());
            mPlayerTicketsView.setText("" + player.getTicketNum());

            mLayout.setBackgroundColor(getPlayerColor(player.getPlayerColor()));
        }

        private int getPlayerColor(GameColor color) {
            int retColor = 0;
            switch (color) {
                case PLAYER_RED:
                    retColor = getResources().getColor(R.color.playerRedLight);
                    break;
                case PLAYER_BLUE:
                    retColor = getResources().getColor(R.color.playerBlueLight);
                    break;
                case PLAYER_PURPLE:
                    retColor = getResources().getColor(R.color.playerPurpleLight);
                    break;
                case PLAYER_BLACK:
                    retColor = getResources().getColor(R.color.playerBlackLight);
                    break;
                case PLAYER_YELLOW:
                    retColor = getResources().getColor(R.color.playerYellowLight);
                    break;
                    default:
                        retColor = getResources().getColor(R.color.trainGrey);
            }
            return retColor;
        }
    }

    private class PlayerAdapter extends RecyclerView.Adapter<PlayerHolder> {

        private List<PlayerOpponent> mPlayers;

        public PlayerAdapter(List<PlayerOpponent> players) {
            mPlayers = players;
        }

        public PlayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.playerinfo_view, parent, false);
            return new PlayerHolder(view);
        }

        public void onBindViewHolder(PlayerHolder holder, int position) {
            Log.d(TAG+"_adapter", "message[" + position + "]: " + mPlayers.get(position));
            holder.bind(mPlayers.get(position));
        }

        public int getItemCount() {
            return mPlayers.size();
        }

    }

}
