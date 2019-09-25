package communication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jalton on 10/3/18.
 */

public class ActiveUser {

    private Player player;
    private String authToken;
    private List<String> joinedGames;

    public ActiveUser(Player player, String authToken) {
        this.player = player;
        this.authToken = authToken;
        joinedGames = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiveUser that = (ActiveUser) o;

        if (player != null ? !player.equals(that.player) : that.player != null) return false;
        if (authToken != null ? !authToken.equals(that.authToken) : that.authToken != null)
            return false;
        return joinedGames != null ? joinedGames.equals(that.joinedGames) : that.joinedGames == null;
    }

    @Override
    public int hashCode() {
        int result = player != null ? player.hashCode() : 0;
        result = 31 * result + (authToken != null ? authToken.hashCode() : 0);
        result = 31 * result + (joinedGames != null ? joinedGames.hashCode() : 0);
        return result;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Player getPlayer() {
        return player;
    }

    public String getAuthToken() {
        return authToken;
    }

    public List<String> getJoinedGames() {
        return joinedGames;
    }

    void joinGame(String gameID){
        joinedGames.add(gameID);
    }
}
