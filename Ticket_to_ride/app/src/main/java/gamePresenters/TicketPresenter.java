package gamePresenters;

import android.util.Log;

import com.obfuscation.ttr_phase1b.gameViews.ITicketView;

import java.util.List;

import communication.Ticket;
import model.FakeModel;
import model.IGameModel;
import model.ModelFacade;

public class TicketPresenter implements ITicketPresenter {

    private static String TAG = "tickPres";

    private ITicketView view;
    private OnBackListener listener;
    private IGameModel model;

    public TicketPresenter(ITicketView view, OnBackListener listener) {
        this.view = view;
        view.setPresenter(this);
        this.listener = listener;
        model = ModelFacade.getInstance();
//        model = FakeModel.newInstance();
    }

    @Override
    public void onFinish(List<Ticket> tickets) {
        Log.d(TAG, "onFinish: " + tickets);
        model.chooseTickets(tickets);
        if(!view.isGameSetup()) {
            Log.d(TAG, "finish turn");
            model.endTurn();
        }
//        model.endTurn();
        listener.onBack();
    }

    @Override
    public void updateInfo(Object result) {
        Log.d(TAG, "updating info: " + model.getChoiceTickets());
        view.setTickets(model.getChoiceTickets());
        view.updateUI();
    }

    @Override
    public void update() {
        model.updateChoiceTickets();
    }

    @Override
    public void showToast(String toast) {
        view.sendToast(toast);
    }

}
