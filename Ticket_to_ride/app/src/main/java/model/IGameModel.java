package model;

import java.util.ArrayList;
import java.util.List;

import communication.Card;
import communication.GameColor;
import communication.GameHistory;
import communication.GameMap;
import communication.Message;
import communication.Player;
import communication.PlayerOpponent;
import communication.PlayerStats;
import communication.Route;
import communication.Ticket;

public interface IGameModel {

    boolean isMyTurn();

    void setMyTurn(boolean isTurn);

    String getUserName();

    GameMap getMap();

    Player getPlayer();

    /**
     * Sends a request to claim a route with the given cards
     * @param route
     * @param player
     * @param cards
     * @return
     */
    void claimRoute(Route route, Player player, List<Card> cards);

    void claimRoute(Route route, List<Card> cards);

    void updateTickets();

    void updateChoiceTickets();

    List<Ticket> getTickets();

    List<Ticket> getChoiceTickets();

    void chooseTickets(List<Ticket> tickets);

    void updateCards();

    void updateFaceCards();

    List<Card> getCards();

    List<Card> getFaceCards();

    /**
     * Checks if the player has enough cards of color and enough trains left to claim a route of
     * given length. Also checks to see if it is a dual route, and, if so, checks to be sure the
     * player has not claimed its sibling.
     *
     * @param route@return  A list of cards if the player can claim the route, an error message if not
     */
    Object checkRouteCanClaim(Route route);

    /**
     * Returns the color of the selected face-up card
     * @param index
     * @return
     */
    GameColor checkCard(int index);

    void chooseCard(int index);

    int getDeckSize();

    int getTicketDeckSize();

    void updateMessages();

    List<Message> getMessages();

    void sendMessage(Message message);

    List<Player> getPlayers();

    List<PlayerOpponent> getOpponents();

    void addPoints(int p);

    void useCards(GameColor color, int number);

    void addTickets(List<Ticket> tickets);

    void removeTicket(int index);

    void updateOpponent();

    void endTurn();

    ArrayList<PlayerStats> getPlayerStats();

    boolean isLastTurn();

    boolean isGameEnded();

    List<GameHistory> getGameHistory();

}
