package com.obfuscation.server;

import java.lang.reflect.Array;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import communication.ActiveUser;
import communication.Card;
import communication.City;
import communication.DualRoute;
import communication.Game;
import communication.GameFactory;
import communication.GameHistory;
import communication.LobbyGame;
import communication.GameServer;
import communication.MapGraph;
import communication.Message;
import communication.Player;
import communication.PlayerStats;
import communication.PlayerUser;
import communication.Result;
import communication.Route;
import communication.TickectMaker;
import communication.Ticket;
import communication.GameColor;
import dao.DAOFacade;
import dao.User;

import static communication.GameColor.GREY;

/**
 * Created by jalton on 10/3/18.
 * Database class that stores information such as gameVersion
 */

public class Database {
    private Map<String, String> loginInfo = new HashMap<>();
    //TODO : make gameid and gamelobbyid same
    private List<LobbyGame> lobbyGameList = new ArrayList<>();
    private List<GameServer> gameList = new ArrayList<>();

    /**
     * gameUpdates stores the list of commands that we save.
     * This list is expanded and saved in the following commands:
     * sendMessage()
     *
     */
    private Map<String, List<GenericCommand>> gameUpdates = new HashMap<>();
    private int updateDelta;

    public int getUpdateDelta() {
        return updateDelta;
    }

    public void setUpdateDelta(int updateDelta) {
        this.updateDelta = updateDelta;
    }

    private HashMap<Integer, Integer> routeScores = new HashMap<>();
    private HashMap<String, MapGraph> gameGraph = new HashMap<>();

    public List<GameServer> getGameList() {
        return gameList;
    }

    public void setGameList(List<GameServer> gameList) {
        this.gameList = gameList;
        initGameUpdates();
    }

    private void initGameUpdates() {
        for (int i = 0; i < gameList.size(); i++) {
            List<GenericCommand> newList = new ArrayList<>();
            gameUpdates.put(gameList.get(i).getGameID(), newList);
        }
    }

    private List<ActiveUser> activeUsers;
    private HashMap<String, String> authTokenMap = new HashMap<>();
    public List<User> users;

    public void initializeDatabase(int commandNum) {
        updateDelta = commandNum;
        lobbyGameList = DAOFacade.getInstance().getLobbies();
        gameList = DAOFacade.getInstance().getGames();
        for (int i = 0; i < gameList.size(); i++) {
            List<GenericCommand> newList = new ArrayList<>();
            gameUpdates.put(gameList.get(i).getGameID(), newList);
        }
        users = DAOFacade.getInstance().getUsers();

        for (User user : users) {
            authTokenMap.put(user.getId(), user.getAuthtoken());
            loginInfo.put(user.getId(), user.getPassword());
        }
        ServerFacade.getInstance().initializeServer();

        List<GenericCommand> commands = DAOFacade.getInstance().getCommands();
        for (GenericCommand genericCommand : commands) {
            genericCommand.execute();
        }




    }

