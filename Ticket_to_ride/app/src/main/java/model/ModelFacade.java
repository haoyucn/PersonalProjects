package model;


import android.util.Log;

import com.obfuscation.ttr_phase1b.activity.PresenterFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import communication.Card;
import communication.DualRoute;
import communication.GameClient;
import communication.GameColor;
import communication.Game;
import communication.GameHistory;
import communication.LobbyGame;
import communication.GameMap;
import communication.Message;
import communication.Player;
import communication.PlayerOpponent;
import communication.PlayerStats;
import communication.PlayerUser;
import communication.Result;
import communication.Route;
import communication.Ticket;
import server.CommandTask;
import task.GenericTask;

/**
 * Created by hao on 10/3/18.
 */

public class ModelFacade implements IGameModel {

    private static String TAG = "modelFacade";

    private static ModelFacade modelFacade;

    private List<Card> mFaceCards;

    private ModelFacade() {
        Player mHost = new Player("Bob (the host)");
        List<Player> fakePlayers = new ArrayList<>();
        fakePlayers.add(mHost);
        fakePlayers.add( new Player("player 2") );
        fakePlayers.add( new Player("player 3") );
        fakePlayers.add( new Player("player 4") );

        mFaceCards = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int rando = ThreadLocalRandom.current().nextInt(0,9);

            switch (rando) {
                case 0:
                    mFaceCards.add(new Card(GameColor.RED));
                    break;
                case 1:
                    mFaceCards.add(new Card(GameColor.PURPLE));
                    break;
                case 2:
                    mFaceCards.add(new Card(GameColor.BLUE));
                    break;
                case 3:
                    mFaceCards.add(new Card(GameColor.GREEN));
                    break;
                case 4:
                    mFaceCards.add(new Card(GameColor.YELLOW));
                    break;
                case 5:
                    mFaceCards.add(new Card(GameColor.ORANGE));
                    break;
                case 6:
                    mFaceCards.add(new Card(GameColor.BLACK));
                    break;
                case 7:
                    mFaceCards.add(new Card(GameColor.WHITE));
                    break;
                case 8:
                    mFaceCards.add(new Card(GameColor.LOCOMOTIVE));
                    break;
                default:
            }
        }


//        mCurrentGame = new Game("new republic (the game id)", mHost.getPlayerName(), fakePlayers, 5);
    }

    public static ModelFacade getInstance() {
        if (modelFacade == null) {
            modelFacade = new ModelFacade();
        }
        return modelFacade;
    }

    @Override
    public void setMyTurn(boolean isTurn) {

    }

    @Override
    public int getDeckSize() {
        System.out.println("the decksize is: " + ModelRoot.getInstance().getGame().getTrainCardDeckSize() );
        return ModelRoot.getInstance().getGame().getTrainCardDeckSize();
    }

    @Override
    public int getTicketDeckSize() {
        return ModelRoot.getInstance().getGame().getTicketDeckSize();
    }

    public void login(String userName, String password){
        GenericTask genericTask = new GenericTask("login");
        genericTask.execute(userName,password);
        ModelRoot.getInstance().setUserName(userName);
    }

    public void register(String userName, String password) {
        GenericTask genericTask = new GenericTask("register");
        genericTask.execute(userName,password);
        ModelRoot.getInstance().setUserName(userName);
    }

    public void joinLobbyGame(LobbyGame lobbyGame) {
        GenericTask genericTask = new GenericTask("joinLobbyGame");
        genericTask.execute(ModelRoot.getInstance().getUserName(), lobbyGame.getGameID(), ModelRoot.getInstance().getAuthToken());
        ModelRoot.getInstance().setLobbyGame(lobbyGame);
    }

    public void createLobbyGame(LobbyGame lobbyGame) {
        GenericTask genericTask = new GenericTask("CreateLobby");
        genericTask.execute(lobbyGame, ModelRoot.getInstance().getAuthToken());
        ModelRoot.getInstance().setLobbyGame(lobbyGame);
    }

    public void leaveLobbyGame(LobbyGame lobbyGame) {
        GenericTask genericTask = new GenericTask("leaveLobbyGame");
        genericTask.execute(ModelRoot.getInstance().getUserName(), lobbyGame.getGameID(), ModelRoot.getInstance().getAuthToken());
    }

    public void leaveGame(Game game) {
        GenericTask genericTask = new GenericTask("leaveGame");
        genericTask.execute(ModelRoot.getInstance().getUserName(), game.getGameID(), ModelRoot.getInstance().getAuthToken());
    }

    public void startGame(String gameId) {
        if(ModelRoot.getInstance().getGame() == null){
            String lobbyID = ModelRoot.getInstance().getLobbyGame().getGameID();
            String userName = ModelRoot.getInstance().getUserName();
            ModelRoot.getInstance().setGame(new GameClient(lobbyID, userName));
            CommandTask commandTask = new CommandTask();
            commandTask.execute();
        }

        GenericTask genericTask = new GenericTask("startGame");
        genericTask.execute(gameId, ModelRoot.getInstance().getAuthToken());
    }

    public void updateGameList() {
        GenericTask genericTask = new GenericTask("GetLobbyList");
        genericTask.execute(ModelRoot.getInstance().getAuthToken());
    }

    public void UpdateLobby() {
        GenericTask genericTask = new GenericTask("GetLobby");
        genericTask.execute(ModelRoot.getInstance().getLobbyGame().getGameID(), ModelRoot.getInstance().getAuthToken());
    }

    public void CheckGameList() {
        GenericTask genericTask = new GenericTask("CheckGameList");
        genericTask.execute(ModelRoot.getInstance().getAuthToken());
    }

