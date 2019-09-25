package communication;

import com.obfuscation.server.ClientProxy;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by urimaj on 11/9/18.
 */

public class GameServer extends Game {

    private ArrayList<Card> trainCards = new ArrayList<>();
    private ArrayList<Card> discardDeck = new ArrayList<>();
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private String currentPlayer;
    private String lastRoundTriggeredBy;
    private ArrayList<PlayerUser> players = new ArrayList<>();
    private ArrayList<ClientProxy> clientProxies = new ArrayList<>();

    public ArrayList<ClientProxy> getOriginalClientProxies() {
        return originalClientProxies;
    }

    public void setOriginalClientProxies(ArrayList<ClientProxy> originalClientProxies) {
        this.originalClientProxies = originalClientProxies;
    }

    private ArrayList<ClientProxy> originalClientProxies = new ArrayList<>();

    public ArrayList<ClientProxy> getClientProxies() {
        return clientProxies;
    }

    public void setClientProxies(ArrayList<ClientProxy> clientProxies) {
        this.clientProxies = clientProxies;
    }

    public ArrayList<Card> getTrainCards() {
        return trainCards;
    }

    public void setTrainCards(ArrayList<Card> trainCards) {
        this.trainCards = trainCards;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public ArrayList<PlayerUser> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<PlayerUser> players) {
        this.players = players;
    }

    public ArrayList<Player> getmAbsentPlayers() {
        return mAbsentPlayers;
    }

    public void setmAbsentPlayers(ArrayList<Player> mAbsentPlayers) {
        this.mAbsentPlayers = mAbsentPlayers;
    }

    public GameServer() {
        setGameID(UUID.randomUUID().toString());
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void moveToNextTurn() {
        currentPlayerIndex++;
        currentPlayerIndex %= getPlayers().size();
        currentPlayer = players.get(currentPlayerIndex).getPlayerName();
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Card takeCard(int index) {
        Card card;
        if(index < 0) {
            card = trainCards.get(0);
            trainCards.remove(0);
        }else {
            card = getFaceUpTrainCarCards().get(index);
            getFaceUpTrainCarCards().remove(index);
            getFaceUpTrainCarCards().add(trainCards.get(0));
            trainCards.remove(0);
        }
        return card;
    }
    private ArrayList<Player> mAbsentPlayers;

    public int getDeckSize() {
        return trainCards.size();
    }

    public PlayerUser getPlayerbyUserName(String username) {
        for(PlayerUser p: players) {
            if (p.getPlayerName().equals(username)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Card> getDiscardDeck() {
        return discardDeck;
    }

    public void setDiscardDeck(ArrayList<Card> discardDeck) {
        this.discardDeck = discardDeck;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getLastRoundTriggeredBy() {
        return lastRoundTriggeredBy;
    }

    public void setLastRoundTriggeredBy(String lastRoundTriggeredBy) {
        this.lastRoundTriggeredBy = lastRoundTriggeredBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GameServer that = (GameServer) o;
        if (currentPlayerIndex != that.currentPlayerIndex) return false;
        if (trainCards != null ? !trainCards.equals(that.trainCards) : that.trainCards != null)
            return false;
        if (discardDeck != null ? !discardDeck.equals(that.discardDeck) : that.discardDeck != null)
            return false;
        if (tickets != null ? !tickets.equals(that.tickets) : that.tickets != null) return false;
        if (currentPlayer != null ? !currentPlayer.equals(that.currentPlayer) : that.currentPlayer != null)
            return false;
        if (lastRoundTriggeredBy != null ? !lastRoundTriggeredBy.equals(that.lastRoundTriggeredBy) : that.lastRoundTriggeredBy != null)
            return false;
        if (players != null ? !players.equals(that.players) : that.players != null) return false;
        return mAbsentPlayers != null ? mAbsentPlayers.equals(that.mAbsentPlayers) : that.mAbsentPlayers == null;
    }

    @Override
    public int hashCode() {
        int result = trainCards != null ? trainCards.hashCode() : 0;
        result = 31 * result + (discardDeck != null ? discardDeck.hashCode() : 0);
        result = 31 * result + (tickets != null ? tickets.hashCode() : 0);
        result = 31 * result + currentPlayerIndex;
        result = 31 * result + (currentPlayer != null ? currentPlayer.hashCode() : 0);
        result = 31 * result + (lastRoundTriggeredBy != null ? lastRoundTriggeredBy.hashCode() : 0);
        result = 31 * result + (players != null ? players.hashCode() : 0);
        result = 31 * result + (mAbsentPlayers != null ? mAbsentPlayers.hashCode() : 0);
        return result;
    }
}