    public void setDummyGame() {
        try {
            newGameLobby(new LobbyGame("Bob", "GAME", 3), "authBob");
            joinGame("Joe", "GAME");
            System.out.println("#####");
           // startGame("GAME", "authBob");
            //setupGame("GAME");
            System.out.println("####@");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<GameColor> colors = Arrays.asList(GameColor.PLAYER_BLACK, GameColor.PLAYER_BLUE, GameColor.PLAYER_PURPLE, GameColor.PLAYER_RED, GameColor.PLAYER_YELLOW);

    public List<LobbyGame> getLobbyList() {
        return lobbyGameList;
    }

    public List<ActiveUser> getActiveUsers() {
        return activeUsers;
    }

    private static Database db = new Database();

    public static Database getInstance() {
        return db;
    }

    private Database() {
        loginInfo = new HashMap<>();
        lobbyGameList = new ArrayList<>();
        activeUsers = new ArrayList<>();
        authTokenMap = new HashMap<>();
//        authTokenMap.put("Bob", "authBob");
//        authTokenMap.put("Joe", "authJoe");
//        loginInfo.put("Bob", "password");
//        loginInfo.put("Joe", "password");
//        login("Bob", "password");
//        login("Joe", "password");

        //setting route scores
        routeScores.put(1, 1);
        routeScores.put(2, 2);
        routeScores.put(3, 4);
        routeScores.put(4, 7);
        routeScores.put(5, 10);
        routeScores.put(6, 15);
        //setDummyGame();
    }

    Result register(String id, String password){
        if(id == null || id.equals("")) return new Result(false, null, "Error: Invalid username (cannot be blank)");
        if(password == null || password.equals("")) return new Result(false, null, "Error: Invalid password (cannot be blank");

        if(!loginInfo.containsKey(id)){
            //Add user and password
            loginInfo.put(id, password);
            String authToken = generateAuthToken();
            authTokenMap.put(id, authToken);
            DAOFacade.getInstance().addUser(id, password, authToken);
            return login(id, password);
        }
        else{
            return new Result(false, null, "Error: Username already exists");
        }
    }

    Result login(String id, String password){
        if(loginInfo.containsKey(id)){
            if(loginInfo.get(id).equals(password)){
                //Generate authToken
                String authToken = generateAuthToken();
                if (!id.equals("Bob") && !id.equals("Joe")) {
                    authTokenMap.put(id, authToken);
                }
                //Add Player to Active Users
                if(findUserByID(id) != null) {
                    ActiveUser user = findUserByID(id);
                    user.setAuthToken(authToken);
                }
                else {
                    //TODO : ?
                    Player player = new Player(id);
                    ActiveUser user = new ActiveUser(player, authToken);
                    activeUsers.add(user);
                }
                //Create and return result object
                System.out.println("LOGGED IN " + authTokenMap.get(id));
                DAOFacade.getInstance().updateAuthToken(id, authToken);
                return new Result(true, authTokenMap.get(id), null);
            }
            else{
                return new Result(false, null, "Error: Invalid password");
            }
        }
        else {
            return new Result(false, null, "Error: Invalid username");
        }
    }

    Result newGameLobby(LobbyGame lobbyGame, String authToken){
        boolean valid = false;
        String errorInfo = null;
        if(lobbyGame == null) {
            errorInfo = "Error: Game is null";
        }
        else if(lobbyGame.getGameID() == null || lobbyGame.getGameID().equals("")) {
            errorInfo = "Error: Invalid game name (cannot be blank)";
        }
        else if(findGameLobbyByID(lobbyGame.getGameID()) != null) {
            errorInfo = "Error: Game name must be unique";
        }
        else if (lobbyGame.getHost() == null || lobbyGame.getHost().equals("")) {
            errorInfo = "Error: Invalid host name (cannot be blank)";
        }
        else if (lobbyGame.getPlayers() == null) {
            errorInfo = "Error: Invalid Player List";
        }
        else if(lobbyGame.getMaxPlayers() < 2 || lobbyGame.getMaxPlayers() > 5){
            errorInfo = "Error: Invalid max players";
        }
        else valid = true;
        if(!valid) return new Result(valid, null, errorInfo);
        //check the userID
        String userID = lobbyGame.getHost();
        System.out.println(authToken);
        System.out.println(userID);

        if(!checkAuthToken(authToken, userID)) return new Result(false, null, "Error: Invalid Token");

        ActiveUser user = findUserByID(userID);
        if (user == null) return new Result(false, null, "Error: Invlaid user");
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(user.getPlayer());
        lobbyGame.setPlayers(playerList);

        lobbyGameList.add(lobbyGame);
        DAOFacade.getInstance().addLobby(lobbyGame.getGameID(), lobbyGame);
        return new Result(true, lobbyGameList, null);
    }

    Result joinGame(String playerID, String gameID) {
        ActiveUser user = findUserByID(playerID);
        LobbyGame game = findGameLobbyByID(gameID);

        if(user == null || game == null) {
            return new Result(false, null, "Error: Could not join game");
        }

        Player player = user.getPlayer();
        if (game.isStarted()) {
            if (game.getPlayers().contains(player)) {
                return rejoinGame(gameID, playerID);
            } else return new Result(false, null, "Error: game has started");
        }
        else if (game.getPlayers().size() < game.getMaxPlayers()) {
            if (!game.getPlayers().contains(player)) {
                game.getPlayers().add(player);
                DAOFacade.getInstance().updateLobby(gameID, game);
                return new Result(true, true, null);
            }
            else return new Result(false, null, "Error: player already in game");
        }
        else return new Result(false, null, "Error: game is full");
    }

    void setupGame(String gameID) {
        GameServer gameServer = findGameByID(gameID);

        //initialize traincards
        ArrayList<Card> trainCards = new ArrayList<>();

        //FIXME * should be 12, just reducing the number for debugging
        //FIXME should be 14
        for (int i = 0; i < 14; i++) {
            Card locomotiveCard = new Card(GameColor.LOCOMOTIVE);
            trainCards.add(locomotiveCard);
        }
        for (int i = 0; i < 12; i++) {
            Card purpleCard = new Card(GameColor.PURPLE);
            Card blueCard = new Card(GameColor.BLUE);
            Card orangeCard = new Card(GameColor.ORANGE);
            Card whiteCard = new Card(GameColor.WHITE);
            Card greenCard = new Card(GameColor.GREEN);
            Card redCard = new Card(GameColor.RED);
            Card blackCard = new Card(GameColor.BLACK);
            Card yellowCard = new Card(GameColor.YELLOW);
            trainCards.add(purpleCard);
            trainCards.add(blueCard);
            trainCards.add(orangeCard);
            trainCards.add(whiteCard);
            trainCards.add(greenCard);
            trainCards.add(redCard);
            trainCards.add(blackCard);
            trainCards.add(yellowCard);
        }

        Collections.shuffle(trainCards);
        gameServer.setTrainCards(trainCards);

        System.out.println("TRAIN CARD SIZE " + trainCards.size());

        ArrayList<Ticket> tickets = new ArrayList<>();
        //initialize destTickets
        tickets = GameFactory.getAllTickets();
        System.out.println("TICKET SIZE : " + tickets.size());

        //FIXME just for debugging. Should be erased later.
//        tickets = new ArrayList<> (tickets.subList(0, 20));

        Collections.shuffle(tickets);

        //set the deck
        gameServer.setTickets(tickets);

        //set player train cards
        for (PlayerUser player : gameServer.getPlayers()) {
            ArrayList<Card> playerTrainCards = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Card card = trainCards.get(0);
                playerTrainCards.add(card);
                trainCards.remove(0);
            }
            player.setCards(playerTrainCards);
            System.out.println(player.getCards().size());
        }

        //set faceuptrain cards

        //If 3 are locomotives, then shuffle again
        ArrayList<Card> faceUpTrainCards = null;
        while (true) {
            faceUpTrainCards = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Card card = trainCards.get(0);
                faceUpTrainCards.add(card);
                trainCards.remove(0);
            }

            int counter = 0;
            for (int i = 0; i < 5; i++) {
                System.out.println(faceUpTrainCards.get(i).getColor());
                if (faceUpTrainCards.get(i).getColor() == GameColor.LOCOMOTIVE) {
                    counter++;
                }
            }
            if (counter >= 3) {
                //if locomotive is more than 3, discard them and shuffle again
                gameServer.getDiscardDeck().addAll(faceUpTrainCards);
                faceUpTrainCards.clear();
            }
            else {
                break;
            }
        }

//        //set tickets
//        //FIXME FOR TESTING
//        tickets = gameServer.getTickets();
//        for (PlayerUser p : gameServer.getPlayers()) {
//            ArrayList<Ticket> playerTickets = new ArrayList<>();
//            if (tickets.size() < 3) {
//                playerTickets =  tickets;
//            }
//            for (int i = 0; i < 3; i++) {
//                Ticket ticket = tickets.get(0);
//                playerTickets.add(ticket);
//                tickets.remove(0);
//            }
//            p.setTickets(playerTickets);
//        }

        System.out.println("FINAL DECK SIZE " + gameServer.getTrainCards().size());
        // set faceup cards
        gameServer.setFaceUpTrainCarCards(faceUpTrainCards);

        gameServer.setTrainCards(trainCards);

    }
    Result startGame(String gameID, String authToken) {
        LobbyGame lobbyGame = findGameLobbyByID(gameID);
        if (lobbyGame == null) return new Result(false, null, "Error: lobbyGame not found");

        if(!checkAuthToken(authToken, lobbyGame.getHost())) {
            return new Result(false, null, "Error: Invalid token");
        }

        if(lobbyGame.getPlayers().size() < 2) return new Result(false, null, "Error: Cannot start a game with less than 2 players");
        GameServer game = new GameServer();

        if (findGameByID(gameID) != null) return new Result(false, null, "Error : already started");
        game.setGameID(gameID);

        //lobby game is started
        findGameLobbyByID(gameID).setStarted(true);

        //set players
        ArrayList<PlayerUser> players = new ArrayList<>();
        for (Player p : lobbyGame.getPlayers()) {
            players.add(new PlayerUser(p.getPlayerName()));
        }
        game.setPlayers(players);
        game.setCurrentPlayer(players.get(0).getPlayerName());

        //TODO : NEED THIS?
//        for (Player player : players) {
//            ActiveUser user = findUserByPlayer(player);
//            if (user == null) return new Result(false, null, "Error: User not found");
//
//            user.getJoinedGames().add(gameID);
//        }



        //Assign PlayerColors
        Collections.shuffle(colors);
        for (int i = 0; i < game.getPlayers().size(); i++) {
            game.getPlayers().get(i).setPlayerColor(colors.get(i));
        }

        gameList.add(game);
        ArrayList<GenericCommand> updateList = new ArrayList<>();
        gameUpdates.put(gameID, updateList);

        // database stuff
        DAOFacade.getInstance().removeLobby(lobbyGame.getGameID());
        DAOFacade.getInstance().addGame(gameID, game);
        saveUpdates(gameID, updateList);

        gameGraph.put(gameID, new MapGraph());
        return new Result(true, game, null);
    }