//    public void CheckGame() {
//        GenericTask genericTask = new GenericTask("CheckGame");
//        genericTask.execute(ModelRoot.newInstance().getAuthToken(), ModelRoot.newInstance().getGame().getGameID(), Poller.gameVersion);
//    }

    @Override
    public void sendMessage(Message message) {
        GenericTask genericTask = new GenericTask("SendMessage");
        genericTask.execute(ModelRoot.getInstance().getAuthToken(), ModelRoot.getInstance().getGame().getGameID(), message);
    }

    @Override
    public void endTurn() {
        System.out.println("end turn called");
        GenericTask genericTask = new GenericTask("EndTurn");
        genericTask.execute(ModelRoot.getInstance().getGame().getGameID(), ModelRoot.getInstance().getAuthToken());

        setMyTurn(false);
    }

    @Override
    public List<Player> getPlayers() {
        ModelRoot mr = ModelRoot.getInstance();
        if(mr.getDisplayState() == DisplayState.LOBBY) {
            return mr.getLobbyGame().getPlayers();
        }
        else
            return null;
    }

    @Override
    public List<PlayerOpponent> getOpponents() {
        return getCurrentGame().getPlayerOpponents();
    }

    @Override
    public void addPoints(int p) {
        getPlayer().setPoint(p);
    }

    @Override
    public void useCards(GameColor color, int number) {
        getPlayer().useCards(color, number);
    }

    @Override
    public void addTickets(List<Ticket> tickets) {
        getPlayer().addTickets((ArrayList<Ticket>) tickets);
    }

    @Override
    public void removeTicket(int index) {
        getPlayer().removeTicket(index);
    }

    @Override
    public void updateOpponent() {
//        List<Player> players = ModelRoot.newInstance().getGame().getPlayers();
//        players.get(1).setTrainCarNum(12);
//        players.get(1).setCardNum(24);
//        players.get(1).setPoint(32);
    }

    @Override
    public void chooseTickets(List<Ticket> tickets) {
//        System.out.println("called choose ticket");
//        System.out.println(tickets.size());
        if (getTickets() != null)
            System.out.println("ticket size is: " + getTickets().size());
        GenericTask genericTask = new GenericTask("ReturnTickets");

        genericTask.execute(ModelRoot.getInstance().getGame().getGameID(),ModelRoot.getInstance().getAuthToken(), tickets);
        ModelRoot.getInstance().setTicketsWanted((ArrayList<Ticket>) tickets);


        getPlayer().addTickets((ArrayList<Ticket>)tickets);
    }

    @Override
    //ask for three new tickets to choose from server
    public void updateChoiceTickets() {
        GenericTask genericTask = new GenericTask("GetTickets");
        genericTask.execute(ModelRoot.getInstance().getGame().getGameID(), ModelRoot.getInstance().getAuthToken());
//        System.out.println("called it onece once once");
    }


    @Override
    //update the list of cards user has
    public void updateCards() {
//not for this phase
//        return ModelRoot.newInstance().getGame().getTrainCards();
    }

    @Override
    //it is just a get method
    public void updateFaceCards() {
//        ModelRoot.newInstance().getGame().UserTakeFaceUpCard(0);
    }

    @Override
    public GameColor checkCard(int index) {
        try {
            mFaceCards = ModelRoot.getInstance().getGame().getFaceUpTrainCarCards();
            return mFaceCards.get(index).getColor();
        } catch (ArrayIndexOutOfBoundsException e) {
            return GameColor.GREY;
        }
    }

    @Override
    public void chooseCard(int index) {
//        ModelRoot.newInstance().getGame().UserTakeFaceUpCard(index);
        if(index >= 0) {
            getCurrentGame().removeFaceUpTrainCarCardsByIndex(index);
            PresenterFacade.getInstance().updatePresenter(new Result(true, null, null));
        }
        GenericTask genericTask = new GenericTask("DrawTrainCard");
        genericTask.execute(ModelRoot.getInstance().getGame().getGameID(), index, ModelRoot.getInstance().getAuthToken());
    }

    @Override
    public void updateMessages() {
//not for this phase
    }

    //Called by presenter
    public boolean updateState(DisplayState displayState) {
        ModelRoot.getInstance().setDisplayState(displayState);
        return true;
    }

    public ArrayList<LobbyGame> getLobbyGameList() {
        ArrayList<LobbyGame> unstarted = new ArrayList<LobbyGame>();
        ArrayList<LobbyGame> allLobby = ModelRoot.getInstance().getLobbyGames();
        if (allLobby != null) {
            for (LobbyGame lobbyGame : allLobby) {
                if (!lobbyGame.isStarted()) {
                    unstarted.add(lobbyGame);
                }
            }
        }
        return unstarted;
    }

    public GameClient getCurrentGame() {
        return ModelRoot.getInstance().getGame();
    }

    @Override
    public GameMap getMap() {
        return ModelRoot.getInstance().getGame().getmMap();
    }

    @Override
    //ask server
    public void updateTickets() {
//not for this phase
    }


    @Override
    public boolean isMyTurn() {
        if (getCurrentGame().getTurnUser() == null) {
            return false;
        }

        System.out.println("it is " + getCurrentGame().getTurnUser() +  " turn");

        return getCurrentGame().getTurnUser().equals(getUserName());
    }

    @Override
    public String getUserName() {
        return ModelRoot.getInstance().getUserName();
    }

    @Override
    public List<Card> getCards() {
        return getPlayer().getCards();
    }

    @Override
    public Object checkRouteCanClaim(Route route) {
        //Before first, check to see if it has been claimed
        if(route.getClaimedBy() != null) {
            return "This route has already been claimed";
        }

        //First, check to see if it's a dual route
        if(route.getSibling() != null) {
            String siblingID = route.getSibling();
            Route rib = ModelRoot.getInstance().getGame().getmMap().getRouteByRouteId(siblingID);
            if(rib.getClaimedBy() != null) {
                if(ModelRoot.getInstance().getGame().getPlayerOpponents().size() < 3) {
                    return "Only one dual route can be claimed if there are < 4 players";
                }if (getPlayer().checkRouteIfClaimed(siblingID)) {
                    return "You may not claim both dual routes";
                }
            }
        }

        GameColor color = route.getColor();
        int length = route.getLength();
        if(getPlayer().getTrainNum() < length) {
            return "You don't have enough train cars!";
        }

        List<Card> cards = getCards();

        if (color == GameColor.GREY) {
            //works through all colors to see if there's enough for a set
            if (largestSet() >= length) return true;
            else return "Not enough cards";
        }
        else {
            int cardNum = 0;
            List<Card> cardColor = new ArrayList<>();

            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                if (card.getColor() == color) {
                    cardNum++;
                    cardColor.add(new Card(color));
                }
                else if (card.getColor() == GameColor.LOCOMOTIVE) {
                    cardNum++;
                }
            }
            if (cardNum >= length) {
                if (cardColor.size() < length) {
                    while (cardColor.size() < length) {
                        cardColor.add(new Card(GameColor.LOCOMOTIVE));
                    }
                }
                return new ArrayList<>(cardColor.subList(0, length));
            }
            else return "Not enough cards";
        }
    }

    public void claimRoute(Route route, List<Card> cards) {
        claimRoute(route, getPlayer(), cards);
    }

    @Override
    public void claimRoute(Route route, Player player, List<Card> cards) {
        Log.d(TAG, "claimRoute: " + route.getRouteID());
        PlayerUser p = (PlayerUser) player;
        for(Card card : cards ) {
            p.useCards(card.getColor(), 1);
        }
        p.subtractTrain(cards.size());
        p.addRouteAsClaimed(route.getRouteID());
        ModelRoot.getInstance().getGame().getmMap().getRouteByRouteId(route.getRouteID()).setClaimedBy(player);
        route.setClaimedBy(p);
        PresenterFacade.getInstance().getPresenter().updateInfo(route);
        GenericTask genericTask = new GenericTask("ClaimRoute");
        genericTask.execute( ModelRoot.getInstance().getGame().getGameID(), route.getRouteID(),cards,ModelRoot.getInstance().getAuthToken());

    }

    private int largestSet(){
        //step 1: make hand
        int[] hand = {0,0,0,0,0,0,0,0,0};

        for(int i = 0; i < getPlayer().getCards().size(); i++) {
            Card card = getPlayer().getCards().get(i);

            switch(card.getColor()) {
                case RED:
                    hand[0] += 1;
                    break;
                case ORANGE:
                    hand[1] += 1;
                    break;
                case YELLOW:
                    hand[2] += 1;
                    break;
                case GREEN:
                    hand[3] += 1;
                    break;
                case BLUE:
                    hand[4] += 1;
                    break;
                case PURPLE:
                    hand[5] += 1;
                    break;
                case WHITE:
                    hand[6] += 1;
                    break;
                case BLACK:
                    hand[7] += 1;
                    break;
                case LOCOMOTIVE:
                    hand[8] += 1;
                    break;
                    default:
            }
        }

        //Step 2: check to see which color has the most cards
        int largest = hand[0];

        for(int i = 1; i < 8; i++) {
            if (hand[i] > largest) {
                largest = hand[i];
            }
        }

        //Step 3: return that number plus the number of locomotives
        return largest+hand[8];
    }

    @Override
    public List<Card> getFaceCards() {
        return ModelRoot.getInstance().getGame().getFaceUpTrainCarCards();
    }

    public PlayerUser getPlayer() {
        GameClient g = ModelRoot.getInstance().getGame();
        if (g != null) {
            PlayerUser p = g.getPlayerUser();
            return p;
        }
//        System.out.println("game client is still null");
        return null;
    }

    @Override
    public List<Ticket> getTickets() {
        return getPlayer().getTickets();
    }

    @Override
    public List<Ticket> getChoiceTickets() {
        if (ModelRoot.getInstance().getGame() != null){
            if (ModelRoot.getInstance().getGame().getPlayerUser()!= null) {
                return ModelRoot.getInstance().getGame().getPlayerUser().getTicketToChoose();
            }
        }
        return null;
    }

    @Override
    public List<Message> getMessages() {
        return getCurrentGame().getMessages();
    }

    public boolean isGameStarted() {
        return ModelRoot.getInstance().getLobbyGame().isStarted();
    }

    public LobbyGame getLobbyGame() {
        return ModelRoot.getInstance().getLobbyGame();
    }

    public ArrayList<PlayerStats> getPlayerStats(){
        return ModelRoot.getInstance().getGame().getPlayerStatsList();
    }

    public boolean isLastTurn(){
        return ModelRoot.getInstance().getGame().isLastRound();
    }

    public boolean isGameEnded() {
        return ModelRoot.getInstance().getGame().isGameEnded();
    }

    public List<GameHistory> getGameHistory() {
        return getCurrentGame().getGameHistories();
    }

}
