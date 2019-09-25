package communication;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static communication.GameColor.*;

/**
 * Created by jalton on 10/24/18.
 */

public class GameMap {
    private List<City> cities;
    private List<Route> routes;

    public GameMap() {
        cities = GameFactory.getInstance().getCities();
        routes = GameFactory.getInstance().getRoutes();
    }

    public List<City> getCities() {
        return cities;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void claimRoute(Route route, Player player) {
        Route r = routes.get(routes.indexOf(route));

        r.setClaimedBy(player);
        r.setColor(player.getPlayerColor());
    }

    public Route getRouteByRouteId(String routeID) {
        for(Route r: routes) {
            if (r.getRouteID().equals(routeID)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameMap gameMap = (GameMap) o;
        if (cities != null ? !cities.equals(gameMap.cities) : gameMap.cities != null) return false;
        return true;
//        return routes != null ? routes.equals(gameMap.routes) : gameMap.routes == null;
    }

    @Override
    public int hashCode() {
        int result = cities != null ? cities.hashCode() : 0;
        result = 31 * result + (routes != null ? routes.hashCode() : 0);
        return result;
    }
}
