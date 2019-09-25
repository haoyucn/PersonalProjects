package communication;

/**
 * Created by urimaj on 11/14/18.
 */

public class GameHistory {

    String gameID;

    String playerName;

    String action;

    public GameHistory(String gameID, String playerName, String action) {
        this.gameID = gameID;
        this.playerName = playerName;
        this.action = action;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String toString() {
        return playerName + "_" + action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameHistory that = (GameHistory) o;

        if (gameID != null ? !gameID.equals(that.gameID) : that.gameID != null) return false;
        if (playerName != null ? !playerName.equals(that.playerName) : that.playerName != null)
            return false;
        return action != null ? action.equals(that.action) : that.action == null;
    }

    @Override
    public int hashCode() {
        int result = gameID != null ? gameID.hashCode() : 0;
        result = 31 * result + (playerName != null ? playerName.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }
}
