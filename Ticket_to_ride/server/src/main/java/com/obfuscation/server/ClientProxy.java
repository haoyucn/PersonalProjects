package com.obfuscation.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import communication.Card;
import communication.Game;
import communication.GameClient;
import communication.GameHistory;
import communication.IClient;
import communication.ICommand;
import communication.Message;
import communication.PlayerStats;
import communication.Result;
import communication.Ticket;

/**
 * Created by jalton on 10/1/18.
 */

public class ClientProxy implements IClient {
    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ClientProxy(String authToken) {
        version = 0;
        gameVersion = new HashMap<>();
        this.authToken = authToken;
    }

    /**
     * private member and class name strings
     */
    private Map<String, List<GenericCommand>> notSeenCommands = new HashMap<>();
    private Map<String, Integer> gameStateMap = new HashMap<>();
    private static final String CLIENT_FACADE = "server.ClientFacade";
    private static final String STRING = "java.lang.String";
    private static final String INTEGER = "java.lang.Integer";
    private static final String CARD = Card.class.getName();
    private static final String TICKET = Ticket.class.getName();
    private static final String MESSAGE = Message.class.getName();
    private static final String GAMECLIENT = GameClient.class.getName();
    private static final String LIST = List.class.getName();

    /**
     * getters and setters
     * @return
     */

    @Override
    public void initializeGame(GameClient game) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "initializeGame"
                , new String[]{GAMECLIENT}
                , new Object[] {game});
//        System.out.println("GET HERE");
        notSeenCommands.put(game.getGameID(), new ArrayList<GenericCommand>());
        notSeenCommands.get(game.getGameID()).add(command);
    }

    @Override
    public void updatePlayerPoints(String gameID, String plyerID, Integer points) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updatePlayerPoints"
                , new String[]{STRING, STRING, INTEGER}
                , new Object[] {gameID, plyerID, points});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateTrainCards(String gameID, List<Card> trainCards) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateTrainCards"
                , new String[]{STRING, LIST}
                , new Object[] {gameID, trainCards});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateTickets(String gameID, List<Ticket> tickets) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateTickets"
                , new String[]{STRING, LIST}
                , new Object[] {gameID, tickets});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateOpponentTrainCards(String gameID, String playerID, Integer cardNum) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateOpponentTrainCards"
                , new String[]{STRING, STRING, INTEGER}
                , new Object[] {gameID, playerID, cardNum});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateOpponentTrainCars(String gameID, String playerID, Integer carNum) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateOpponentTrainCars"
                , new String[]{STRING, STRING, INTEGER}
                , new Object[] {gameID, playerID, carNum});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateOpponentTickets(String gameID, String playerID, Integer cardNum) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateOpponentTickets"
                , new String[]{STRING, STRING, INTEGER}
                , new Object[] {gameID, playerID, cardNum});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateTrainDeck(String gameID, List<Card> faceCards, Integer downCardNum) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateTrainDeck"
                , new String[]{STRING, LIST, INTEGER}
                , new Object[] {gameID, faceCards, downCardNum});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateDestinationDeck(String gameID, Integer cardNum) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateDestinationDeck"
                , new String[]{STRING, INTEGER}
                , new Object[] {gameID, cardNum});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void claimRoute(String gameID, String playerID, String routeID) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "claimRoute"
                , new String[]{STRING, STRING, STRING}
                , new Object[] {gameID, playerID, routeID});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateChat(String gameID, Message m) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateChat"
                , new String[]{STRING, MESSAGE}
                , new Object[] {gameID, m});
        notSeenCommands.get(gameID).add(command);
//        System.out.println("UPDATING CHAT : " + notSeenCommands.get(gameID).size());
    }

    @Override
    public void updateGameHistory(String gameID, List<GameHistory> gh) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateGameHistory"
                , new String[]{STRING, LIST}
                , new Object[] {gameID, gh});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void lastRound(String gameID) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "lastRound"
                , new String[]{STRING}
                , new Object[] {gameID});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void endGame(String gameID, List<PlayerStats> stats) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "endGame"
                , new String[]{STRING, LIST}
                , new Object[] {gameID, stats});
        notSeenCommands.get(gameID).add(command);
    }

    @Override
    public void updateTurns(String gameID, String userName) {
        GenericCommand command = new GenericCommand(
                CLIENT_FACADE
                , "updateTurns"
                , new String[]{STRING, STRING}
                , new Object[] {gameID, userName});
        notSeenCommands.get(gameID).add(command);
    }

    private int version;
    private Map<String, Integer> gameVersion;

    public ClientProxy() {
        version = 0;
        gameVersion = new HashMap<>();
    }

    @Override
    public void updateGameLobbyList(String gameID) {
        version++;
        if (gameID != null) gameVersion.put(gameID, 0);
    }

    @Override
    public void updateGame(String gameID) {
        int v = gameVersion.get(gameID);
        v += 1;
        gameVersion.put(gameID, v);
    }

    public Result checkUpdates(String gameID){
        if (gameID == null) {
            return new Result(true, version, null);
        }
        else {
            if (gameVersion.get(gameID) == null) {
                return new Result(false, null, "Error: Game not found");
            }
            else return new Result(true, gameVersion.get(gameID), null);
        }
    }
    //TODO : when the game ends, clear the commands list and erase the key


    //TODO : provide a way to check if commands are transmitted successfully or not
    //TODO : clients have to keep track of games and the last command id, and send them (keep map<gameID, commandID>)
    public Result getNotSeenCommands(String gameID, Integer state) {
        try {
//            System.out.println("state come in as: " + state.toString());
            if (notSeenCommands != null) {
                if (notSeenCommands.get(gameID) != null) {
//                    System.out.println("HAHA : " + notSeenCommands.get(gameID).size());
//                    for (int i = 0; i < notSeenCommands.get(gameID).size(); i++) {
//                        System.out.println(notSeenCommands.get(gameID).);
//                    }
                    if (state < notSeenCommands.get(gameID).size()) {
                        List<GenericCommand> commands = new ArrayList<GenericCommand>(notSeenCommands.get(gameID).subList(state, notSeenCommands.get(gameID).size()));
                      //  System.out.println(commands.size() + " AAAA");
                     //   System.out.println("not null at least once");
                        ArrayList<GenericCommand> g = new ArrayList<GenericCommand>();
                        for (GenericCommand c : commands) {
                           // System.out.println("COMMANDS GETTING ADDED");
//                            System.out.println(c.methodName);
                            //System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                            g.add((GenericCommand) c);
                        }
                        return new Result(true, g, null);
                    } else {
                        return new Result(true, new ArrayList<>(), null);
                    }
                }
                else {
                    return new Result(true, new ArrayList<>(), null);
                }
            }
            return new Result(false, null, "Error : commands are null");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
