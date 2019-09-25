package com.obfuscation.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import communication.Card;
import communication.GameClient;
import communication.LobbyGame;
import communication.GameServer;
import communication.IServer;
import communication.Message;
import communication.Player;
import communication.PlayerOpponent;
import communication.PlayerStats;
import communication.PlayerUser;
import communication.Result;
import communication.Serializer;
import communication.Ticket;
import dao.User;

/**
 * Created by jalton on 10/1/18.
 */

public class ServerFacade implements IServer {

    private static final String SERVER_FACADE = "com.obfuscation.server.ServerFacade";
    private static final String STRING = "java.lang.String";
    private static final String INTEGER = "java.lang.Integer";
    //    TODO: helps to find the typeName
    private static final String ARRAYLISTCARD = "??";
    private static final String LIST = List.class.getName();

    private Database db = Database.getInstance();
    private List<ClientProxy> clientproxies = new ArrayList<>();
    private Map<String, List<ClientProxy>> gameIDclientProxyMap = new HashMap<>();
    private static ServerFacade instance = new ServerFacade();

    public void initializeServer() {
        System.out.println("INITIALIZING SERVERFACADE");
        List<GameServer> games = db.getGameList();
        for (GameServer gameServer : games) {
            clientproxies = gameServer.getOriginalClientProxies();
            gameIDclientProxyMap.put(gameServer.getGameID(), gameServer.getClientProxies());
        }
    }

    public static ServerFacade getInstance(){
        return instance;
    }

    private void saveUpdate(String gameID, GenericCommand update) {
        db.saveCommand(gameID, update);
    }