    Result leaveGame(String gameID, String playerID) {
        ActiveUser user = findUserByID(playerID);
        LobbyGame game = findGameLobbyByID(gameID);

        if(user == null || game == null) {
            return new Result(false, null, "Error: Could not leave game");
        }
        Player player = user.getPlayer();
        if (!game.getPlayers().contains(player)) return new Result(false, false, "Error: Player not in game");

        //update host
        if (game.getHost().equals(playerID) && game.getPlayers().size() > 1) {
            for (Player p : game.getPlayers()) {
                if (!p.getPlayerName().equals(game.getHost())) {
                    game.setHost(p.getPlayerName());
                    break;
                }
            }
        }

        if (game.isStarted()){
            //TODO : add absent players
            //game.get.add(player);
        }
        else game.getPlayers().remove(player);
        DAOFacade.getInstance().addLobby(gameID, game);
        return new Result(true, true, null);

    }

    Result rejoinGame(String gameID, String playerID) {
        Player player = findUserByID(playerID).getPlayer();
        LobbyGame game = findGameLobbyByID(gameID);

        if(player == null || game == null) {
            return new Result(false, null, "Error: Could not rejoin game");
        }

        if(!game.getPlayers().contains(player)) return new Result(false, null, "Error: Player not found");
        //TODO : absent players?
//        else if (mAbsentPlayers.contains(player)) {
//            mAbsentPlayers.remove(player);
//        }

        DAOFacade.getInstance().addLobby(gameID, game);
        return new Result(true, true, null);
    }

