package gamePresenters;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.obfuscation.ttr_phase1b.gameViews.IPTicketsView;
import com.obfuscation.ttr_phase1b.gameViews.IPlayerInfoView;

import java.util.List;

import communication.PlayerOpponent;
import communication.Result;
import model.IGameModel;
import model.ModelFacade;

/**
 * Created by jalton on 11/29/18.
 */

public class PInfoPresenter implements IPInfoPresenter {

    private static String TAG = "pticksPres";

    private IPlayerInfoView view;
    private IPTicketsPresenter.OnCloseListener listener;
    private IGameModel model;
    private IGamePresenter prevPresenter;

    public PInfoPresenter(IPlayerInfoView view, IPTicketsPresenter.OnCloseListener listener, IGamePresenter prevPresenter) {
        this.view = view;
        view.setPresenter(this);
        this.listener = listener;
        model = ModelFacade.getInstance();
        this.prevPresenter = prevPresenter;
    }

    @Override
    public void onClose(Fragment fragment) {
        listener.onClose(fragment, prevPresenter);
    }

    @Override
    public void updateInfo(Object result) {
        prevPresenter.updateInfo(result);
    }

    @Override
    public void update() {
        prevPresenter.update();
    }

    @Override
    public void showToast(String toast) {
        view.sendToast(toast);
    }

    @Override
    public void setPrevPresenter(IGamePresenter pres) {
        prevPresenter = pres;
    }

    @Override
    public List<PlayerOpponent> getPlayers() {
        if(prevPresenter != null) {
            return prevPresenter.getPlayers();
        }
        return null;
    }

    @Override
    public void showPlayerInfo(IPlayerInfoView view) {
        Log.d(TAG, "showPlayerInfo: " + model.getPlayers());
        this.view = view;
        this.view.setPresenter(this);
        this.view.setPlayers(model.getOpponents());
        this.view.updateUI();
    }
}