    @Override
    public Result SendMessage(String authToken, String gameID, Message message) {
        GenericCommand genericCommand = new GenericCommand(SERVER_FACADE,
                "SendMessage", new String[]{STRING,STRING,Message.class.getName()},
                new Object[]{authToken, gameID, message});
        saveUpdate(gameID, genericCommand);

        try {
//            System.out.println("Updating the message");
            //FIXME**
            Result result = db.sendMessage(gameID, message.getText(), authToken);
//            System.out.println(result.toString());
            if (result.isSuccess()) {
                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                    clientProxy.updateChat(gameID, (Message) result.getData());
                }
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result DrawTrainCard(String gameID, Integer index, String authToken) {
        GenericCommand genericCommand = new GenericCommand(SERVER_FACADE,
                "DrawTrainCard", new String[]{STRING, INTEGER, STRING},
                new Object[]{gameID, index, authToken});
        saveUpdate(gameID, genericCommand);

        try {
            Result result = db.drawCard(gameID, index, authToken);
            if (result.isSuccess()) {
                GameServer gameServer = db.findGameByID(gameID);
                String username = db.findUsernameByAuthToken(authToken);
                PlayerUser currentPlayer = gameServer.getPlayerbyUserName(username);
                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                    if (!clientProxy.getAuthToken().equals(authToken)) {
                        clientProxy.updateOpponentTrainCards(gameID, username,
                                currentPlayer.getCardNum());
                    }
                    clientProxy.updateTrainDeck(gameID, gameServer.getFaceUpTrainCarCards(),
                            gameServer.getTrainCards().size());
                }
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    private ServerFacade() {
        //setting dummy game
        clientproxies.add(new ClientProxy("authBob"));
        clientproxies.add(new ClientProxy("authJoe"));
        gameIDclientProxyMap.put("GAME", new ArrayList<ClientProxy>());
        gameIDclientProxyMap.get("GAME").add(clientproxies.get(0));
        gameIDclientProxyMap.get("GAME").add(clientproxies.get(1));
        StartGame("GAME", "authBob");
    }

    private ClientProxy getClientProxyByAuthToken(String authToken) {
        for (ClientProxy clientProxy : clientproxies) {
            if (clientProxy.getAuthToken().equals(authToken)) {
                return clientProxy;
            }
        }
        return null;
    }

    @Override
    public Result Login(String id, String password) {
        try {
            System.out.println("On login");
            Result result = db.login(id, password);
            if (result.isSuccess()) {
                clientproxies.add(new ClientProxy((String) result.getData()));
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result Register(String id, String password) {
        try {
            Result result = db.register(id, password);
            if (result.isSuccess()) {
                clientproxies.add(new ClientProxy((String) result.getData()));
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result JoinLobby(String id, String gameID, String authToken) {
        try {
            if (!db.checkAuthToken(authToken, id)) {
                return new Result(false, null,
                        "Error: Invalid authorization");
            }
            Result result = db.joinGame(id, gameID);
            if (result.isSuccess()) {
                for (ClientProxy clientProxy : clientproxies) {
                    clientProxy.updateGameLobbyList(null);
                }

                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                    clientProxy.updateGame(gameID);
                }
                gameIDclientProxyMap.get(gameID).add(getClientProxyByAuthToken(authToken));
                System.out.println("JOINING GAME : " + gameIDclientProxyMap.get(gameID).size());
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result LeaveGame(String id, String gameID, String authToken) {
        return null;
    }

    @Override
    public Result LeaveLobbyGame(String id, String gameID, String authToken) {
        try {
            if (!db.checkAuthToken(authToken, id)) {
                return new Result(false, null,
                        "Error: Invalid authorization");
            }

            Result result = db.leaveGame(gameID, id);
            if (result.isSuccess()) {
                for (ClientProxy clientProxy : clientproxies) {
                    clientProxy.updateGameLobbyList(null);
                }
                gameIDclientProxyMap.get(gameID).remove(getClientProxyByAuthToken(authToken));
                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                    clientProxy.updateGame(gameID);
                }
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result CreateLobby(LobbyGame lobbyGame, String authToken) {
        try {
//            System.out.println(authToken + " EEE");
            Result result = db.newGameLobby(lobbyGame, authToken);
//            System.out.println("WHAT IS " + result.toString());
            if (result.isSuccess()) {
                for (ClientProxy clientProxy : clientproxies) {
                    clientProxy.updateGameLobbyList(lobbyGame.getGameID());
                }
                gameIDclientProxyMap.put(lobbyGame.getGameID(), new ArrayList<>());
                System.out.println("AUTH TOKEN " + authToken);
                gameIDclientProxyMap.get(lobbyGame.getGameID())
                        .add(getClientProxyByAuthToken(authToken));
            }
            System.out.println("CREATE " + result.toString());
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result StartGame(String gameID, String authToken) {
        try {
            Result result = db.startGame(gameID, authToken);
            if (result.isSuccess()) {
                GameServer game = (GameServer) result.getData();
                for (ClientProxy clientProxy : clientproxies) {
                    clientProxy.updateGameLobbyList(game.getGameID());
                }

                //setup game
                db.setupGame(gameID);
                ArrayList<Object> objects = new ArrayList<>();
                game = db.findGameByID(gameID);
                game.getClientProxies().addAll(gameIDclientProxyMap.get(gameID));
                game.getOriginalClientProxies().addAll(clientproxies);
                objects.add(game);
                System.out.println("STARTING GAME : " + gameIDclientProxyMap.get(gameID).size());
                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                    System.out.println("UPDATING : " + clientProxy.getAuthToken());
                    System.out.println(db.findUsernameByAuthToken(clientProxy.getAuthToken()));
                    clientProxy.updateGame(gameID);
                    clientProxy.updateGame(gameID);
                    String username = db.findUsernameByAuthToken(clientProxy.getAuthToken());

                    //initalize game TODO : SET GAMECLIENTS
                    GameClient gameClient = new GameClient();
                    gameClient.setGameID(gameID);
                    gameClient.setPlayerUser(game.getPlayerbyUserName(username));
                    gameClient.setTrainCardDeckSize(game.getTrainCards().size());
                    gameClient.setFaceUpTrainCarCards(game.getFaceUpTrainCarCards());
                    gameClient.setTicketDeckSize(game.getTickets().size());
                    gameClient.setTurnUser(game.getCurrentPlayer());
                    //initialize user player
                    PlayerUser playerUser = new PlayerUser(username);
                    ArrayList<PlayerOpponent> opponents = new ArrayList<>();

                    //initialize opponents
                    for (ClientProxy clientProxy1 : gameIDclientProxyMap.get(gameID)) {
                        if (!clientProxy.getAuthToken().equals(clientProxy1.getAuthToken())) {
                            //FIXME for testing
                            PlayerOpponent playerOpponent = new PlayerOpponent(
                                    db.findUsernameByAuthToken(clientProxy1.getAuthToken()),
                                    0, 5, 4);

                            String opponent = db.findUsernameByAuthToken(
                                    clientProxy1.getAuthToken());
                            for (PlayerUser p : game.getPlayers()) {
                                if (p.getPlayerName().equals(opponent)) {
                                    playerOpponent.setPlayerColor(p.getPlayerColor());
                                }
                            }
                            opponents.add(playerOpponent);
                        }
                    }
                    gameClient.setPlayerOpponents(opponents);

                    objects.add(gameClient);
                    clientProxy.initializeGame(gameClient);

                    db.saveGameState(gameID);

                    //add command to initialize
                }
                return new Result(true, objects, null);
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    /**
     * client calls this method to fetch unprocessed commands from the server.
     * @param authToken a String object containing the user's authToken
     * @param gameID    a String object containing the gameID of the game to be started
     * @param state     an Integer that record the number of command has been processed
     * @return
     */
    @Override
    public Result GetUpdates(String authToken, String gameID, Integer state) {
        try {
            for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                if (clientProxy.getAuthToken().equals(authToken)) {
                    return new Result(true, clientProxy.getNotSeenCommands(gameID, state),
                            null);
                }
            }
            return new Result(false, null, "Error : Client not found");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result GetLobbyList(String authToken) {
        try {
            return new Result(true, db.getLobbyList(), null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result GetLobby(String gameID, String authToken) {
        try {
            return new Result(true, db.findGameLobbyByID(gameID), null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result CheckGameList(String authToken){
        try {
//            System.out.println("User Checking gamelist");
//            System.out.println(authToken);
            Result result = null;
            for (ClientProxy clientProxy : clientproxies) {
                if (clientProxy.getAuthToken().equals(authToken)) {
                    result = clientProxy.checkUpdates(null);
                }
            }
//            System.out.println("EE");
//            System.out.println(result.toString());
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result CheckGame(String authToken, String gameID, Integer state) {
        try {
            List<ClientProxy> proxies = gameIDclientProxyMap.get(gameID);
            if (proxies == null) {
                return new Result(false, null, "Error : Client not found");
            }
            for (ClientProxy clientProxy : proxies) {
                if (clientProxy.getAuthToken().equals(authToken)) {
                    return clientProxy.getNotSeenCommands(gameID, state);
                }
            }
            return new Result(false, null, "Error : Client not found");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result CheckGameLobby(String authToken, String gameID) {
        try {
//            System.out.println("User checking gsame");
            Result result = null;
            for (ClientProxy clientProxy : clientproxies) {
//                System.out.println(clientProxy.getAuthToken());
//                System.out.println(authToken);
//                System.out.println("^^^^^^^^^");
                if (clientProxy.getAuthToken().equals(authToken)) {
                    result = clientProxy.checkUpdates(gameID);
                }
            }

//            System.out.println("With isSuccess" + result.isSuccess());
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result ClaimRoute(String gameID, String routeID, List<Card> cards, String authToken) {
        GenericCommand genericCommand = new GenericCommand(SERVER_FACADE, "ClaimRoute",
                new String[]{STRING, STRING, LIST, STRING},
                new Object[]{gameID, routeID, cards, authToken});
        saveUpdate(gameID, genericCommand);

        ArrayList<Card> realCards = new ArrayList<>();
        Serializer serializer = new Serializer();

        for(Object o : cards) {
            realCards.add(serializer.deserializeCard(o.toString()));
        }

        try {
            Result result = db.claimRoute(gameID, routeID, realCards, authToken);
            if (result.isSuccess()) {
                String username = db.findUsernameByAuthToken(authToken);
                GameServer gameServer = db.findGameByID(gameID);
                PlayerUser currentPlayer = gameServer.getPlayerbyUserName(username);
                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {

                    //update opponents points
                    clientProxy.claimRoute(gameID, username, routeID);
                    clientProxy.updatePlayerPoints(gameID, username, currentPlayer.getPoint());
                    clientProxy.updateTrainDeck(gameID, gameServer.getFaceUpTrainCarCards(),
                            gameServer.getTrainCards().size());

                    if (!clientProxy.getAuthToken().equals(authToken)) {
                        clientProxy.updateOpponentTrainCards(gameID, username,
                                currentPlayer.getCardNum());
                        clientProxy.updateOpponentTrainCars(gameID, username,
                                currentPlayer.getTrainNum());
                    }

                    //update last round if necessary
                    if (gameServer.isLastRound()) {
                        clientProxy.lastRound(gameID);
                    }
                }
            }
            //TODO : what to return?
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result GetTickets(String gameID, String authToken) {
        GenericCommand genericCommand = new GenericCommand(SERVER_FACADE, "GetTickets",
                new String[]{STRING, STRING}, new Object[]{gameID, authToken});
        saveUpdate(gameID, genericCommand);

        try {
            //return the tickets to the clients
            return new Result(true, db.getTickets(gameID, authToken), null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    //
    @Override
    public Result ReturnTickets(String gameID, String authToken, List<Ticket> ticketsToKeep) {
        GenericCommand genericCommand = new GenericCommand(SERVER_FACADE,
                "ReturnTickets", new String[]{STRING,STRING,LIST},
                new Object[]{gameID, authToken,ticketsToKeep});
        saveUpdate(gameID, genericCommand);

        try {
            Serializer serializer = new Serializer();
            ArrayList<Ticket> ticketsToChoose2 = new ArrayList<>();
            for (Object o : ticketsToKeep) {
                ticketsToChoose2.add((Ticket) serializer.deserializeTicket(o.toString()));
            }

            Result result = db.setTickets(gameID, authToken, ticketsToChoose2);
            String playerID = db.findUsernameByAuthToken(authToken);
            if (result.isSuccess()) {
                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                    clientProxy.updateOpponentTickets(gameID, playerID,
                            new Integer(((ArrayList<Ticket>) result.getData()).size()));
                    clientProxy.updateDestinationDeck(gameID,
                            new Integer(db.findGameByID(gameID).getTickets().size()));
                }
            }
            return new Result(true, true, null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    @Override
    public Result EndTurn(String gameID, String authToken) {
        GenericCommand genericCommand = new GenericCommand(SERVER_FACADE, "EndTurn",
                new String[]{STRING,STRING}, new Object[]{gameID, authToken});
        saveUpdate(gameID, genericCommand);

//        System.out.println("get end turn command");
        // Ending turn
        try {
            //if last player of last round ended, end the game
            GameServer gameServer = db.findGameByID(gameID);
            String username = db.findUsernameByAuthToken(authToken);

            //if final move, end game
            if (gameServer.isLastRound() && gameServer.getLastRoundTriggeredBy() != null
                    && gameServer.getLastRoundTriggeredBy().equals(username)) {
                System.out.println("FINAL SCORE GETTING CALLED");
                ArrayList<PlayerStats> finalScores = db.getGameResult(gameID);
                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                    clientProxy.endGame(gameID, finalScores);
                }
                return new Result(true, true, null);
            }

            //if the tran car number is less than 3, last round
            if (gameServer.getPlayerbyUserName(username).getTrainNum() < 3
                    && !gameServer.isLastRound()) {
                System.out.println("LAST ROUND GETTING CALLED");
                gameServer.setLastRound(true);
                gameServer.setLastRoundTriggeredBy(username);
                for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                    clientProxy.lastRound(gameID);
                }
            }

            gameServer.moveToNextTurn();
            for (ClientProxy clientProxy : gameIDclientProxyMap.get(gameID)) {
                System.out.println("CURRENT PLAYER " + gameServer.getCurrentPlayer());
                clientProxy.updateTurns(gameID, gameServer.getCurrentPlayer());
                String temp = gameServer.getCurrentPlayer();
                //update game history here
                clientProxy.updateGameHistory(gameID, gameServer.getGameHistories());
            }
            return new Result(true, gameServer.getCurrentPlayer(), null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    //--------------------------------FOR TEST-------------------------------------------
    public Result GetLobbyList() {
        try {
            return new Result(true, db.getLobbyList(), null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result GetLobby(String gameID) {
        try {
            if (db.findGameLobbyByID(gameID) == null) {
                return new Result(false, null, "lobby doesn't exist");
            }
            else {
                return new Result(true, db.findGameLobbyByID(gameID), null);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result GetGameList() {
        try {
            return new Result(true, db.getGameList(), null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result GetGame(String gameID) {
        try {
            if (db.findGameLobbyByID(gameID) == null) {
                return new Result(false, null, "game doesn't exist");
            }
            else {
                return new Result(true, db.findGameByID(gameID), null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
