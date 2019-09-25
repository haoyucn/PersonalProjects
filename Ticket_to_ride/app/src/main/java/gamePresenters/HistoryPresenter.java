package gamePresenters;

import android.util.Log;

import com.obfuscation.ttr_phase1b.gameViews.IHistoryView;

import communication.Result;
import model.IGameModel;
import model.ModelFacade;

public class HistoryPresenter implements IHistoryPresenter {

    private static String TAG = "histPres";

    private IHistoryView view;
    private OnBackListener listener;
    private IGameModel model;

    public HistoryPresenter(IHistoryView view, OnBackListener listener) {
        this.view = view;
        view.setPresenter(this);
        this.listener = listener;
        model = ModelFacade.getInstance();
    }

    @Override
    public void onBack() {
        listener.onBack();
    }

    @Override
    public void updateInfo(Object result) {
        if(result != null && (result instanceof Result)) {
            Result r = (Result) result;
            if (!r.isSuccess()) {
                Log.d(TAG, "updateInfo; is not success: " + r.getErrorInfo());
                view.sendToast(r.getErrorInfo());
            }
        }
        Log.d(TAG, "updateInfo: ");
        view.setHistory(model.getGameHistory());
        view.updateUI();
    }

    @Override
    public void update() {
    }

    @Override
    public void showToast(String toast) {
        view.sendToast(toast);
    }
}
