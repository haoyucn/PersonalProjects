package communication;

/**
 * Created by urimaj on 11/14/18.
 */

public class PlayerStats {

    int rank;
    String name;
    int totalPoint;
    int winnedPoint;
    int lostPoint;
    int pathNum;
    boolean hasLongestPath;

    public PlayerStats(String name, int totalPoint, int winnedPoint, int lostPoint, int pathNum) {
        this.name = name;
        this.totalPoint = totalPoint;
        this.winnedPoint = winnedPoint;
        this.lostPoint = lostPoint;
        this.pathNum = pathNum;
        this.hasLongestPath = false;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    public int getWinnedPoint() {
        return winnedPoint;
    }

    public void setWinnedPoint(int winnedPoint) {
        this.winnedPoint = winnedPoint;
    }

    public int getLostPoint() {
        return lostPoint;
    }

    public void setLostPoint(int lostPoint) {
        this.lostPoint = lostPoint;
    }

    public int getPathNum() {
        return pathNum;
    }

    public void setPathNum(int pathNum) {
        this.pathNum = pathNum;
    }

    public boolean isHasLongestPath() {
        return hasLongestPath;
    }

    public void setHasLongestPath(boolean hasLongestPath) {
        this.hasLongestPath = hasLongestPath;
    }
}


