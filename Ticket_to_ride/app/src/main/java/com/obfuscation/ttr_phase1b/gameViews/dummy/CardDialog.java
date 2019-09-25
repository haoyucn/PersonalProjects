package com.obfuscation.ttr_phase1b.gameViews.dummy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

import communication.GameColor;

/**
 * Created by jalton on 11/19/18.
 */

public class CardDialog extends DialogFragment {

    public interface CardDialogListener {
        void onConfirmCards(CardDialog dialog);
        void onRejectCards(DialogFragment dialog);
    }

    CardDialogListener mListener;
    ArrayList<Integer> toUse;
    GameColor color;

    public static CardDialog newInstance(Bundle args) {
        CardDialog dialog = new CardDialog();

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        toUse = getArguments().getIntegerArrayList("toUse");
        color = (GameColor) getArguments().getSerializable("color");

        String header = createHeader(toUse, color);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(header)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onConfirmCards(CardDialog.this);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onRejectCards(CardDialog.this);
            }
        });
        return builder.create();


    }

    private String createHeader(ArrayList<Integer> toUse, GameColor color) {
        StringBuilder header = new StringBuilder("Use ");

        //case only locomotive
        if (color == GameColor.LOCOMOTIVE) {
            header.append(toUse.get(1));
            header.append(" locomotive cards?");
        }
        //case no locomotive
        else if (toUse.get(1) <= 0) {
            String colorString = colorToString(color);
            header.append(toUse.get(0));
            header.append(" ");
            header.append(colorString);
            header.append(" cards?");
        }
        //case mix
        else {
            String colorString = colorToString(color);
            header.append(toUse.get(0));
            header.append(" ");
            header.append(colorString);
            header.append(" and ");
            header.append(toUse.get(1));
            header.append(" locomotive cards?");
        }

        return header.toString();
    }

    private String colorToString(GameColor color) {
        switch(color) {
            case RED:
                return "red";
            case ORANGE:
                return "orange";
            case YELLOW:
                return "yellow";
            case GREEN:
                return "green";
            case BLUE:
                return "blue";
            case PURPLE:
                return "purple";
            case WHITE:
                return "white";
            case BLACK:
                return "black";
            case LOCOMOTIVE:
                return "locomotive";
                default: return "grey";
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mListener = (CardDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("must implement CardDialogListener");

        }
    }

    public ArrayList<Integer> getToUse() {
        return toUse;
    }

    public GameColor getColor() {
        return color;
    }
}
