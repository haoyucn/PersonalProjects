package communication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by urimaj on 11/9/18.
 */

public class LobbyGame {

    private String gameID;
    private String host;
    private int maxPlayers;
    // this is the state that increment when players join in or leave the lobby
    private int playerListVerisonNum;
    private ArrayList<Player> players = new ArrayList<>();

    private boolean started;
    private int versionNum;

    public LobbyGame(String host, String gameID, int maxPlayers) {
        this.host = host;
        this.maxPlayers = maxPlayers;
        this.gameID = gameID;
    }

    public LobbyGame(String gameid, String host, ArrayList<Player> players, int maxPlayers) {
        this.gameID = gameid;
        this.host = host;
        this.players = players;
        this.maxPlayers = maxPlayers;
    }

    public LobbyGame(String host) {
        this.host = host;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public int getPlayerListVerisonNum() {
        return playerListVerisonNum;
    }

    public void setPlayerListVerisonNum(int playerListVerisonNum) {
        this.playerListVerisonNum = playerListVerisonNum;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public void removePlayerByID (String playerID) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerName().equals(playerID)) {
                players.remove(i);
                break;
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyGame lobbyGame = (LobbyGame) o;
        if (maxPlayers != lobbyGame.maxPlayers) return false;
        if (playerListVerisonNum != lobbyGame.playerListVerisonNum) return false;
        if (started != lobbyGame.started) return false;
        if (versionNum != lobbyGame.versionNum) return false;
        if (gameID != null ? !gameID.equals(lobbyGame.gameID) : lobbyGame.gameID != null)
            return false;
        if (host != null ? !host.equals(lobbyGame.host) : lobbyGame.host != null) return false;
        return players != null ? players.equals(lobbyGame.players) : lobbyGame.players == null;
    }

    @Override
    public int hashCode() {
        int result = gameID != null ? gameID.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + maxPlayers;
        result = 31 * result + playerListVerisonNum;
        result = 31 * result + (players != null ? players.hashCode() : 0);
        result = 31 * result + (started ? 1 : 0);
        result = 31 * result + versionNum;
        return result;
    }
}
