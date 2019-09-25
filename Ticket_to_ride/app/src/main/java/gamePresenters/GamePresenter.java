package gamePresenters;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.obfuscation.ttr_phase1b.gameViews.IGameView;
import com.obfuscation.ttr_phase1b.gameViews.IPlayerInfoView;

import communication.Card;
import communication.GameColor;

import java.util.ArrayList;
import java.util.List;

import communication.Player;
import communication.PlayerOpponent;
import communication.Result;
import communication.Route;
import model.IGameModel;
import model.ModelFacade;

public class GamePresenter implements IGamePresenter {

    private static String TAG = "gamePres";

    private IPlayerInfoView playerInfoView;
    private IGameView view;
    private OnShowListener listener;
    private IGameModel model;

    private ITurnState state;
    private boolean ending;

    public GamePresenter(IGameView view, OnShowListener listener) {
        this.view = view;
        view.setPresenter(this);
        this.listener = listener;
        model = ModelFacade.getInstance();
        state = new NotTurn(this);
        ending = false;
    }

    public void setState(ITurnState state){
        this.state = state;
    }

    @Override
    public void updateInfo(Object result) {
        if(!model.isMyTurn()) {
            ending = false;
        }
        if(model.isGameEnded()) {
            Log.d(TAG, "updateInfo: ending game");
            listener.onShow(Shows.score, null);
        }

        if(result instanceof Result) {
            Result r = (Result) result;
            if(r.getData() instanceof Route) {
                Log.d(TAG, "updateInfo: result is route");
                view.setMap(model.getMap());
                view.updateRoute((Route) r.getData());
            }
            state.finish(r);
        }
        view.setPlayer(model.getPlayer());
        if(model.getPlayer() == null) {
            Log.d(TAG, "user is null");
        }if(model.isMyTurn() && state.getClass().equals(NotTurn.class) && !ending) {
            setState(new TurnNoSelection(this));
        }else if(!model.isMyTurn() && !state.getClass().equals(NotTurn.class)) {
            setState(new NotTurn(this));
        }
        if(playerInfoView == null) {
            view.setLastTurn(model.isLastTurn());
            view.setTurn(model.isMyTurn());
            view.setDeckSize(model.getDeckSize());
            view.setTicketDeckSize(model.getTicketDeckSize());
            view.setCards(model.getCards());
            view.setFaceCards(model.getFaceCards());
            view.setMap(model.getMap());
            view.setTickets(model.getTickets());
            view.updateUI();

        }else {
            playerInfoView.setPlayers(model.getOpponents());
            playerInfoView.updateUI();
        }
    }

    @Override
    public void update() {
        model.updateCards();
        model.updateFaceCards();
        model.updateTickets();
    }

    @Override
    public void showToast(String toast) {
        Log.d(TAG, "showToast: " + toast);
        view.sendToast(toast);
    }

    @Override
    public void claimRoute(Route route, Player player) {
        state.claimRoute(route, player);
    }

    @Override
    public void showPlayerInfo(IPlayerInfoView view) {
        if(view == null) {
            this.listener.onShow(Shows.playerInfo, null);

        }else {
            Log.d(TAG, "showPlayerInfo: " + model.getPlayers());
            playerInfoView = view;
            playerInfoView.setPresenter(this);
            playerInfoView.setPlayers(model.getOpponents());
            playerInfoView.updateUI();
        }
    }

    @Override
    public List<PlayerOpponent> getPlayers() {
        return model.getOpponents();
    }

    @Override
    public IGameModel getModel() {
        return model;
    }

    @Override
    public void chooseCard(int index) {
//        model.chooseCard(index);
        if (index == -1) {
            state.selectDeck();
        }
        else {
            state.selectFaceUp(index);
        }
    }

    @Override
    public void onBack() {
        this.listener.onShow(Shows.map, null);
    }

    @Override
    public void onClose(Fragment fragment){

        this.listener.onClose(fragment, null);
    }

    public void showMenu() {
        this.listener.onShow(Shows.menu, null);
    }

    public void selectTickets() {
        state.selectTicketsButton();
    }

    @Override
    public void showTickets() {
        listener.onShow(Shows.owned_tickets, null);
    }

    public void showChat() {
        this.listener.onShow(Shows.chat, null);
    }

    @Override
    public void showHistory() {
        listener.onShow(Shows.history, null);
    }

    @Override
    public void sendToast(String toast) {
        Log.d(TAG, "sendToast: " + toast);
        view.sendToast(toast);
    }



    class ITurnState {
        void selectFaceUp(int index){}
        void selectDeck(){}
        void selectTicketsButton(){}
        void claimRoute(Route route, Player player){}
        void finish(Result result){}
    }