    /**
     * Helper function to find the player by its id
     * @param id
     * @return
     */
    ActiveUser findUserByID(String id){
        for (ActiveUser user : activeUsers) {
            if (user.getPlayer().getPlayerName().equals(id))
                    return user;
        }
        return null;
    }

    /**
     * Helper function to find the game by its id
     * @param gameID
     * @return
     */
    LobbyGame findGameLobbyByID(String gameID){
        for (LobbyGame lobbyGame : lobbyGameList) {
            if (lobbyGame.getGameID().equals(gameID)){
                return lobbyGame;
            }
        }
        return null;
    }

    GameServer findGameByID(String gameID){
        for (GameServer gameServer : gameList) {
            if (gameServer.getGameID().equals(gameID)){
                return gameServer;
            }
        }
        return null;
    }

    /**
     * Helper function to find the active user by the player object
     * @param player
     * @return
     */
    ActiveUser findUserByPlayer(Player player) {
        for (ActiveUser user: activeUsers) {
            if (user.getPlayer().equals(player)) {
                return user;
            }
        }
        return null;
    }

    public String findUsernameByAuthToken(String authToken) {
        for (Map.Entry<String, String> entry : authTokenMap.entrySet()) {
            if (authToken.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String findAuthTokenByPlayerID(String playerID) {
        for (Map.Entry<String, String> entry : authTokenMap.entrySet()) {
            if (playerID.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }


    /**
     * Checking if the authToken belongs to the specified user. If not return false
     * @param authToken
     * @param userID
     * @return
     */
    public boolean checkAuthToken(String authToken, String userID) {
        if (authToken.equals("masterKey")) return true;
        if (authTokenMap.containsKey(userID) && authTokenMap.get(userID).equals(authToken)) return true;
        return false;
    }

    /**
     * updating game message list
     * @param gameID
     * @param message
     * @param authToken
     * @return
     */
    public Result sendMessage(String gameID, String message, String authToken) {
        if (authTokenMap.containsValue(authToken) || authToken.equals("masterKey")) {
            Game game = findGameByID(gameID);
            String playerID = findUsernameByAuthToken(authToken);
            if (playerID != null) {
                Message messageObject = new Message(playerID, message);
                if (game.getMessages() == null) {
                    game.setMessages(new ArrayList<>());
                }
                game.getMessages().add(messageObject);
                return new Result(true, messageObject, null);
            }
        }
        return new Result(false, null, "Error : Invalid auth_token");
    }
    /**
     * Creates and retuns a unique, random ID
     * @param isPlayer  if true, returns player ID. If false, returns game ID
     * @return
     */
    String generateID(boolean isPlayer) {
        if(isPlayer) {
            return "P" + UUID.randomUUID().toString();
        }
        else return "G" + UUID.randomUUID().toString();
    }

    /**
     * Generates authToken for users. There are stores in a map where keys are the username and values are authToken Strings
     * @return
     */
    String generateAuthToken(){
        String authToken = UUID.randomUUID().toString();
        return authToken;
    }

    List<Ticket> getTickets(String gameID, String authToken) {
        System.out.println("GET TICKETS");
        GameServer game = findGameByID(gameID);
        List<Ticket> tickets = game.getTickets();
        ArrayList<Ticket> playerTickets = new ArrayList<>();
        if (tickets.size() < 3) {
            List<Ticket> newTickets = new ArrayList<>(tickets);
            game.getTickets().clear();
            return newTickets;
        }
        for (int i = 0; i < 3; i++) {
            Ticket ticket = tickets.get(0);
            playerTickets.add(ticket);
            tickets.remove(0);
        }

        String playerID = findUsernameByAuthToken(authToken);
        for (PlayerUser player : game.getPlayers()) {
            if (player.getPlayerName().equals(playerID)) {
                player.setTicketToChoose(playerTickets);
            }
        }
        return playerTickets;
    }

    Result setTickets(String gameID, String authToken, List<Ticket> tickets) {
        System.out.println("SET TICKETS");
//        //checking if the ticket to keep is more than 2 tickets
//        if (tickets.size() > 2) {
//            return new Result(false, null, "Error : too many tickets");
//        }
        try {
            GameServer game = findGameByID(gameID);
            if (game != null) {
                String playerID = findUsernameByAuthToken(authToken);
                ArrayList<Ticket> tickets1 = new ArrayList<>();

                for (PlayerUser player : game.getPlayers()) {
                    if (player.getPlayerName().equals(playerID)) {
                        tickets1 = player.getTicketToChoose();
                    }
                }

                Set<Integer> overlap = new HashSet<>();
                for (int i = 0; i < tickets.size(); i++) {
                    for (int j = 0; j < tickets1.size(); j++) {
                        if (tickets.get(i).equals(tickets1.get(j))) {
                            overlap.add(j);
                        }
                    }
                }

                List<Ticket> ticketDeck = game.getTickets();
                for (int i = 0; i < tickets1.size(); i++) {
                    if (!overlap.contains(i)) {
                        game.getTickets().add(tickets1.get(i));
                    }
                }
                ArrayList<Ticket> playerTickets = new ArrayList<>();
                for (PlayerUser player : game.getPlayers()) {
                    if (player.getPlayerName().equals(playerID)) {
                        player.getTickets().addAll((ArrayList<Ticket>) tickets);
                        player.setTicketToChoose(new ArrayList<>());
                        playerTickets.addAll(player.getTickets());
                    }
                }

                //update gameHistory
                game.getGameHistories().add(new GameHistory(gameID, playerID, "drew_" + tickets.size() + "_tickets"));
                return new Result(true, playerTickets, null);
            }
//                //TODO : if the deck size is less then 3?
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, null, "Error : player not found");
    }

    Result claimRoute(String gameID, String routeID, List<Card> cards, String authToken) {
        String username = findUsernameByAuthToken(authToken);
        if (username != null) {
            GameServer gameServer = findGameByID(gameID);
            if (gameServer != null) {
                PlayerUser p = gameServer.getPlayerbyUserName(username);
                //TODO : if client doesn't check for right cards, should check here
                //check if player has all the cards
                if (!p.getCards().containsAll(cards)) {
                    return new Result(false, null, "Error : invalid cards");
                }

                //check if player has right cars
                if (p != null && p.getTrainNum() - cards.size() >= 0) {
                    Route claimedRoute = gameServer.getRouteByID(routeID);

                    //check if already claimed
                    if (claimedRoute.getClaimedBy() != null) {
                        return new Result(false, null, "Error : Already Claimed!");
                    }

                    //double route rules
                    //FIXME** must be fixed later
//                    if (claimedRoute instanceof DualRoute && ((DualRoute) claimedRoute).getSibling() != null) {
//                        if (gameServer.getPlayers().size() < 4
//                                || ((DualRoute) claimedRoute).getSibling().getClaimedBy().getPlayerName().equals(username)) {
//                            return new Result(false, null, "Error : violation of double route rules");
//                        }
//                    }

                    //set player claimed route
                    p.getClaimedRoutes().add(routeID);

                    //remove cards
                    for (Card card : cards) {
                        p.getCards().remove(card);
                    }

                    //these cards go to discard piles
                    gameServer.getDiscardDeck().addAll(cards);

                    //set route claimedby
                    gameServer.getmMap().claimRoute(claimedRoute, p);

                    //deduct train car number
                    System.out.println("SUBTRACTING");
                    System.out.println(p.getTrainNum() - cards.size());
                    p.setTrainNum(p.getTrainNum() - cards.size());

                    //give proper points
                    p.addPoint(routeScores.get(claimedRoute.getLength()));

                    //update game history
                    gameServer.getGameHistories().add(new GameHistory(gameID, username, "claimed_" + routeID + "_routes"));

                    //if deck is empty, refill
                    putDiscardToCardDeck(gameServer);
                    //add to graph
//                    gameGraph.get(gameID).addPath(username, claimedRoute);
                    return new Result(true, true, null);
                }

            }
        }
        return new Result(false, null, "Error happened while claiming route");
    }

    Result drawCard(String gameID, int index, String authToken) {
        String username = findUsernameByAuthToken(authToken);
        if (username != null) {
            GameServer game = findGameByID(gameID);
            if (game != null) {
                if (index == -1) {
                    //draw from deck
                    if (game.getDeckSize() == 0) {
                        putDiscardToCardDeck(game);
                    }

                    if (game.getTrainCards().size() == 0) {
                        System.out.println("DECK IS EMPTy");
                        return new Result(false, null, "The deck is empty");
                    }
                    Card card = game.getTrainCards().get(0);
                    game.getTrainCards().remove(0);

                    //add card to player cards
                    game.getPlayerbyUserName(username).getCards().add(card);

                    if (game.getDeckSize() == 0) {
                        putDiscardToCardDeck(game);
                    }
                    game.getGameHistories().add(new GameHistory(gameID, username, "drew_train_card"));
                    return new Result(true, card, null);
                }
                else if (index >= 0 && index <= 4) {
                    if (game.getFaceUpTrainCarCards().size() > index) {
                        //get card from the face up cards
                        Card card = game.getFaceUpTrainCarCards().get(index);
                        if (game.getDeckSize() == 0) {
                            putDiscardToCardDeck(game);
                        }

                        //draw card from the deck and set it to face up cards
//                        if (game.getTrainCards().size() == 0) {
//                            return new Result(false, null, "The deck is empty");
//                        }
                        if (game.getTrainCards().size() > 0) {
                            Card topCard = game.getTrainCards().get(0);
                            game.getTrainCards().remove(0);
                            game.getFaceUpTrainCarCards().set(index, topCard);
                        }
                        else {
                            game.getFaceUpTrainCarCards().remove(index);
                        }

                        while (true) {
                            int counter = 0;

                            //counting locomotive cards
                            for (int i = 0; i < game.getFaceUpTrainCarCards().size(); i++) {
//                                System.out.println(game.getFaceUpTrainCarCards().get(i).getColor());
                                if (game.getFaceUpTrainCarCards().get(i).getColor() == GameColor.LOCOMOTIVE) {
                                    System.out.println("COUNTING");
                                    counter++;
                                }
                            }

                            if (counter >= 3) {
                                //if locomotive is more than 3, discard them and shuffle again
                                ArrayList<Card> faceUpTrainCards = new ArrayList<>();
                                for (int i = 0; i < 5; i++) {
                                    //if deck is empty, shuffle
                                    if (game.getTrainCards().size() == 0) {
                                        putDiscardToCardDeck(game);
                                    }
                                    Card newCard = game.getTrainCards().get(0);
                                    faceUpTrainCards.add(newCard);
                                    game.getTrainCards().remove(0);
                                }
                                game.getDiscardDeck().addAll(game.getFaceUpTrainCarCards());
                                game.setFaceUpTrainCarCards(faceUpTrainCards);
                            }
                            else {
                                break;
                            }
                            System.out.println("GEH TE");
                        }
                        game.getPlayerbyUserName(username).getCards().add(card);
                        game.getGameHistories().add(new GameHistory(gameID, username, "drew_train_card"));

                        if (game.getDeckSize() == 0) {
                            putDiscardToCardDeck(game);
                        }

                        return new Result(true, card, null);
                    }
                }
            }
        }
        return new Result(false, null, "Error while drawing Train Card");
    }

    ArrayList<PlayerStats> getGameResult(String gameID) {
        GameServer gameServer = findGameByID(gameID);
        ArrayList<PlayerStats> playerStats = new ArrayList<>();

        HashMap<String, Integer> winnedPoint = new HashMap<>();
        HashMap<String, Integer> lostPoint = new HashMap<>();
        HashMap<String, Integer> claimedRoutesNum = new HashMap<>();

        for (PlayerUser p : gameServer.getPlayers()) {
            winnedPoint.put(p.getPlayerName(), 0);
            lostPoint.put(p.getPlayerName(), 0);
            claimedRoutesNum.put(p.getPlayerName(), 0);
        }

        for (Route route : gameServer.getmMap().getRoutes()) {
            if (route.getClaimedBy() != null) {
                int point = claimedRoutesNum.get(route.getClaimedBy().getPlayerName()) + 1;
                claimedRoutesNum.put(route.getClaimedBy().getPlayerName(), point);
            }
        }

        //computing ticket paths
        String start, end;
        for (PlayerUser p : gameServer.getPlayers()) {
            for (Ticket ticket : p.getTickets()) {
                start = ticket.getCity1().getName();
                end = ticket.getCity2().getName();
                if (gameGraph.get(gameID).hasPath(p.getPlayerName(), start, end)) {
                    int point = winnedPoint.get(p.getPlayerName()) + ticket.getValue();
                    winnedPoint.put(p.getPlayerName(), point);
                }
                else {
                    int point = lostPoint.get(p.getPlayerName()) + ticket.getValue();
                    lostPoint.put(p.getPlayerName(), point);
                }
            }
        }

        //computing longest path
        MapGraph mapGraph = getGraphByGame(gameID);
        HashMap<String, Integer> paths = mapGraph.findLongestPath();
        System.out.println("LONGEST PATH");
        for (String s : paths.keySet()) {
            System.out.println(s + " " + paths.get(s));
        }

        int maxRouteNum = -1;
        for (String s : paths.keySet()) {
            maxRouteNum = Math.max(maxRouteNum, paths.get(s));
        }
        //calculate scores
        for (PlayerUser p : gameServer.getPlayers()) {
            String username = p.getPlayerName();
            if (!paths.containsKey(username)) {
                paths.put(username, 0);
            }
            int winnedPoints = winnedPoint.get(username);
            int lostPoints = lostPoint.get(username);
            int pathNum = paths.get(username);
            int totalPoints = p.getPoint() + winnedPoints - lostPoints;
            if (pathNum == maxRouteNum) {
                totalPoints += 10;
            }
            PlayerStats playerStats1 = new PlayerStats(username, totalPoints, winnedPoints, lostPoints, pathNum);
            if (pathNum == maxRouteNum) {
                playerStats1.setHasLongestPath(true);
            }
            playerStats.add(playerStats1);
        }

        playerStats.sort(new Comparator<PlayerStats>() {
            @Override
            public int compare(PlayerStats stats, PlayerStats t1) {
                if (stats.getTotalPoint() > t1.getTotalPoint()) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        });

        for (int i = 0; i < playerStats.size(); i++) {
            playerStats.get(i).setRank(i + 1);
        }
        //TODO : get rid of game?
        gameList.remove(gameServer);
        gameUpdates.remove(gameID);
        gameGraph.remove(gameID);
        LobbyGame lobbyGame = findGameLobbyByID(gameID);
        lobbyGameList.remove(lobbyGame);

        return playerStats;
    }
    private void putDiscardToCardDeck(GameServer gameServer) {
        System.out.println("DISCARD FILE");
        System.out.println("BEFORE");
        System.out.println(gameServer.getTrainCards().size());
        System.out.println(gameServer.getDiscardDeck().size());
        ArrayList<Card> disCards = gameServer.getDiscardDeck();
        if (gameServer.getTrainCards().size() == 0 && disCards.size() != 0) {
            Collections.shuffle(disCards);
            gameServer.getTrainCards().addAll(disCards);
            gameServer.getDiscardDeck().clear();
        }
        System.out.println("AFTER");
        System.out.println(gameServer.getTrainCards().size());
        System.out.println(gameServer.getDiscardDeck().size());
    }

    private String nextTurn(GameServer gameServer) {
        gameServer.moveToNextTurn();
        int index = gameServer.getCurrentPlayerIndex();
        return gameServer.getPlayers().get(index).getPlayerName();
    }

    private MapGraph getGraphByGame(String gameID) {
        return gameGraph.get(gameID);
    }

    public void saveCommand(String gameID, GenericCommand command) {
        // 1: add command to list
        ArrayList<GenericCommand> list = (ArrayList<GenericCommand>) gameUpdates.get(gameID);

        // 2: if list.size == updateDelta, save entire game state
        if (list.size() >= updateDelta) {
            list = new ArrayList<>();
            gameUpdates.put(gameID, list);
            saveGameState(gameID);
        }

        list.add(command);

        // 3: save commandList
        saveUpdates(gameID, list);
    }

    public void saveGameState(String gameID) {
        DAOFacade.getInstance().updateGame(gameID, findGameByID(gameID));
    }

    private void saveUpdates(String gameID, ArrayList<GenericCommand> updates) {
        DAOFacade.getInstance().updateCmdList(gameID, updates);
    }

    
}
