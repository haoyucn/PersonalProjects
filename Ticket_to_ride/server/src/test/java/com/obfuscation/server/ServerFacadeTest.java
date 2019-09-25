//package com.obfuscation.server;
//
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import communication.Game;
//import communication.Player;
//import communication.Result;
//
//import static org.junit.Assert.*;
//
///**
// * Created by jalton on 10/8/18.
// */
//public class ServerFacadeTest {
//
//    ServerFacade facade = ServerFacade.getInstance();
//
//    static final String KEY = "masterKey";
//
//    @Test
//    public void login() throws Exception {
//        //Phase 1: test invalid input
//        assertEquals(facade.Login(null, null).isSuccess(), false);
//        assertEquals(facade.Login(null, "1234").isSuccess(), false);
//        assertEquals(facade.Login("1234", null).isSuccess(), false);
//
//        facade.Register("John", "john");
//
//        //Invalid password
//        assertEquals(facade.Login("John", "notjohn").isSuccess(), false);
//        //Invalid username
//        assertEquals(facade.Login("NotJohn", "notjohn").isSuccess(), false);
//
//        //Phase 3: test valid login
//        assertEquals(facade.Login("John", "john").isSuccess(), true);
//        assertEquals(facade.Login("John", "john").isSuccess(), true);
//
//    }
//
//    @Test
//    public void register() throws Exception {
//        //Phase 1: test invalid input
//        assertEquals(facade.Register(null, "1234").isSuccess(), false);
//        assertEquals(facade.Register("1234", null).isSuccess(), false);
//
//        //Phase 2: test registration and re-registration
//        assertEquals(facade.Login("James", "james").isSuccess(), false);
//        //valid registration
//        assertEquals(facade.Register("James", "james").isSuccess(), true);
//        assertEquals(facade.Login("James", "james").isSuccess(), true);
//        //cannot re-register
//        assertEquals(facade.Register("James", "james").isSuccess(), false);
//    }
//
//    @Test
//    public void joinGame() throws Exception {
//        //Phase 1: test invalid input
//        ArrayList<Player> players = new ArrayList<>();
//        facade.Register("Jill", "Jill");
////        Game game = new Game("Jill's game", "Jill", players, 2);
////        facade.CreateGame(game, "masterKey");
//
//        //Test valid join
//        Result True = new Result(true, null, null);
//        facade.Register("Jack", "jack");
////        assertEquals(facade.JoinGame("Jack", "Jill's game", "masterKey").isSuccess(), true);
//
//        //Test invalid (too many players)
//        facade.Register("Harold", "harold");
////        assertEquals(facade.JoinGame("Harold", "Jill's game", "masterKey").isSuccess(), false);
//
//        //Test invalid (game started)
////        Game game5 = new Game("5", "Harold", players, 3);
////        facade.JoinGame("Jack", "5", KEY);
////        assertEquals(facade.JoinGame("Jack", "5", KEY).isSuccess(), false);
//        facade.StartGame("5", KEY);
//
////        assertEquals(facade.JoinGame("Jill", "5", KEY).isSuccess(), false);
//
//
//    }
//
//    @Test
//    public void leaveGame() throws Exception {
//        facade.Register("Bot", "bob");
//        ArrayList<Player> list = new ArrayList<>();
////        Game game = new Game("Bot's game", "Bot", list, 2);
////        facade.CreateGame(game, KEY);
//
//        facade.Register("Kyle", "kyle");
//        assertEquals(facade.LeaveGame("Kyle", "Bot's game", KEY).isSuccess(), false);
//
////        facade.JoinGame("Kyle", "Bot's game", KEY);
//        assertEquals(facade.LeaveGame("Kyle", "Bot's game", KEY).isSuccess(), true);
//
//
//    }
//
//    @Test
//    public void createGame() throws Exception {
//        //Phase 1: test invalid input
//        Game game = null;
////        assertEquals(facade.CreateGame(game, "masterKey").isSuccess(), false);
//
////        game = new Game(null, null, null, 0);
////        assertEquals(facade.CreateGame(game, "masterKey").isSuccess(), false);
//        game.setGameID("Joe's Game");
////        assertEquals(facade.CreateGame(game, "masterKey").isSuccess(), false);
//        facade.Register("Joe", "joe");
////        game.setHost("Joe");
////        assertEquals(facade.CreateGame(game, "masterKey").isSuccess(), false);
////        game.setPlayers(new ArrayList<Player>());
////        assertEquals(facade.CreateGame(game, "masterKey").isSuccess(), false);
////        game.setMaxPlayers(6);
////        assertEquals(facade.CreateGame(game, "masterKey").isSuccess(), false);
////        game.setMaxPlayers(3);
////        assertEquals(facade.CreateGame(game, "notMasterKey").isSuccess(), false);
////        assertEquals(facade.CreateGame(game, "masterKey").isSuccess(), true);
//
//    }
//
//    @Test
//    public void startGame() throws Exception {
//        facade.Register("Bob", "bob");
//        ArrayList<Player> list = new ArrayList<>();
////        Game game = new Game("Bob's game", "Bob", list, 2);
////        facade.CreateGame(game, KEY);
//
//        assertEquals(facade.StartGame("Bob's game", KEY).isSuccess(), false);
//
//        facade.Register("Hob", "hob");
////        facade.JoinGame("Hob", "Bob's game", KEY);
//
//        assertEquals(facade.StartGame("Bob's game", KEY).isSuccess(), true);
//
//
//    }
//
//    @Test
//    public void getGameList() throws Exception {
//        facade.Register("M", "m");
//        ArrayList<Player> players = new ArrayList<>();
//
////        Game game1 = new Game("1", "M", players, 2);
////        Game game2 = new Game("2", "M", players, 3);
//
////        facade.CreateGame(game1, KEY);
////        facade.CreateGame(game2, KEY);
//        List<Game> gameList = (ArrayList<Game>) facade.GetLobbyList(KEY).getData();
//        assertEquals(gameList.size(), 4);
//        assertEquals(gameList.get(2).getGameID(), "1");
//    }
//
//    @Test
//    public void getGame() throws Exception {
//        facade.Register("M", "m");
//        ArrayList<Player> players = new ArrayList<>();
////        Game game1 = new Game("1", "M", players, 2);
////        facade.CreateGame(game1, KEY);
//
////        Game game = (Game) facade.GetGame("1", KEY).getData();
//
////        assertEquals(game.getHost(), "M");
//    }
//
//    @Test
//    public void checkGameList() throws Exception {
//        facade.Register("M", "m");
//        ArrayList<Player> players = new ArrayList<>();
//        int up = (Integer) facade.CheckGameList(KEY).getData();
//
////        Game game3 = new Game("3", "M", players, 3);
////        facade.CreateGame(game3, KEY);
//
//        int up2 = (Integer) facade.CheckGameList(KEY).getData();
//
//        assertEquals(up, (up2 - 1));
//    }
//
//    @Test
//    public void checkGame() throws Exception {
//        facade.Register("M", "m");
//        ArrayList<Player> players = new ArrayList<>();
//        Game game4 = new Game("4", "M", players, 3);
//        facade.CreateGame(game4, KEY);
//
//        int up = (Integer) facade.CheckGame(KEY, "4").getData();
//
//        facade.Register("N", "n");
//        facade.JoinGame("N", "4", KEY);
//
//        int up2 = (Integer) facade.CheckGame(KEY, "4").getData();
//
//        assertEquals(up, (up2 - 1));
//
//    }
//
//}