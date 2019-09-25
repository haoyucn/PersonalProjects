package doa;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import communication.LobbyGame;
import communication.Player;
import dao.ILobbyDao;
import tsvdao.TSVDaoFactory;
import sqldao.SQLFactory;

public class LobbyDaoTest {

    @Test
    public void TSVTest() {
        System.out.println("Start");

        ILobbyDao dao = new TSVDaoFactory().getLobbyDao();
        dao.clear();
        ArrayList<Player> players;
        List<LobbyGame> games;

        players = new ArrayList<>();
        players.add(new Player("bob"));
        players.add(new Player("babe"));
        players.add(new Player("buddy"));
        players.add(new Player("buns"));

        LobbyGame game = new LobbyGame("bob", "mygameid", players, 3);
        LobbyGame comp = new LobbyGame("bob", "mygameid", players, 3);
        assert(comp.equals(game));

        dao.addLobby(game.getGameID(), game);
        games = dao.getLobbies();
        assert(games.size() == 1);
        assert(games.get(0).equals(game));

        players = new ArrayList<>();
        players.add(new Player("bob"));
        players.add(new Player("babe"));
        players.add(new Player("buddy"));
        game.setMaxPlayers(5);
        game.setPlayers(players);
        dao.updateLobby(game.getGameID(), game);
        games = dao.getLobbies();
        assert(games.size() == 1);
        assert(games.get(0).equals(game));

        dao.removeLobby(game.getGameID());
        games = dao.getLobbies();
        assert(games.size() == 0);

        System.out.println("No problems");
    }

    @Test
    public void SQLTest() {
        System.out.println("Start");

        ILobbyDao dao = new SQLFactory().getLobbyDao();
        dao.clear();
        ArrayList<Player> players;
        List<LobbyGame> games;

        players = new ArrayList<>();
        players.add(new Player("bob"));
        players.add(new Player("babe"));
        players.add(new Player("buddy"));
        players.add(new Player("buns"));

        LobbyGame game = new LobbyGame("bob", "mygameid", players, 3);
        LobbyGame comp = new LobbyGame("bob", "mygameid", players, 3);
        assert(comp.equals(game));

        dao.addLobby(game.getGameID(), game);
        games = dao.getLobbies();
        assert(games.size() == 1);
        assert(games.get(0).equals(game));

        players = new ArrayList<>();
        players.add(new Player("bob"));
        players.add(new Player("babe"));
        players.add(new Player("buddy"));
        game.setMaxPlayers(5);
        game.setPlayers(players);
        dao.updateLobby(game.getGameID(), game);
        games = dao.getLobbies();
        assert(games.size() == 1);
        assert(games.get(0).equals(game));

        dao.removeLobby(game.getGameID());
        games = dao.getLobbies();
        assert(games.size() == 0);

        System.out.println("No problems");
    }

}
