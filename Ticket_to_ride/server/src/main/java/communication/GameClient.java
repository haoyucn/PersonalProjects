package communication;

import java.util.ArrayList;

/**
 * Created by urimaj on 11/9/18.
 */

public class GameClient extends Game {

    private ArrayList<PlayerOpponent> playerOpponents;
    private PlayerUser playerUser;
    private int trainCardDeckSize;
    private int ticketDeckSize;
    private boolean gameEnded;
    private ArrayList<PlayerStats> playerStatsList;
    private String turnUser;

    public int getTicketDeckSize() {
        return ticketDeckSize;
    }

    public void setTicketDeckSize(int ticketDeckSize) {
        this.ticketDeckSize = ticketDeckSize;
    }

    private int state;

    public Player getPlayerByUserName(){
        return null;
    }

    public GameClient(){
        super();
        playerStatsList = new ArrayList<PlayerStats>();
    }
    public GameClient(String gameID, String userName) {
        super(gameID);
        playerOpponents = new ArrayList<PlayerOpponent>();
        playerUser = new PlayerUser(userName);
        gameEnded = false;
        playerStatsList = new ArrayList<PlayerStats>();
        trainCardDeckSize = 0;
    }


    public ArrayList<PlayerOpponent> getPlayerOpponents() {
        return playerOpponents;
    }

    public void setPlayerOpponents(ArrayList<PlayerOpponent> playerOpponents) {
        this.playerOpponents = playerOpponents;
    }

    public PlayerOpponent getPlayerOpponentByUsername(String userName) {
        for(PlayerOpponent p : playerOpponents) {
            if (p.getPlayerName().equals(userName)) {
                return p;
            }
        }
        return null;
    }

    public PlayerUser getPlayerUser() {
        return playerUser;
    }

    public void setPlayerUser(PlayerUser playerUser) {
        this.playerUser = playerUser;
    }

    public int getTrainCardDeckSize() {
        return trainCardDeckSize;
    }

    public void setTrainCardDeckSize(int trainCardDeckSize) {
        this.trainCardDeckSize = trainCardDeckSize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void stateIncreament(){
        state++;
    }

    public IPlayer getPlayerByUserName(String userName) {
        if (playerUser.getPlayerName().equals(userName)) {
            return playerUser;
        }

        for(PlayerOpponent po : playerOpponents) {
            if(po.getPlayerName().equals(userName)) {
                return po;
            }
        }
        return null;
    }

    public Card UserTakeFaceUpCard(int index) {
        Card card = null;
        ArrayList<Card> faceUpCards = this.getFaceUpTrainCarCards();
        if (faceUpCards != null) {
            card = faceUpCards.remove(index);
            System.out.println("index is: " + index);
            System.out.println(faceUpCards.size());
            this.getPlayerUser().addCard(card);
        }
        return card;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public ArrayList<PlayerStats> getPlayerStatsList() {
        return playerStatsList;
    }

    public void setPlayerStatsList(ArrayList<PlayerStats> playerStatsList) {
        this.playerStatsList = playerStatsList;
    }

    public void addPlayerStatsList(ArrayList<PlayerStats> playerStatsList) {
        this.playerStatsList.addAll(playerStatsList);
    }

    public void addPlayerStats(PlayerStats stats) {
        this.playerStatsList.add(stats);
    }

    public String getTurnUser() {
        return turnUser;
    }

    public void setTurnUser(String turnUser) {
        this.turnUser = turnUser;
    }
}
