package communication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by urimaj on 11/16/18.
 */

public class MapGraph{

//
//    public class Main {
//        public static void main(String[] args) {
//            MapGraph mapGraph = new MapGraph();
//            mapGraph.addPath("Bob", new Route(new City("A", 2, 2), new City("B", 2, 3), 2,GameColor.BLACK));
//            mapGraph.addPath("Bob", new Route(new City("B", 2, 2), new City("C", 2, 3), 2,GameColor.BLACK));
//            mapGraph.addPath("Bob", new Route(new City("C", 2, 2), new City("A", 2, 3), 2,GameColor.BLACK));
//            mapGraph.addPath("Bob", new Route(new City("C", 2, 2), new City("D", 2, 3), 6,GameColor.BLACK));
//            mapGraph.addPath("Bob", new Route(new City("A", 2, 2), new City("E", 2, 3), 3,GameColor.BLACK));
//            mapGraph.addPath("Bob", new Route(new City("W", 2, 2), new City("H", 2, 3), 3,GameColor.BLACK));
//
//            HashMap<String, Integer> scores = mapGraph.findLongestPath();
//            for (String s : scores.keySet()) {
//                System.out.println(s);
//                System.out.println(scores.get(s));
//            }
//        }
//    }


    //username to routes
    HashMap<String, ArrayList<Route>> graph = new HashMap<>();
    Integer maxLength = Integer.valueOf(0);
    Boolean hasPath = false;

    public void addPath(String username, Route route) {
        if (graph.containsKey(username)) {
            graph.get(username).add(route);
        }
        else {
            graph.put(username, new ArrayList<>());
            graph.get(username).add(route);
        }
    }

    public HashMap<String, Integer>  findLongestPath() {
        HashMap<String, Integer> paths = new HashMap<>();
        for (String key : graph.keySet()) {
            maxLength= 0;
            paths.put(key, findPath(key));
        }
        return paths;
    }

    public boolean hasPath(String username, String city1, String city2) {
        hasPath = false;
        ArrayList<Route> routes = graph.get(username);
        if (graph.get(username) == null) {
            routes = new ArrayList<>();
        }
        System.out.println(city1 + " " + city2);
        for (int j = 0; j < routes.size(); j++) {
            System.out.println(routes.get(j).getCity1().toString() + " " + routes.get(j).getCity2().toString());
            if (routes.get(j).getCity1().getName().equals(city1)) {
                System.out.println("EEEEE");
                ArrayList<Boolean> visited = new ArrayList<>();
                for (int i = 0; i < routes.size(); i++) {
                    visited.add(new Boolean(false));
                }
                visited.set(j, true);
            System.out.println("STARTING AT " + routes.get(j).getCity1().toString());
                dfs2(city1, visited, routes, routes.get(j).getLength(), city2);

                visited = new ArrayList<>();
                for (int i = 0; i < routes.size(); i++) {
                    visited.add(new Boolean(false));
                }
                visited.set(j, true);
            System.out.println("STARTING AT " + routes.get(j).getCity1().toString());
                dfs2(routes.get(j).getCity2().getName(), visited, routes, routes.get(j).getLength(), city2);
            }
            else if (routes.get(j).getCity2().getName().equals(city1)){
                ArrayList<Boolean> visited = new ArrayList<>();
                for (int i = 0; i < routes.size(); i++) {
                    visited.add(new Boolean(false));
                }
                visited.set(j, true);
                System.out.println("STARTING AT " + routes.get(j).getCity1().getName());
                dfs2(routes.get(j).getCity2().getName(), visited, routes, routes.get(j).getLength(), city2);

                visited = new ArrayList<>();
                for (int i = 0; i < routes.size(); i++) {
                    visited.add(new Boolean(false));
                }
                visited.set(j, true);
                System.out.println("STARTING AT " + routes.get(j).getCity1().getName());
                dfs2(routes.get(j).getCity1().getName(), visited, routes, routes.get(j).getLength(), city2);
            }
        }
        return hasPath;
    }

