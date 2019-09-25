package communication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jalton on 10/1/18.
 */


/**
 * the interface for ServerFacade and ServerProxy
 *
 * @invariant non
 */
public interface IServer {

    /**
     * Perform a login process and return the result
     *
     * @pre     the id is already registered and password is correctly provided.
     * @post    a authtoken will be assigned to it, and stored in database
     *
     * @param id        a String containing the username
     * @param password  a String containing the password
     *
     * @return          a Result object containing the authToken
     */
    Result Login(String id, String password);

    /**
     * Perform a Register process and return a result
     *
     * @pre     the id can't be already registerd
     *          id and password can't be empty
     * @post    id and password are stored in the database
     *          id are signed in and a authtoken is assigned to this id
     *
     * @param id        a String containing the username
     * @param password  a String containing the password
     * @return          a Result object containing the authToken
     */
    Result Register(String id, String password);

    /**
     * Join the user to a game lobby
     *
     * @pre     username of the user already exist
     *          gameID of the game is already exist, still has empty spots, havent start
     *          authToken which is still valid and exist, and belong to this user
     * @post    join the user in the game
     *
     *
     * @param id        a String object containing the userName
     * @param gameID    a String object containing the gameID
     * @param authToken a String object with the username's authToken
     * @return          a Result object containing a boolean indicating success
     */
    Result JoinLobby(String id, String gameID, String authToken);

    /**
     * let the player leave the game
     *
     * @pre     username of the user already exist
     *          gameID of the game is already exist. Game has the user
     *          authToken which is still valid and exist and belong to this user
     *
     * @post    user leave the game
     *          game no long have user in the list of active players
     *
     * @param id
     * @param gameID
     * @param authToken
     * @return
     */
    Result LeaveGame(String id, String gameID, String authToken);

    Result LeaveLobbyGame(String id, String gameID, String authToken);

    /**
     * get game properties as name and number of players allowed in the game, then create it
     *
     * @pre     game's name is different from all other games which has been created previously
     *          number of players allowed in game has to be between 3 to 7
     *          authToken which is still valid and exist
     * @post    game is created with all the sepcification of the parameter game.
     *
     * @param lobbyGame      the lobbyGame object, containing the game name and max player count
     * @param authToken a String object containing the users authToken
     * @return          a Result object containing a boolean indication success
     */
    Result CreateLobby(LobbyGame lobbyGame, String authToken);

    /**
     * the game owner can start the game, which will notify all other players in the room
     *
     * @pre     the gameID of the game is still in the database
     *          that game has to have enough amount of players, not over
     *          the authToken is still valid and its user owns the game
     * @post    game starts and notify all other players
     *
     * @param gameID    a String object containing the gameID of the game to be started
     * @param authToken a String object containing the user's authToken
     * @return          a Result object containing success boolean
     */
    Result StartGame(String gameID, String authToken);


    /**
     * Get a list of command to update a game to the state in the Server
     *
     * @pre     authToken has to be valid and belong to a current user in the game.
     *          gameID is to belong to a game which is already starts
     *
     * @post    get a list of command to run to update to the state of game in Server
     *
     * @param authToken a String object containing the user's authToken
     * @param gameID    a String object containing the gameID of the game to be started
     * @param state     an Integer that record the number of command has been processed
     * @return          a Result object containing all update commands
     */

    Result GetUpdates(String authToken, String gameID, Integer state);

    /**
     * User claim a route on the map with his cards
     *
     * @pre     the game that this user belong has to be already exist and started
     *          the routeID of the route has to be already in the map and hasnt been claimed
     *          the cards havent been used
     *          authToken has to be valid and belong to a current user in the game.
     *
     * @post    the route is claimed by the user
     *
     * @param routeID    a String object containing ID of the route wanted to claim
     * @param cards      a List object containing cards used to claim this route
     * @param authToken  a String object containing the user's authToken
     * @return           a Result object a boolean to indicate the success or failure
     */
    Result ClaimRoute(String gameID, String routeID, List<Card> cards, String authToken);

    /**
     * user draw Train Card from the face up deck or face down deck
     *
     * @pre     the game of this user currently belong is already started
     *          by the time it call this, it has to be the user's turn, and this function havent been called more than 2 twice in this user round
     *          the index has to be less than the number of cards remain, and between 0 and 4
     *
     * @post    the user get the card he call to draw.
     *          this card is no longer in the deck of havent-drawn-cards
     *
     * @param index     an Integer of the index of the cards in the Train Card deck
     * @param authToken a String object containing the user's authToken
     * @return          a Result object contain a card.
     */
    Result DrawTrainCard(String gameID, Integer index, String authToken);

    /**
     * user recieve three destination tickets blindly from the deck to choose from
     *
     * @pre     the game this user currently belong has been started
     *          it is this player's turn, which player identified by this authToken.
     *          This function havent been called before in this turn
     *          This authToken is still valid
     * @post    there are three dest tickets being removed from the top of DestTicket deck
     *          and giving to user to choose from
     *
     * @param authToken a String object containing the user's authToken
     * @return          a Result object contain three ticktes.
     */
    Result GetTickets(String gameID, String authToken);

    /**
     * user put back the tickets he doesnt want to keep
     *
     * @pre      the game this user currently belong has been started
     *          it is this player's turn, which player identified by this authToken.
     *          DrawDestTickets() function has been called
     *          This function havent been called before in this turn
     *          This authToken is still valid
     *          the number of tickets should be between 0 and 2 .
     *          non of these tickets are in the DestTicket deck
     *
     * @post    a list of tickets being added to the bottom of DestTicket deck
     *          the card which being sent by DrawDestTickets, but not returned will be assign to this user
     *
     * @param ticketsToKeep   a list of tickets that user want to keep
     * @param authToken a String of unique authToken
     * @return          a result object containing an boolean indicate of success of this call
     */
    Result ReturnTickets(String gameID, String authToken, List<Ticket> ticketsToKeep);


    //TODO: functions below should be consided deprecated

    /**
     *
     *
     * @pre
     * @param authToken a String object containing the authToken of the user
     * @return          a Result object containing the game list
     */
    Result GetLobbyList(String authToken);

    /**
     *
     * @param gameID    a String object containing the game ID
     * @param authToken a String object containing the user's authToken
     * @return          a Result object containing the Game object
     */
    Result GetLobby(String gameID, String authToken);

    /**
     *
     * @param authToken
     * @return          a Result object containing the gameList version number
     */
    Result CheckGameList(String authToken);

    /**
     *
     * @param authToken
     * @param gameID
     * @return          a Result object containing the game version number
     */
    Result CheckGame(String authToken, String gameID, Integer state);

    /**
     *
     * @param authToken
     * @param gameID
     * @return          a Result object containing the game version number
     */
    Result CheckGameLobby(String authToken, String gameID);

    Result SendMessage(String authToken, String gameID, Message message);

//    Result NotifyLastRound(String authToken, String gameID);

    Result EndTurn(String gameID, String authToken);
}
