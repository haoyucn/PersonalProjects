package gamePresenters;

import com.obfuscation.ttr_phase1b.activity.IPresenter;

import java.util.List;

import communication.Card;
import communication.Player;
import communication.Route;

/**
 * Created by jalton on 11/19/18.
 */

public interface ICardSelectPresenter extends IPresenter {

    /**
     * Tells the presenter to use the given cards for the grey mRoute
     * @param cards the cards to use
     * @return
     */
    void playerChooseCards(List<Card> cards);

    int[] getHand();

    public interface OnBackListener {
        void onBack();
    }

}
