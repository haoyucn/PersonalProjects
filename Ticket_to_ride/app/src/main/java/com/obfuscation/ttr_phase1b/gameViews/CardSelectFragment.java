package com.obfuscation.ttr_phase1b.gameViews;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.obfuscation.ttr_phase1b.R;
import com.obfuscation.ttr_phase1b.activity.IPresenter;
import com.obfuscation.ttr_phase1b.gameViews.dummy.CardDialog;

import java.util.ArrayList;
import java.util.List;

import communication.Card;
import communication.GameColor;
import gamePresenters.ICardSelectPresenter;

/**
 * Created by jalton on 11/19/18.
 */

public class CardSelectFragment extends Fragment implements ICardSelectView, CardDialog.CardDialogListener {

    private static final String TAG = "TicketFrag";

    private boolean mIsTurn;

    private ICardSelectPresenter mPresenter;

    private int cardsToSelect;

    private TextView mHeader;

    private Button[] cardButtons;

    public static CardSelectFragment newInstance() {
        CardSelectFragment fragment = new CardSelectFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mPresenter.update();

        cardsToSelect = getArguments().getInt("cardsToSelect");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_select, container, false);

        mHeader = view.findViewById(R.id.card_header);

        cardButtons = new Button[9];

        int i = 0;

        cardButtons[i] = view.findViewById(R.id.card_orange);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(0);
            }
        });

        i++;

        cardButtons[i] = view.findViewById(R.id.card_red);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToast("This is toast");
                onSelect(1);
            }
        });

        i++;

        cardButtons[i] = view.findViewById(R.id.card_green);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(2);
            }
        });

        i++;

        cardButtons[i] = view.findViewById(R.id.card_yellow);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(3);
            }
        });

        i++;

        cardButtons[i] = view.findViewById(R.id.card_purple);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(4);
            }
        });

        i++;

        cardButtons[i] = view.findViewById(R.id.card_blue);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(5);
            }
        });

        i++;

        cardButtons[i] = view.findViewById(R.id.card_white);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(6);
            }
        });

        i++;

        cardButtons[i] = view.findViewById(R.id.card_black);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(7);
            }
        });

        i++;

        cardButtons[i] = view.findViewById(R.id.card_loco);
        cardButtons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(8);
            }
        });

        updateUI();

        return view;
    }



    @Override
    public void updateUI() {
        setHand(mPresenter.getHand());
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        mPresenter = (ICardSelectPresenter) presenter;
    }

    @Override
    public void sendToast(String toast) {
        Log.d(TAG, "sendToast: " + toast);
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void setHand(int[] hand) {
        for(int i = 0; i < 9; i++) {
            cardButtons[i].setText(Integer.toString(hand[i]));
        }
    }

    /**
     * Sets the number of cards to choose
     * @param cardsToSelect
     */
    @Override
    public void setCardsToSelect(int cardsToSelect) {
        this.cardsToSelect = cardsToSelect;
    }

    /**
     * checks to see if the correct cards have been selected. If
     */
    private void onSelect(int i){
        if (cardsToSelect == 0) {
            sendToast("Hold your horses");
            return;
        }

//        String toast = "selected cards " + i;

//        sendToast(toast);

        // toUse holds 2 ints
        // index 0 holds the number of color cards
        // index 1 holds the number of locmotives
        ArrayList<Integer> toUse = new ArrayList<>();

        //Special case for locomotive
        if(i == 8) {
            if (cardButtons[i] == null) {
                System.out.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
            }
            if (cardButtons[i] == null) {
                System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            }
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@");
            int remain = cardsToSelect - Integer.parseInt(cardButtons[i].getText().toString());
            if (remain > 0) {
                sendToast("Not enough cards: need " + remain + " more");
            }
            else {
                toUse.add(0);
                toUse.add(cardsToSelect);
            }
        }
        else {
            int colorCards = Integer.parseInt(cardButtons[i].getText().toString());
            int remain = cardsToSelect - colorCards;
            remain -= Integer.parseInt(cardButtons[8].getText().toString());

            if (remain > 0) {
                sendToast("Not enough cards: need " + remain + " more");
                return;
            }
            else {


                //case where you don't need locomotive
                if (colorCards > cardsToSelect) {
                    toUse.add(cardsToSelect);
                    toUse.add(0);
                }
                //case where you need locomotives
                else {
                    toUse.add(colorCards);
                    toUse.add(cardsToSelect - colorCards);
                }
            }
        }

        Bundle args = new Bundle();
        args.putIntegerArrayList("toUse", toUse);
        args.putSerializable("color", indexToColor(i));

        CardDialog dialog = CardDialog.newInstance(args);
        FragmentManager fm = getFragmentManager();
        dialog.show(fm, "confirm");
    }

    private GameColor indexToColor(int i) {
        switch(i) {
            case 0:
                return GameColor.ORANGE;
            case 1:
                return GameColor.RED;
            case 2:
                return GameColor.GREEN;
            case 3:
                return GameColor.YELLOW;
            case 4:
                return GameColor.PURPLE;
            case 5:
                return GameColor.BLUE;
            case 6:
                return GameColor.WHITE;
            case 7:
                return GameColor.BLACK;
            case 8:
                return GameColor.LOCOMOTIVE;
                default: return GameColor.GREY;
        }
    }

    @Override
    public void onConfirmCards(CardDialog dialog) {
        List<Card> cards = new ArrayList<>();
        ArrayList<Integer> toUse = dialog.getToUse();
        GameColor color = dialog.getColor();

        for(int i = 0; i < toUse.get(0); i++) {
            cards.add(new Card(color));
        }

        for(int j = 0; j < toUse.get(1); j++) {
            cards.add(new Card(GameColor.LOCOMOTIVE));
        }
        mPresenter.playerChooseCards(cards);
        dialog.dismiss();
    }

    @Override
    public void onRejectCards(DialogFragment dialog) {
        dialog.dismiss();
    }
}
