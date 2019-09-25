package com.obfuscation.ttr_phase1b.activity;

import android.util.Log;

import communication.Result;
import communication.Route;
import gamePresenters.IGamePresenter;
import gamePresenters.TicketPresenter;
import model.ModelFacade;
import model.DisplayState;

public class PresenterFacade {

    private static final String TAG = "PresenterFacade";

    private static PresenterFacade SINGLETON = new PresenterFacade();

    private static boolean changed;

    private IPresenter mPresenter;

    private PresenterFacade() {
    }

    public static PresenterFacade getInstance() {
        return SINGLETON;
    }

    public IPresenter getPresenter() {
        return mPresenter;
    }

    public void setPresenter(IPresenter presenter) {
        Log.d(TAG, "setPresenter: setting");
        mPresenter = presenter;
        if(mPresenter.getClass() == GameListFragment.class) {
            ModelFacade.getInstance().updateState(DisplayState.GAMELOBBYLIST);
        }else if(mPresenter.getClass() == LobbyFragment.class) {
            ModelFacade.getInstance().updateState(DisplayState.LOBBY);
        }else if(mPresenter.getClass() == TicketPresenter.class) {
            ModelFacade.getInstance().updateState(DisplayState.GAME);
        }
    }

    public void updatePresenter(Object data) {
        if(data != null && data.getClass().equals(Result.class)) {
            Result r = (Result) data;
            if(!r.isSuccess()) {
                Log.d(TAG, "show error: " + r.getErrorInfo());
                mPresenter.showToast(r.getErrorInfo());
            }
        }
        this.mPresenter.updateInfo(data);
    }

}
