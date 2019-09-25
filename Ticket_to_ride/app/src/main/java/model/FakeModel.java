package model;

import com.obfuscation.ttr_phase1b.activity.PresenterFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import communication.Card;
import communication.GameColor;
import communication.City;
import communication.Game;
import communication.GameMap;
import communication.Message;
import communication.Player;
import communication.Result;
import communication.Route;
import communication.Ticket;

public class FakeModel {

//    private static FakeModel model;
//
//    private String authToken;
//    private ArrayList<Game> gameList;
//    private Game game;
//    private DisplayState displayState;
//    private String userName;
//
//    private List<Ticket> allTickets;
//    private int allTickInd;
//    private List<Ticket> userTickets;
//    private List<Ticket> choiceTickets;
//
//    private List<Card> faceupCards;
//    private List<Card> userCards;
//
//    private List<Message> messages;
//
//    boolean myTurn;
//
//    private Player mPlayer;
//
//    private FakeModel() {
//
//        myTurn = false;
//
//        Player mHost = new Player("Bob (the host)");
//
//        List<Player> fakePlayers = new ArrayList<>();
//        fakePlayers.add(mHost);
//        fakePlayers.add( new Player("player 2") );
//        fakePlayers.get(1).setPoint(10);
//        fakePlayers.get(1).setTrainCarNum(37);
//        fakePlayers.add( new Player("player 3") );
//        fakePlayers.get(2).setPoint(14);
//        fakePlayers.get(2).setTrainCarNum(36);
//        fakePlayers.add( new Player("player 4") );
//        fakePlayers.get(3).setPoint(18);
//        fakePlayers.get(3).setTrainCarNum(39);

//        mPlayer = mHost;
//
//        initPlayer();
//
//        userName = "Bob";
//        game = new Game();
//        game.setHost(userName);
//        game.setPlayers((ArrayList<Player>) fakePlayers);
//        displayState = DisplayState.GAME;
//
//        List<GameColor> colors = new ArrayList<>();
//        colors.add(GameColor.PLAYER_RED);
//        colors.add(GameColor.PLAYER_PURPLE);
//        colors.add(GameColor.PLAYER_BLUE);
//        colors.add(GameColor.PLAYER_YELLOW);
//        colors.add(GameColor.PLAYER_BLACK);
//
//        colors.remove(mPlayer.getPlayerColor());
//
//        for (int i = 1; i < game.getPlayers().size(); i++) {
//            game.getPlayers().get(i).setPlayerColor(colors.get(i));
//        }
//
//        userTickets = new ArrayList<>();
//        choiceTickets = null;
//        allTickets = new ArrayList<>();
//        City city1, city2;
//        city1 = new City("New York City", 40, 73);
//        city2 = new City("Los Angeles", 34, 118);
//        allTickets.add(new Ticket(city1, city2, 22));
//        city1 = new City("New York City", 40, 73);
//        city2 = new City("Florida", 25, 80);
//        allTickets.add(new Ticket(city1, city2, 12));
//        city1 = new City("Seattle", 47, 122);
//        city2 = new City("Los Angeles", 34, 118);
//        allTickets.add(new Ticket(city1, city2, 8));
//        city1 = new City("Kansas", 39, 94);
//        city2 = new City("Los Angeles", 34, 118);
//        allTickets.add(new Ticket(city1, city2, 15));
//        city1 = new City("Los Angeles", 34, 188);
//        city2 = new City("Florida", 25, 80);
//        allTickets.add(new Ticket(city1, city2, 19));
//        city1 = new City("Seattle", 47, 122);
//        city2 = new City("Kansas",0,0);
//        allTickets.add(new Ticket(city1, city2, 14));
//        city1 = new City("New York City",0,0);
//        city2 = new City("Kansas",0,0);
//        allTickets.add(new Ticket(city1, city2, 9));
//        allTickInd = 0;
//        game.setTickets((ArrayList<Ticket>) allTickets);
//
//        messages = new ArrayList<>();
//        messages.add(new Message("angie", "hello people of earth"));
//        messages.add(new Message("harry", "who are you?"));
//        messages.add(new Message("angie", "death in a box"));
//        messages.add(new Message("harry", "?\n...okay..."));
//        messages.add(new Message(userName, "whatsup my homies!"));
//        game.setMessages((ArrayList<Message>) messages);
//
//
//        faceupCards = new ArrayList<>();
//        faceupCards.add(new Card(GameColor.BLACK));
//        faceupCards.add(new Card(GameColor.BLACK));
//        faceupCards.add(new Card(GameColor.PURPLE));
//        faceupCards.add(new Card(GameColor.RED));
//        faceupCards.add(new Card(GameColor.YELLOW));
//        game.setFaceUpTrainCarCards((ArrayList<Card>) faceupCards);
//
//
//        userCards = new ArrayList<>();
//        userCards.add(new Card(GameColor.LOCOMOTIVE));
//        userCards.add(new Card(GameColor.WHITE));
//        userCards.add(new Card(GameColor.BLACK));
//        userCards.add(new Card(GameColor.GREEN));
//        mHost.setCards((ArrayList<Card>) userCards);
//        mHost.setCardNum(userCards.size());
//        mHost.setPlayerColor(GameColor.PLAYER_RED);
//
//    }
//
//    public static FakeModel getInstance() {
//        if(model == null) {
//            model = new FakeModel();
//        }
//        return model;
//    }
//
//    private void initPlayer(){
//        GameColor color = GameColor.PLAYER_BLACK;
//
//
//
//        int rando = ThreadLocalRandom.current().nextInt(0,5);
//        switch (rando) {
//            case 0:
//                color = GameColor.PLAYER_RED;
//                break;
//            case 1:
//                color = GameColor.PLAYER_PURPLE;
//                break;
//            case 2:
//                color = GameColor.PLAYER_YELLOW;
//                break;
//            case 3:
//                color = GameColor.PLAYER_BLACK;
//                break;
//            case 4:
//                color = GameColor.PLAYER_BLUE;
//                break;
//        }
//
//        mPlayer.setPlayerColor(color);
//
//
//    }
//
//    @Override
//    public Result claimRoute(Route route, Player player) {
//        game.claimRoute(route, player);
//        return new Result(true, route, null);
//    }
//
//    @Override
//    public Player getPlayer() {
//        return mPlayer;
//    }
//
//    @Override
//    public int getDeckSize() {
//        return game.getDeckSize();
//    }
//
//    //  metadata
//    @Override
//    public boolean isMyTurn() {
//        return myTurn;
//    }
//
//    @Override
//    public String getUserName() {
//        return userName;
//    }
//
//    @Override
//    public GameMap getMap() {
//        return game.getmMap();
//    }
//
//    //  tickets
//    @Override
//    public void updateTickets() {
//        PresenterFacade.getInstance().updatePresenter(true);
//    }
//
//    @Override
//    public void updateChoiceTickets() {
//        PresenterFacade.getInstance().updatePresenter(true);
//    }
//
//    @Override
//    public List<Ticket> getTickets() {
//        return mPlayer.getTickets();
//    }
//
//    @Override
//    public List<Ticket> getChoiceTickets() {
//        choiceTickets = new ArrayList<>();
//        for(int i = 0; i < 3; i++) {
//            choiceTickets.add(allTickets.get(allTickInd));
//            ++allTickInd;
//            allTickInd = allTickInd % allTickets.size();
//        }
//        return choiceTickets;
//    }
//
//    @Override
//    public void selectTickets(List<Ticket> tickets) {
//        mPlayer.addTickets((ArrayList<Ticket>) tickets);
//        choiceTickets = null;
//    }
//
//    //  cards
//    @Override
//    public void updateCards() {
//        PresenterFacade.getInstance().updatePresenter(true);
//    }
//
//    @Override
//    public void updateFaceCards() {
//        PresenterFacade.getInstance().updatePresenter(true);
//    }
//
//    @Override
//    public List<Card> getFaceCards() {
//        return game.getFaceUpTrainCarCards();
//    }
//
//    @Override
//    public List<Card> getCards() {
//        return mPlayer.getCards();
//    }
//
//    @Override
//    public void chooseCard(int index) {
//        ArrayList<Card> faceupCards = game.getFaceUpTrainCarCards();
//        if(index > 0) {
//            mPlayer.addCard(faceupCards.get(index));
////            userCards.add(faceupCards.get(index));
//            faceupCards.remove(index);
//            faceupCards.add(new Card(GameColor.GREEN));
//        }else {
//            mPlayer.addCard(new Card(GameColor.ORANGE));
//        }
//    }
//
//    @Override
//    public void setMyTurn(boolean isTurn) {
//        myTurn = isTurn;
//    }
//
//    //  messages
//    @Override
//    public void updateMessages() {
//        PresenterFacade.getInstance().updatePresenter(true);
//    }
//
//    @Override
//    public List<Message> getMessages() {
//        return messages;
//    }
//
//    @Override
//    public void sendMessage(Message message) {
//        messages.add(message);
//        PresenterFacade.getInstance().updatePresenter(null);
//    }
//
//    @Override
//    public List<Player> getPlayers() {
//        return game.getPlayers();
//    }
//
//    @Override
//    public void addPoints(int p) {
//        mPlayer.addPoint(p);
//    }
//
//    @Override
//    public void useCards(GameColor color, int number) {
//        mPlayer.useCards(color, number);
//    }
//
//    @Override
//    public void addTickets(List<Ticket> tickets) {
//        mPlayer.addTickets((ArrayList<Ticket>) tickets);
//    }
//
//    @Override
//    public void removeTicket(int index) {
//        mPlayer.removeTicket(index);
//    }
//
//    @Override
//    public void updateOpponent() {
//        List<Player> players = game.getPlayers();
//        players.get(1).setTrainCarNum(12);
//        players.get(1).setCardNum(24);
//        players.get(1).setPoint(32);
//    }

}
