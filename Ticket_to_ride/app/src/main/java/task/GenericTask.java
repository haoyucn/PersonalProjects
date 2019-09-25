package task;

import android.os.AsyncTask;
import android.util.Log;

import com.obfuscation.ttr_phase1b.activity.PresenterFacade;

import java.util.ArrayList;
import java.util.List;

import communication.Card;
import communication.GameClient;
import communication.LobbyGame;
import communication.Message;
import communication.Result;
import communication.Serializer;
import communication.Ticket;
import model.ModelFacade;
import model.ModelRoot;
import server.CommandTask;
import server.Poller;
import server.ServerProxy;

/**
 * Created by hao on 10/3/18.
 */

public class GenericTask extends AsyncTask<Object, Void, Result> {

    private static final String TAG = "genTask";

    String action;
//    Object holder;
    public GenericTask (String action) {
        this.action = action;
    }

    @Override

    public Result doInBackground(Object... params) {
        ServerProxy serverProxy = new ServerProxy();


        switch (action) {
            case "login":
                return serverProxy.Login((String) params[0], (String) params[1]);
            case "register":
                return serverProxy.Register((String) params[0], (String) params[1]);
            case "joinLobbyGame":
                return serverProxy.JoinLobby((String) params[0], (String) params[1], (String) params[2]);
            case "leaveGame":
                return serverProxy.LeaveGame((String) params[0], (String) params[1], (String) params[2]);
            case "leaveLobbyGame":
                return serverProxy.LeaveLobbyGame((String) params[0], (String) params[1], (String) params[2]);
            case "CreateLobby":
                return serverProxy.CreateLobby((LobbyGame) params[0], (String) params[1]);
            case "startGame":
                return serverProxy.StartGame((String) params[0], (String) params[1]);
            case "GetLobbyList":
                return serverProxy.GetLobbyList((String) params[0]);
            case "GetLobby":
                return serverProxy.GetLobby((String) params[0], (String) params[1]);
            case "CheckGameList":
                return serverProxy.CheckGameList((String) params[0]);
            case "ClaimRoute":
                return serverProxy.ClaimRoute((String) params[0],(String) params[1],(List<Card>)params[2], (String) params[3]);
            case "CheckGame":
                return serverProxy.CheckGame((String) params[0], (String) params[1], (Integer)params[2]);
            case "SendMessage":
                return serverProxy.SendMessage((String) params[0], (String) params[1], (Message) params[2]);
            case "GetTickets":
                return serverProxy.GetTickets((String) params[0], (String) params[1]);
            case "ReturnTickets":
                return serverProxy.ReturnTickets((String) params[0], (String) params[1], (List<Ticket>) params[2]);
            case "DrawTrainCard":
                return serverProxy.DrawTrainCard((String) params[0], (int) params[1], (String) params[2]);
            case "EndTurn":
                return serverProxy.EndTurn((String) params[0], (String) params[1]);
            default:
                return new Result(false,null, "Invalid Request");
        }


    }

    @Override
    public void onPostExecute(Result result) {
        Log.d(TAG, "onPostExecute: " + result);
        switch (action) {
            case "login":
                OnSignIn(result);
                break;
            case "register":
                //need to update the authkey
                OnSignIn(result);
                break;
            case "GetLobbyList":
                FetchGameListFrom(result);
                break;
            case "GetLobby":
                FetchLobbyGameFrom(result);
                break;
            case "leaveLobbyGame":
                onLeaveLobbyGame(result);
                break;
            case "GetTickets":
                FetchTicketsOption(result);
                break;
            case "ChooseTicket":
                OnTickectsChoosen(result);
                break;
            case "DrawTrainCard":
                OnTrainCardDrawn(result);
                break;
            case "ClaimRoute":
                OnClaimRoute(result);
                break;
            case "EndTurn":
                OnTurnEnd(result);
                break;
            default:
                break;
        }
        PresenterFacade.getInstance().updatePresenter(result);

    }

    private void OnSignIn(Result result) {
        if (result.isSuccess()) {
            ModelRoot.getInstance().setAuthToken((String) result.getData());
            Poller.StartPoller();
        }
    }

    private void FetchGameListFrom(Result result) {
        if (result.isSuccess()) {

            ArrayList<Object> temp = (ArrayList<Object>) result.getData();
            ArrayList<LobbyGame> gameLobbies = new ArrayList<>();
            for (int i = 0;i < temp.size(); i++) {
                gameLobbies.add(new Serializer().deserializeGameLobby(temp.get(i).toString()));
            }
            ModelRoot.getInstance().setGameLobbies(gameLobbies);

        }
    }

    private void FetchLobbyGameFrom(Result result) {
        System.out.println("FETCHING LOBBY");;
        System.out.println(result.toString());
        if (result.isSuccess()) {
            ModelRoot modelRoot = ModelRoot.getInstance();
            modelRoot.setLobbyGame((LobbyGame) result.getData());
            System.out.println(modelRoot.getLobbyGame().getHost());
            GameClient gameClient = new GameClient(modelRoot.getLobbyGame().getGameID(), modelRoot.getUserName());
            modelRoot.setGame(gameClient);
        }
    }

    private void OnTickectsChoosen(Result result) {
        System.out.println(result.toString());
        if (result.isSuccess()) {
            ModelRoot.getInstance().getGame().getPlayerUser().setTickets(ModelRoot.getInstance().getTicketsWanted());
            ModelRoot.getInstance().setTicketsWanted(new ArrayList<Ticket>());
        }
        else {
            System.out.println("errow masssage is ");
            System.out.println(result.getErrorInfo());
        }
    }

    private void OnTrainCardDrawn(Result result) {
        if (result.isSuccess()) {
            ModelRoot m = ModelRoot.getInstance();
            String userName = m.getUserName();
            if (result.getData() != null) {
                Card ticketsRecieved = (Card) result.getData();
                ModelRoot.getInstance().getGame().getPlayerUser().addCard(ticketsRecieved);
                System.out.println("added card to user");
            }
        }
    }

    private void FetchTicketsOption(Result result) {
        if (result.isSuccess()) {
            Serializer serializer = new Serializer();
            ArrayList<Object> objects = (ArrayList<Object>) result.getData();

            ArrayList<Ticket> ticketsToChoose = new ArrayList<Ticket>();
            for (Object o: objects) {
                ticketsToChoose.add((Ticket) serializer.deserializeTicket(o.toString()));
            }
            ModelRoot.getInstance().getGame().getPlayerUser().setTicketToChoose(ticketsToChoose);
        }
    }

    private void OnClaimRoute(Result result) {
        if (result.isSuccess()) {

        }
    }

    private void OnTurnEnd(Result result) {
        if (result.isSuccess()) {
            System.out.println("ended user turn");
        }
        else {
            System.out.println("user turn ending failed");
        }
    }

    private void onLeaveLobbyGame(Result result) {
        if (result.isSuccess()) {
            ModelRoot.getInstance().getLobbyGame().removePlayerByID(ModelRoot.getInstance().getUserName());
        }
    }
}
