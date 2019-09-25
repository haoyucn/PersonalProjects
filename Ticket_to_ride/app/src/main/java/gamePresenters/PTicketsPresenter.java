package gamePresenters;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.obfuscation.ttr_phase1b.gameViews.IPTicketsView;

import communication.Result;
import model.IGameModel;
import model.ModelFacade;

public class PTicketsPresenter implements IPTicketsPresenter {

    private static String TAG = "pticksPres";

    private IPTicketsView view;
    private OnCloseListener listener;
    private IGameModel model;
    private IGamePresenter prevPresenter;

    public PTicketsPresenter(IPTicketsView view, OnCloseListener listener, IGamePresenter prevPresenter) {
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
        if(result != null && (result instanceof Result)) {
            Result r = (Result) result;
            if (!r.isSuccess()) {
                Log.d(TAG, "updateInfo: " + r.getErrorInfo());
                view.sendToast(r.getErrorInfo());
            }
        }
        Log.d(TAG, "updateInfo: ");
        view.setTickets(model.getTickets());
        view.updateUI();
        if(prevPresenter != null) {
            prevPresenter.updateInfo(result);
        }
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

}
