package communication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jalton on 10/1/18.
 */

public class Player implements IPlayer, Serializable{
    private String playerName;
    private Integer point;
    private ArrayList<String> claimedRoutesID;
    private GameColor playerColor;
    private Integer trainNum;

    public Player() {
        playerName = "";
        trainNum = 40;
        point = 0;
        playerColor = GameColor.PLAYER_BLACK;
        claimedRoutesID = new ArrayList<>();
    }

    public Player(String playerName) {
        this.playerName = playerName;
        trainNum = 40;
        point = 0;
        playerColor = GameColor.PLAYER_BLACK;
        claimedRoutesID = new ArrayList<>();
    }

    public Player(String playerName, int point, GameColor playerColor) {
        this.playerName = playerName;
        trainNum = 40;
        this.point = point;
        this.playerColor = playerColor;
        claimedRoutesID = new ArrayList<>();
    }

    public ArrayList<String> getClaimedRoutes() {
        return claimedRoutesID;
    }

    public void setClaimedRoutes(ArrayList<String> claimedRoutesID) {
        this.claimedRoutesID = claimedRoutesID;
    }

    public boolean addRouteAsClaimed(String r) {
        if (claimedRoutesID.contains(r)) {
            return false;
        }
        claimedRoutesID.add(r);
        return true;
    }

    public boolean checkRouteIfClaimed(String routeID) {
        return claimedRoutesID.contains(routeID);
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public void addPoint(int point) {
        this.point += point;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getTrainNum(){
        return trainNum;
    }

    public void setTrainNum(int newNum){
        trainNum = newNum;
    }

    public void subtractTrain(int numUsed) {
        trainNum = trainNum - numUsed;
    }

    public GameColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(GameColor playerColor) {
        this.playerColor = playerColor;
    }

    public int getCardNum() {
        return 0;
    }

    public int getTicketNum() {
        return 0;
    }

    @Override
    public PlayerIdentity getIdentity() {
        return PlayerIdentity.PLAYER;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerName='" + playerName + '\'' +
                ", trainNum=" + trainNum +
                ", point=" + point +
                ", playerColor=" + playerColor +
                ", claimedRoutesID=" + claimedRoutesID +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (playerName != null ? !playerName.equals(player.playerName) : player.playerName != null)
            return false;
        if (point != null ? !point.equals(player.point) : player.point != null) return false;
        if (claimedRoutesID != null ? !claimedRoutesID.equals(player.claimedRoutesID) : player.claimedRoutesID != null)
            return false;
        if (playerColor != player.playerColor) return false;
        return trainNum != null ? trainNum.equals(player.trainNum) : player.trainNum == null;
    }

    @Override
    public int hashCode() {
        int result = playerName != null ? playerName.hashCode() : 0;
        result = 31 * result + (point != null ? point.hashCode() : 0);
        result = 31 * result + (claimedRoutesID != null ? claimedRoutesID.hashCode() : 0);
        result = 31 * result + (playerColor != null ? playerColor.hashCode() : 0);
        result = 31 * result + (trainNum != null ? trainNum.hashCode() : 0);
        return result;
    }
}
