package gamePresenters;

import android.util.Log;

import com.obfuscation.ttr_phase1b.gameViews.IChatView;

import communication.Message;
import model.FakeModel;
import model.IGameModel;
import model.ModelFacade;

public class ChatPresenter implements IChatPresenter {

    private static String TAG = "tickPres";

    private IChatView view;
    private IChatPresenter.OnBackListener listener;
    private IGameModel model;

    public ChatPresenter(IChatView view, IChatPresenter.OnBackListener listener) {
        this.view = view;
        view.setPresenter(this);
        this.listener = listener;
        model = ModelFacade.getInstance();
//        model = FakeModel.newInstance();
        view.setMessages(model.getMessages());
        view.setUsername(model.getUserName());
    }

    @Override
    public void updateInfo(Object result) {
        Log.d(TAG, "updating info");
        view.setMessages(model.getMessages());
        view.setUsername(model.getUserName());
        view.updateUI();
    }

    @Override
    public void update() {
        model.updateMessages();
    }

    @Override
    public void goBack() {
        listener.onBack();
    }

    @Override
    public void onSend(Message message) {
        model.sendMessage(message);
    }

    @Override
    public void showToast(String toast) {
        view.sendToast(toast);
    }

}
