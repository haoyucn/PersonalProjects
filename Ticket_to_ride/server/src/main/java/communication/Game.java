package communication;

import com.google.gson.Gson;
import com.obfuscation.server.GenericCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by jalton on 10/1/18.
 */

public class Game {
    //shared --------------------------------
    private String gameID;
    private ArrayList<Message> messages;
    private ArrayList<Card> faceUpTrainCarCards;
    private GameMap mMap;

    boolean lastRound;
    private ArrayList<GameHistory> gameHistories = new ArrayList<>();

    public Game() {
        gameID = UUID.randomUUID().toString();
        messages = new ArrayList<Message>();
        faceUpTrainCarCards = new ArrayList<Card>();
        mMap = new GameMap();
    }

    public Game(String gameID){
        this.gameID = gameID;
        messages = new ArrayList<Message>();
        faceUpTrainCarCards = new ArrayList<Card>();
        mMap = new GameMap();
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String mGameID) {
        this.gameID = mGameID;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Card> getFaceUpTrainCarCards() {
        return faceUpTrainCarCards;
    }

    public void setFaceUpTrainCarCards(ArrayList<Card> faceUpTrainCarCards) {
        this.faceUpTrainCarCards = faceUpTrainCarCards;
    }

    public void removeFaceUpTrainCarCardsByIndex(int index) {
        faceUpTrainCarCards.remove(index);
    }
    public GameMap getmMap() {
        return mMap;
    }

    public void setmMap(GameMap mMap) {
        this.mMap = mMap;
    }

    public void claimRoute(Route route, Player player) {
        mMap.claimRoute(route, player);
        // Need to update route on screen
    }

    public void insertMessage(Message m) {
        messages.add(m);
    }

    public ArrayList<GameHistory> getGameHistories() {
        return gameHistories;
    }

    public void setGameHistories(ArrayList<GameHistory> gameHistories) {
        this.gameHistories = gameHistories;
    }

    public void addHistory(ArrayList<GameHistory> gameHistories) {
        this.gameHistories.addAll(gameHistories);
    }

    public void addHistory(GameHistory gh) {
        this.gameHistories.add(gh);
    }

    public boolean isLastRound() {
        return lastRound;
    }

    public void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    public Route getRouteByID(String routeID) {
        for (Route route : mMap.getRoutes()) {
            if (route.getRouteID().equals(routeID)) {
                return route;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;

        if (lastRound != game.lastRound) return false;
        if (gameID != null ? !gameID.equals(game.gameID) : game.gameID != null) return false;
        if (messages != null ? !messages.equals(game.messages) : game.messages != null)
            return false;
        if (faceUpTrainCarCards != null ? !faceUpTrainCarCards.equals(game.faceUpTrainCarCards) : game.faceUpTrainCarCards != null)
            return false;
        if (mMap != null ? !mMap.equals(game.mMap) : game.mMap != null) return false;
        return gameHistories != null ? gameHistories.equals(game.gameHistories) : game.gameHistories == null;
    }

    @Override
    public int hashCode() {
        int result = gameID != null ? gameID.hashCode() : 0;
        result = 31 * result + (messages != null ? messages.hashCode() : 0);
        result = 31 * result + (faceUpTrainCarCards != null ? faceUpTrainCarCards.hashCode() : 0);
        result = 31 * result + (mMap != null ? mMap.hashCode() : 0);
        result = 31 * result + (lastRound ? 1 : 0);
        result = 31 * result + (gameHistories != null ? gameHistories.hashCode() : 0);
        return result;
    }
}
