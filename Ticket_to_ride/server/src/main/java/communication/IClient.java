package communication;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jalton on 10/1/18.
 */

public interface IClient {


    void updateGameLobbyList(String gameID);

    //TODO: this functon should be replaced with function that has specific purpose, like OpponentClaimRoute()...
    void updateGame(String gameID);

    /**
     * initialize game with colors and turn orders. Turn orders starts from index 0 (playerList), and increments
     * @param game
     */
    void initializeGame(GameClient game);

    void updatePlayerPoints(String gameID, String plyerID, Integer points);

    /**
     * gets 2 (4 at the start of the game) cards frmo the trainCardDeck
     * @param gameID
     * @param trainCards (2 cards from the deck)
     */
    void updateTrainCards(String gameID, List<Card> trainCards);

    //ticket for choosing, always three
    void updateTickets(String gameID, List<Ticket> tickets);

    void updateOpponentTrainCards(String gameID, String playerID, Integer cardNum);

    void updateOpponentTrainCars(String gameID, String playerID, Integer carNum);

    void updateOpponentTickets(String gameID, String playerID, Integer cardNum);

    void updateTrainDeck(String gameID, List<Card> faceCards, Integer downCardNum);

    void updateDestinationDeck(String gameID, Integer cardNum);

    void claimRoute(String gameID, String playerID, String routeID);

    void updateChat(String gameID, Message m);

    void updateGameHistory(String gameID, List<GameHistory> gh);

    void lastRound(String gameID);

    void endGame(String gameID, List<PlayerStats> stats);

    void updateTurns(String gameID, String userName);
}
