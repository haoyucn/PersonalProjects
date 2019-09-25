package gamePresenters;

import android.os.Bundle;
import android.util.Log;

import com.obfuscation.ttr_phase1b.gameViews.ICardSelectView;

import java.util.List;

import communication.Card;
import communication.GameColor;
import communication.Player;
import communication.Result;
import communication.Route;
import model.IGameModel;
import model.ModelFacade;

/**
 * Created by jalton on 11/19/18.
 */

public class CardSelectPresenter implements ICardSelectPresenter {

    private static String TAG = "cardSelPres";

    ICardSelectPresenter.OnBackListener listener;
    ICardSelectView view;
    IGameModel model;

    Route mRoute;
    boolean actionSelected;

    public CardSelectPresenter(ICardSelectView view, OnBackListener listener, Bundle args) {
        this.view = view;
        view.setPresenter(this);
        this.listener = listener;
        model = ModelFacade.getInstance();

        mRoute = (Route) args.getSerializable("route");
        actionSelected = false;
    }


    @Override
    public void updateInfo(Object result) {
        Log.d(TAG, "updateInfo: ");
        if(result instanceof Result) {
            Result r = (Result) result;
            Log.d(TAG, "result not null: " + r);
            if(r.isSuccess() && actionSelected) {
                Log.d(TAG, "updateInfo: ending turn");
                model.endTurn();
                listener.onBack();
            }else {
                Log.d(TAG, "failed: " + r.getErrorInfo());
                view.sendToast(r.getErrorInfo());
                listener.onBack();
            }
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void showToast(String toast) {
        view.sendToast(toast);
    }

    @Override
    public void playerChooseCards(List<Card> cards) {
        actionSelected = true;
        model.claimRoute(mRoute, cards);
    }

    @Override
    public int[] getHand() {
        int[] hand = new int[9];

        List<Card> cards = model.getCards();

        for(int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            switch (card.getColor()){
                case ORANGE:
                    hand[0] += 1;
                    break;
                case RED:
                    hand[1] += 1;
                    break;
                case GREEN:
                    hand[2] += 1;
                    break;
                case YELLOW:
                    hand[3] += 1;
                    break;
                case PURPLE:
                    hand[4] += 1;
                    break;
                case BLUE:
                    hand[5] += 1;
                    break;
                case WHITE:
                    hand[6] += 1;
                    break;
                case BLACK:
                    hand[7] += 1;
                    break;
                case LOCOMOTIVE:
                    hand[8] += 1;
                    break;
                default:
            }
        }

        return hand;
    }
}