    int findPath(String username) {
        ArrayList<Route> routes = graph.get(username);
        for (int j = 0; j < routes.size(); j++) {
            ArrayList<Boolean> visited = new ArrayList<>();
            for (int i = 0; i < routes.size(); i++) {
                visited.add(new Boolean(false));
            }
            visited.set(j, true);
//            System.out.println("STARTING AT " + routes.get(j).getCity1().toString());
            dfs(routes.get(j).getCity1().getName(), visited, routes, routes.get(j).getLength());

            //other way
            visited = new ArrayList<>();
            for (int i = 0; i < routes.size(); i++) {
                visited.add(new Boolean(false));
            }
            visited.set(j, true);
//            System.out.println("STARTING AT " + routes.get(j).getCity2().toString());
            dfs(routes.get(j).getCity2().getName(), visited, routes, routes.get(j).getLength());
        }
        return maxLength;
    }

    void dfs2(String city, ArrayList<Boolean> visited, ArrayList<Route> routes, int depth, String des) {
        if (des.equals(city)) {
            System.out.println("EEEEEEEEEEEEEEEEEEEEEEE");
            hasPath = true;
        }
//        System.out.println("DEPTH " + depth + " MAX : " + maxLength);
        // System.out.println("CCCC " + maxLength + " " + depth);

        for (int i= 0; i < routes.size(); i++) {
            if (!visited.get(i)) {
                if (city.equals(routes.get(i).getCity1().getName())) {
                    visited.set(i, true);
//                    System.out.println("VISITING " + routes.get(i).getRouteID());
//                    System.out.println("CURRENT DEPTH " + depth + " AND LENGTH TO BE ADDED " + routes.get(i).getLength());
                    dfs2(routes.get(i).getCity2().getName(), visited, routes, depth + routes.get(i).getLength(), des);
                }
                else if (city.equals(routes.get(i).getCity2().getName())) {
                    visited.set(i, true);
//                    System.out.println("VISITING " + routes.get(i).getRouteID());
//                    System.out.println("CURRENT DEPTH " + depth + " AND LENGTH TO BE ADDED " + routes.get(i).getLength());
                    dfs2(routes.get(i).getCity1().getName(), visited, routes, depth + routes.get(i).getLength(), des);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapGraph mapGraph = (MapGraph) o;

        if (graph != null ? !graph.equals(mapGraph.graph) : mapGraph.graph != null) return false;
        if (maxLength != null ? !maxLength.equals(mapGraph.maxLength) : mapGraph.maxLength != null)
            return false;
        return hasPath != null ? hasPath.equals(mapGraph.hasPath) : mapGraph.hasPath == null;
    }

    @Override
    public int hashCode() {
        int result = graph != null ? graph.hashCode() : 0;
        result = 31 * result + (maxLength != null ? maxLength.hashCode() : 0);
        result = 31 * result + (hasPath != null ? hasPath.hashCode() : 0);
        return result;
    }

    void dfs(String city, ArrayList<Boolean> visited, ArrayList<Route> routes, int depth) {
        this.maxLength = Math.max(depth, this.maxLength);
//        System.out.println("DEPTH " + depth + " MAX : " + maxLength);
       // System.out.println("CCCC " + maxLength + " " + depth);

        for (int i= 0; i < routes.size(); i++) {
            if (!visited.get(i)) {
                if (city.equals(routes.get(i).getCity1().getName())) {
                    visited.set(i, true);
//                    System.out.println("VISITING " + routes.get(i).getRouteID());
//                    System.out.println("CURRENT DEPTH " + depth + " AND LENGTH TO BE ADDED " + routes.get(i).getLength());
                    dfs(routes.get(i).getCity2().getName(), visited, routes, depth + routes.get(i).getLength());
                }
                else if (city.equals(routes.get(i).getCity2().getName())) {
                    visited.set(i, true);
//                    System.out.println("VISITING " + routes.get(i).getRouteID());
//                    System.out.println("CURRENT DEPTH " + depth + " AND LENGTH TO BE ADDED " + routes.get(i).getLength());
                    dfs(routes.get(i).getCity1().getName(), visited, routes, depth + routes.get(i).getLength());
                }
            }
        }


    }
}
