package communication;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by jalton on 10/24/18.
 */

public class Route implements Serializable{
    private City city1;
    private City city2;
    private Integer length;
    private GameColor color;
    private Player claimedBy = null; //TODO : use username? or playeropponenet?
    private double[] midPoint;
    protected String routeID;

    private boolean isDual;
    private String sibling;
    private boolean isSibClaimed;

    public Route(City city1, City city2, int length, GameColor color) {
        this.city1 = city1;
        this.city2 = city2;
        this.length = length;
        this.color = color;
        isDual = false;
        sibling = null;
        isSibClaimed = false;

        midPoint = new double[] {0.0,0.0};

        midPoint[0] = (city1.getLat()+city2.getLat())/2.0;
        midPoint[1] = (city1.getLng()+city2.getLng())/2.0;
        routeID = city1.getName() + "-" + city2.getName();
    }

    public void setDual(boolean dual) {
        isDual = dual;
    }

    public boolean isDual() {
        return isDual;
    }

    public City getCity1() {
        return city1;
    }

    public void setCity1(City city1) {
        this.city1 = city1;
    }

    public City getCity2() {
        return city2;
    }

    public void setCity2(City city2) {
        this.city2 = city2;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public GameColor getColor() {
        return color;
    }

    public void setColor(GameColor color) {
        this.color = color;
    }

    public Player getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(Player claimedBy) {
        this.claimedBy = claimedBy;
    }

    public double[] getMidPoint() {
        return midPoint;
    }

    public void setMidPoint(double[] midPoint) {
        this.midPoint = midPoint;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public double[] getStartPos() {
        return getPos(city1);
    }

    public double[] getEndPos() {
        return getPos(city2);
    }

    private double[] getPos(City city) {
        double[] pos = new double[2];
        pos[0] = city.getLat();
        pos[1] = city.getLng();
        return pos;
    }

    public void setSibling(String sibling) {
        isDual = true;
        this.sibling = sibling;
    }

    public String getSibling() {
        return sibling;
    }

    public void setSibClaimed(boolean b) {
        isSibClaimed = b;
    }

    public boolean isSibClaimed() {
        return isSibClaimed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        if(!Objects.equals(city1, route.city1) || !Objects.equals(city2, route.city2) ||
            !Objects.equals(length, route.length) || !(color == route.color) ||
            !Objects.equals(claimedBy, route.claimedBy) ||
            !Arrays.equals(midPoint, route.midPoint) || !Objects.equals(routeID, route.routeID))
            return false;
        System.out.println(isDual == route.isDual &&
                isSibClaimed == route.isSibClaimed &&
                Objects.equals(sibling, route.sibling));
        return isDual == route.isDual &&
                isSibClaimed == route.isSibClaimed &&
                Objects.equals(sibling, route.sibling);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(city1, city2, length, color, claimedBy, routeID, isDual, sibling, isSibClaimed);
        result = 31 * result + Arrays.hashCode(midPoint);
        return result;
    }
}