    class NotTurn extends ITurnState {
        private GamePresenter wrapper;
        NotTurn(GamePresenter wrapper) {
            this.wrapper = wrapper;
        }
    }

    class TurnNoSelection extends ITurnState {

        private GamePresenter wrapper;
        private boolean isSelectOne;
        private boolean actionSelected;

        TurnNoSelection(GamePresenter wrapper) {
            this.wrapper = wrapper;
            isSelectOne = false;
            actionSelected = false;
        }

        @Override
        void selectFaceUp(int index) {
            if(actionSelected || isSelectOne) {
                return;
            }
            Log.d(TAG, "selectFaceUp: " + model.checkCard(index));
            if (model.checkCard(index).equals(GameColor.LOCOMOTIVE)) {
                Log.d(TAG, "selectFaceUp locomotive");
                actionSelected = true;
                model.chooseCard(index);
            }
            else {
                isSelectOne = true;
                wrapper.getModel().chooseCard(index);
            }
        }

        @Override
        void selectDeck() {
            if(actionSelected || isSelectOne) {
                Log.d(TAG, "already selected");
                return;
            }
            if(model.getDeckSize() > 0) {
                Log.d(TAG, "choosing deck");
                isSelectOne = true;
                model.chooseCard(-1);
            }
            else {
                wrapper.sendToast("Deck is empty!");
            }

        }

        @Override
        public void selectTicketsButton() {
            if (model.getTicketDeckSize() > 0) {
                if(actionSelected || isSelectOne) {
                    return;
                }
                actionSelected = true;
                listener.onShow(Shows.tickets, null);
            }
        }

        @Override
        public void claimRoute(Route route, Player player) {
            if(actionSelected || isSelectOne) {
                return;
            }
            //Check if a player has sufficient cards
            Object list = model.checkRouteCanClaim(route);

            if (list instanceof String) {
                Log.d(TAG, "claimRoute: " + list);
                wrapper.sendToast((String) list);
            }
            else {
                ArrayList<Card> cardsToUse;
                if (route.getColor() == GameColor.GREY) {
                    Bundle args = new Bundle();
                    args.putSerializable("route", route);
                    args.putInt("cardsToSelect", route.getLength());
                    actionSelected = true;
                    listener.onShow(Shows.cardSelect, args);
                    return;
                }
                else {
                    cardsToUse = (ArrayList<Card>) list;
                }

                actionSelected = true;
                model.claimRoute(route, player, cardsToUse);
            }
        }

        @Override
        void finish(Result result) {
            Log.d(TAG, "TurnNoSelection: finish called");
            if(result.isSuccess()) {
                if(isSelectOne) {
                    Log.d(TAG, "TurnNoSelection: to turnOneCard");
                    wrapper.setState(new TurnOneCard(wrapper));
                    isSelectOne = false;
                }else if(actionSelected) {
                    Log.d(TAG, "TurnNoSelection: finish turn");
                    model.endTurn();
                    ending = true;
                    wrapper.setState(new NotTurn(wrapper));
                }
            }else {
                wrapper.sendToast(result.getErrorInfo());
                isSelectOne = false;
                actionSelected = false;
            }
        }
    }

    class TurnOneCard extends ITurnState {

        private GamePresenter wrapper;
        private boolean actionSelected;

        public TurnOneCard(GamePresenter wrapper) {
            this.wrapper = wrapper;
            actionSelected = false;
        }

        @Override
        public void selectFaceUp(int index) {
            if(actionSelected) {
                return;
            }
            Log.d(TAG, "selectFaceUp: " + wrapper.getModel().checkCard(index));
            if (model.checkCard(index).equals(GameColor.LOCOMOTIVE)) {
                wrapper.sendToast("You can't select a locomotive card as your second choice.");
            }
            else {
                model.chooseCard(index);
                actionSelected = true;
            }
        }

        @Override
        public void selectDeck() {
            if(actionSelected) {
                Log.d(TAG, "already selected");
                return;
            }
            if(model.getDeckSize() > 0) {
                Log.d(TAG, "choosing deck");
                wrapper.getModel().chooseCard(-1);
                actionSelected = true;
            }
            else {
                wrapper.sendToast("Deck is empty!");
            }
        }

        @Override
        void finish(Result result) {
            if(result.isSuccess()) {
                if(actionSelected) {
                    Log.d(TAG, "TurnOneCard: finish turn");
                    model.endTurn();
                    ending = true;
                    wrapper.setState(new NotTurn(wrapper));
                }
            }else {
                wrapper.sendToast(result.getErrorInfo());
                actionSelected = false;
            }
        }
    }

}