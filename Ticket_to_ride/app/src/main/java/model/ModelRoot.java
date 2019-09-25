package model;

import android.os.Bundle;

import com.obfuscation.ttr_phase1b.activity.PresenterFacade;

import java.util.ArrayList;
import java.util.List;

import communication.GameClient;
import communication.LobbyGame;
import communication.Route;
import communication.Ticket;

/**
 * Created by hao on 10/5/18.
 */

public class ModelRoot {

    private static ModelRoot modelRoot;

    private static List<IModelObserver> observerCollection;

    public static ModelRoot getInstance(){
        if (modelRoot == null) {
            modelRoot = new ModelRoot();
            observerCollection = new ArrayList<>();
        }

        return modelRoot;
    }

    private String authToken;
    private ArrayList<LobbyGame> gameLobbies;
    private GameClient game;
    private LobbyGame lobbyGame;
    private DisplayState displayState;
    private String userName;

    private ArrayList<Ticket> ticketsWanted;

    public ModelRoot() {
        this.displayState = DisplayState.GAMELOBBYLIST;
    }

    public void setAuthToken (String s) {
        authToken = s;
    }

    public String getAuthToken () {
        return authToken;
    }

    public ArrayList<LobbyGame> gameLobbies() {
        if (gameLobbies == null) {
            gameLobbies = new ArrayList<LobbyGame>();
        }
        return gameLobbies;
    }

    public void setGame(GameClient game) {
        System.out.println("game object is changeing");
        if(this.game == null) {
            System.out.println("previous is null");
        }
        else {
            System.out.println(this.game.toString());
        }
        System.out.println("new game is: ");
        System.out.println(game.toString());
        this.game = game;
    }

    public void setGame(LobbyGame lobbyGame) {
        GameClient gameClient = new GameClient(lobbyGame.getGameID(),userName);
    }

    public ArrayList<LobbyGame> getLobbyGames() {
        return gameLobbies;
    }

    public void setGameLobbies(ArrayList<LobbyGame> gameLobbies) {
        this.gameLobbies = gameLobbies;
    }

    public void removeLobbybyID(String Id) {
        for (LobbyGame gameLobby: gameLobbies) {
            if (gameLobby.getGameID().equals(Id)) {
                gameLobbies.remove(gameLobby);
            }
        }
    }

    public GameClient getGame(){
        return game;
    }

    public LobbyGame removeLobbyByID(String id) {
        for (LobbyGame gl: gameLobbies) {
            if (gl.getGameID().equals(id)) {
                return gl;
            }
        }
        return null;
    }
    public DisplayState getDisplayState() {
        return displayState;
    }

    public void setDisplayState(DisplayState displayState) {
        this.displayState = displayState;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Ticket> getTicketsWanted() {
        if (ticketsWanted == null) {
            ticketsWanted = new ArrayList<Ticket>();
        }
        return ticketsWanted;
    }

    public void setTicketsWanted(ArrayList<Ticket> ticketsWanted) {
        this.ticketsWanted = ticketsWanted;
    }

    public LobbyGame getLobbyGame() {
        return lobbyGame;
    }

    public void setLobbyGame(LobbyGame lobbyGame) {
        this.lobbyGame = lobbyGame;
    }

    public static void updateRoute(Route r){
        Bundle args = new Bundle();
        args.putSerializable("route", r);

        PresenterFacade.getInstance().updatePresenter(r);
    }
}
