package com.obfuscation.ttr_phase1b.gameViews;

import java.util.List;

import communication.Card;
import communication.GameColor;
import communication.GameMap;
import communication.Player;
import communication.Route;
import communication.Ticket;

/**
 * Created by jalton on 10/24/18.
 *
 * javadoc created by Jordan Alton
 */

public interface IGameView extends IView {

    /**
     * Sets the map to be displayed
     *
     * @pre map is an instance of GameMap and has a proper list of Routes and Cities
     * @post the MapView is updated to show the Routes and Cities in the map
     *
     * @param map a GameMap object containing Routes and Cities
     */
    void setMap(GameMap map);

    /**
     * Sets the players hand of cards
     *
     * @pre cards is a list of valid Card objects
     * @post the player's hand display is updated to the contents of cards
     *
     * @param cards a List of Card objects representing the player's new hand
     */
    void setCards(List<Card> cards);

    /**
     * Sets the available face-up cards
     *
     * @pre cards contains no more than 5 Card objects
     * @post the view is updated to display the 5 given cards
     *
     * @param cards a List of Card objects representing the face-up options
     */
    void setFaceCards(List<Card> cards);

    /**
     * Sets the players list of tickets
     *
     * @pre tickets contains only objects of type Ticket
     * @post the player's tickets are updated to the given list
     *
     * @param tickets the new List of Ticket objects
     */
    void setTickets(List<Ticket> tickets);

    /**
     * Sets the Player object the view draws info from
     *
     * @pre player is a properly initialized Player
     * @post View elements match the player, including player color and claimed routes
     *
     * @param player the Player object representing the client's player
     */
    void setPlayer(Player player);

    /**
     * Sets the number of points for the player
     *
     * @pre points is an int >= 0
     * @post the View element for points is updated
     *
     * @param points the new points total
     */
    void setPoints(int points);

    /**
     * Sets the number of trains for the player
     *
     * @pre trains is an int >= 0
     * @post the View element for trains is updated
     *
     * @param trains the new points total
     */
    void setTrains(int trains);

    /**
     * Sets the number of cards remaining in deck
     *
     * @pre size is an int >= 0
     * @post the View element for deck size is updated
     *
     * @param size the new deck size
     */
    void setDeckSize(int size);

    /**
     * Sets the number of cards remaining in ticket deck
     *
     * @pre size is an int >= 0
     * @post the View element for deck size is updated
     *
     * @param size the new deck size
     */
    void setTicketDeckSize(int size);

    /**
     * Updates the provided Route's color and data text
     *
     * @pre route is a Route that is part of the provided map
     * @post route's color and text are updated to reflect who claimed them
     *
     * @param route the Route corresponding to the view route to be updated
     */
    void updateRoute(Route route);

    /**
     *
     * @param turn
     */
    void setTurn(boolean turn);

    void setLastTurn(boolean lastTurn);

    /**
     * Sends a message to the player in the form of a toast
     * @param toast a String containing the toast to send
     */
    void sendToast(String toast);

    /**
     * Chooses cards to use when claiming a grey route
     * @param number the number of cards to select
     * @return a list of card objects to use
     */
    List<Card> chooseCards(int number);
}
