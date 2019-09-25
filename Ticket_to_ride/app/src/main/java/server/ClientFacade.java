package server;

import com.obfuscation.server.GenericCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import communication.Card;
import communication.Game;
import communication.GameClient;
import communication.GameHistory;
import communication.IClient;
import communication.IPlayer;
import communication.Message;
import communication.Player;
import communication.PlayerOpponent;
import communication.PlayerStats;
import communication.PlayerUser;
import communication.Result;
import communication.Route;
import communication.Serializer;
import communication.Ticket;
import communication.GameColor;
import model.ModelFacade;
import model.ModelRoot;
import model.DisplayState;
import task.PresenterUpdateTask;

/**
 * Created by hao on 10/25/18.
 */

public class ClientFacade implements IClient{

    //TEST
    public static void main(String[] args) {
        GenericCommand command = new GenericCommand(
                "server.ClientFacade"
                , "updateTrainCards"
                , new String[]{String.class.getName(), List.class.getName()}
                , new Object[] {"HELLO", new ArrayList<>(Arrays.asList(new Card(GameColor.BLUE), new Card(GameColor.BLACK)))});

        try {
            command.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static ClientFacade instance = new ClientFacade();
    public static ClientFacade getInstance() {
        return instance;
    }

    private ClientFacade() {

    }

    private void updatePresenter(Object o) {
        PresenterUpdateTask p = new PresenterUpdateTask();
        p.execute(o);
    }

    @Override
    public void updateGameLobbyList(String gameID) {
        //sever will never call this command
    }

    @Override
    public void updateGame(String gameID) {
        //sever will never call this command
    }

    @Override
    public void initializeGame(GameClient gameClient) {

        try {
            ArrayList<Ticket> ticketsToChoose = ModelRoot.getInstance().getGame().getPlayerUser().getTicketToChoose();
            ModelRoot.getInstance().setGame(gameClient);
            updatePresenter(gameClient);
            if (ModelRoot.getInstance().getGame().getPlayerOpponents() == null) {

//                System.out.println(ModelRoot.getInstance().getGame().getPlayerOpponents().toString());

            }
            if (ticketsToChoose != null) {
                if (ticketsToChoose.size() > 0) {

                    if (ModelRoot.getInstance().getGame() != null) {
                        if (ModelRoot.getInstance().getGame().getPlayerUser() != null) {
                            ModelRoot.getInstance().getGame().getPlayerUser().setTicketToChoose(ticketsToChoose);
                        }
                    }
                }
            }

            ModelRoot.getInstance().setDisplayState(DisplayState.GAME);
            updatePresenter(DisplayState.GAME);
            ModelFacade.getInstance().getChoiceTickets();

//            System.out.println("current turn is " + ModelRoot.newInstance().getGame().getTurnUser());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    //it is only access the current
    public void updatePlayerPoints(String gameID, String plyerID, Integer points) {
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            IPlayer p = g.getPlayerByUserName(plyerID);
            if (p != null) {
                ((Player) p).setPoint(points);
                updatePresenter((Player) p);
            }
        }
    }

    @Override
    public void updateTrainCards(String gameID, List<Card> trainCards) {
//        System.out.println("UPDATE TRAIN CARD GETTING CALLED");
//        System.out.println(trainCards.size());
        Serializer serializer = new Serializer();
        ArrayList<Card> cardD = new ArrayList<Card>();
        for(Object O: trainCards) {
            cardD.add(serializer.deserializeCard(O.toString()));
        }
        try {
            GameClient g = ModelRoot.getInstance().getGame();

            if (g != null) {
                PlayerUser p = g.getPlayerUser();
                if (p != null) {
                    p.setCards(cardD);
                    updatePresenter(p);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateTickets(String gameID, List<Ticket> tickets) {
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            PlayerUser p = g.getPlayerUser();
            if (p != null) {
                p.setTicketToChoose((ArrayList<Ticket>) tickets);
                updatePresenter(p);
            }
        }
    }

    @Override
    public void updateOpponentTrainCards(String gameID, String playerID, Integer cardNum) {
        ModelRoot m = ModelRoot.getInstance();
        PlayerOpponent p = m.getGame().getPlayerOpponentByUsername(playerID);

        System.out.println("the player is " + playerID);

        ArrayList<PlayerOpponent> pos = m.getGame().getPlayerOpponents();

//        for(PlayerOpponent po: pos) {
//            System.out.println("Player: " + po.getPlayerName());
//        }

//        if (p == null) {
//            System.out.println("player not found");
//        }
//
//        if (m.getUserName().equals(playerID)) {
//            System.out.println("it is the current user");
//        }
        p.setCardNum(cardNum);
        updatePresenter(p);
    }

    @Override
    public void updateOpponentTrainCars(String gameID, String playerID, Integer carNum) {
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            PlayerOpponent p = ModelRoot.getInstance().getGame().getPlayerOpponentByUsername(playerID);
            if (p != null) {
                p.setTrainNum(carNum);
                updatePresenter(p);
            }
        }
    }

    @Override
    public void updateOpponentTickets(String gameID, String playerID, Integer cardNum) {
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            PlayerOpponent p = ModelRoot.getInstance().getGame().getPlayerOpponentByUsername(playerID);
            if (p != null) {
                p.setTicketNum(cardNum);
                updatePresenter(p);
            }
        }
    }

    @Override
    public void updateTrainDeck(String gameID, List<Card> faceCards, Integer downCardNum) {
        GameClient g = ModelRoot.getInstance().getGame();
        Serializer serializer =  new Serializer();
        ArrayList<Card> cardD = new ArrayList<Card>();
        for(Object O: faceCards) {
            cardD.add(serializer.deserializeCard(O.toString()));
        }

        if (g != null) {
            g.setFaceUpTrainCarCards(cardD);
            updatePresenter(cardD);
            g.setTrainCardDeckSize(downCardNum);
        }
    }

    //update number of card in the deck
    @Override
    public void updateDestinationDeck(String gameID, Integer cardNum) {
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            ModelRoot.getInstance().getGame().setTicketDeckSize(cardNum);
            updatePresenter(g);
        }
    }

    @Override
    public void claimRoute(String gameID, String playerID, String routeID) {
        System.out.println("claimed route with id" + routeID);
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            Route r = g.getmMap().getRouteByRouteId(routeID);
            if (r != null) {
                r.setClaimedBy(((Player)g.getPlayerByUserName(playerID)));
                updatePresenter(new Result(true,r,null));
            }
            ((Player)g.getPlayerByUserName(playerID)).addRouteAsClaimed(routeID);
            updatePresenter(((Player)g.getPlayerByUserName(playerID)));
        }
    }

    @Override
    public void updateChat(String gameID, Message m) {
//        System.out.println("trying to insert"+ m.getText());
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            g.insertMessage(m);
            updatePresenter(g.getMessages());
//            System.out.println("inserted a new chat"+ m.getText());
        }

        g = ModelRoot.getInstance().getGame();
        if (g != null) {
//            System.out.println("message has size of:  " + g.getMessages().size());

        }
    }

    @Override
    public void updateGameHistory(String gameID, List<GameHistory> gh) {
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            Serializer serializer = new Serializer();
            g.getGameHistories().clear();
            for(Object O: gh) {
                g.addHistory(serializer.deserializeGameHistory(O.toString()));
            }
            updatePresenter(g.getGameHistories());
        }
    }

    @Override
    public void lastRound(String gameID) {
        System.out.println("last round getting called");
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
           g.setLastRound(true);
           updatePresenter(g);
        }
    }

    @Override
    public void endGame(String gameID, List<PlayerStats> stats) {
        System.out.println("end game getting called");
        GameClient g = ModelRoot.getInstance().getGame();
        g.setGameEnded(true);
        if (g != null) {
            Serializer serializer = new Serializer();
            for(Object O: stats) {
                g.addPlayerStats(serializer.deserializePlayerStats(O.toString()));
            }
            updatePresenter(g);
        }
    }

    @Override
    public void updateTurns(String gameID, String userName) {
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            g.setTurnUser(userName);

            System.out.println("set game turn to " + userName);

            if (userName.equals(ModelRoot.getInstance().getUserName())) {
                System.out.println("set to the turn of user");

            }
            else {
                System.out.println("set to other user turn");
            }
            updatePresenter(g);
        }

    }
}
