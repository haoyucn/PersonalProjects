package gamePresenters;

import android.support.v4.app.Fragment;

import com.obfuscation.ttr_phase1b.activity.IPresenter;
import com.obfuscation.ttr_phase1b.gameViews.IPlayerInfoView;

import java.util.List;

import communication.PlayerOpponent;

/**
 * Created by jalton on 11/29/18.
 */

public interface IPInfoPresenter extends IPresenter{

    void onClose(Fragment fragment);

    List<PlayerOpponent> getPlayers();

    void showPlayerInfo(IPlayerInfoView view);

    /**
     * Storing the previous IGamePresenter allows the activity to restore the
     * presenter when the fragment is removed
     * @param pres
     */
    void setPrevPresenter(IGamePresenter pres);
}
