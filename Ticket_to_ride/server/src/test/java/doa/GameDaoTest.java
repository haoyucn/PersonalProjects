package doa;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import communication.Card;
import communication.GameColor;
import communication.GameFactory;
import communication.GameServer;
import communication.PlayerUser;
import communication.Ticket;
import dao.IGameDao;
import tsvdao.TSVDaoFactory;
import sqldao.SQLFactory;

public class GameDaoTest {

    @Test
    public void TSVTest() {
        System.out.println("Start");

        IGameDao dao = new TSVDaoFactory().getGameDao();
        dao.clear();

        List<GameServer> games;
        GameServer game = createGame();
        GameServer comp = createGame();
        assert(comp.equals(game));
        dao.addGame(game.getGameID(), game);
        games = dao.getGames();
        assert(games.size() == 1);
        assert(games.get(0).equals(game));

        ArrayList<Ticket> tickets = game.getTickets();
        ArrayList<Ticket> playerTickets;
        for (PlayerUser player : game.getPlayers()) {
            playerTickets = player.getTickets();
            for (int i = 0; i < 1; i++) {
                playerTickets.add(tickets.get(0));
                tickets.remove(0);
            }
            player.setTickets(playerTickets);
        }
        dao.updateGame(game.getGameID(), game);
        games = dao.getGames();
        assert(games.size() == 1);
        assert(games.get(0).equals(game));

        dao.removeGame(game.getGameID());
        games = dao.getGames();
        assert(games.size() == 0);

        System.out.println("No problems");
    }

    @Test
    public void SQLTest() {
        System.out.println("Start");

        IGameDao dao = new SQLFactory().getGameDao();
        dao.clear();

        List<GameServer> games;
        GameServer game = createGame();
        GameServer comp = createGame();
        assert(comp.equals(game));
        dao.addGame(game.getGameID(), game);
        games = dao.getGames();
        assert(games.size() == 1);
        assert(games.get(0).equals(game));

        ArrayList<Ticket> tickets = game.getTickets();
        ArrayList<Ticket> playerTickets;
        for (PlayerUser player : game.getPlayers()) {
            playerTickets = player.getTickets();
            for (int i = 0; i < 1; i++) {
                playerTickets.add(tickets.get(0));
                tickets.remove(0);
            }
            player.setTickets(playerTickets);
        }
        dao.updateGame(game.getGameID(), game);
        games = dao.getGames();
        assert(games.size() == 1);
        assert(games.get(0).equals(game));

        dao.removeGame(game.getGameID());
        games = dao.getGames();
        assert(games.size() == 0);

        System.out.println("No problems");
    }

    private GameServer createGame() {
        ArrayList<PlayerUser> players = new ArrayList<>();
        players.add(new PlayerUser("bob"));
        players.add(new PlayerUser("babe"));
        players.add(new PlayerUser("buddy"));
        players.add(new PlayerUser("buns"));

        GameServer game = new GameServer();
        game.setGameID("my_game_id");
        game.setPlayers(players);
        game.setCurrentPlayer(players.get(0).getPlayerName());


        //Assign PlayerColors
        List<GameColor> colors = Arrays.asList(GameColor.PLAYER_BLACK, GameColor.PLAYER_BLUE,
                GameColor.PLAYER_PURPLE, GameColor.PLAYER_RED, GameColor.PLAYER_YELLOW);
        for (int i = 0; i < game.getPlayers().size(); i++) {
            game.getPlayers().get(i).setPlayerColor(colors.get(i));
        }

        //initialize traincards
        ArrayList<Card> trainCards = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            Card locomotiveCard = new Card(GameColor.LOCOMOTIVE);
            trainCards.add(locomotiveCard);
        }
        for (int i = 0; i < 12; i++) {
            Card purpleCard = new Card(GameColor.PURPLE);
            Card blueCard = new Card(GameColor.BLUE);
            Card orangeCard = new Card(GameColor.ORANGE);
            Card whiteCard = new Card(GameColor.WHITE);
            Card greenCard = new Card(GameColor.GREEN);
            Card redCard = new Card(GameColor.RED);
            Card blackCard = new Card(GameColor.BLACK);
            Card yellowCard = new Card(GameColor.YELLOW);
            trainCards.add(purpleCard);
            trainCards.add(blueCard);
            trainCards.add(orangeCard);
            trainCards.add(whiteCard);
            trainCards.add(greenCard);
            trainCards.add(redCard);
            trainCards.add(blackCard);
            trainCards.add(yellowCard);
        }
        game.setTrainCards(trainCards);

        // set faceuptrain cards
        ArrayList<Card> faceUpTrainCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Card card = trainCards.get(0);
            faceUpTrainCards.add(card);
            trainCards.remove(0);
        }
        game.setFaceUpTrainCarCards(faceUpTrainCards);

        ArrayList<Ticket> tickets = GameFactory.getAllTickets();
        game.setTickets(tickets);

        //set player train cards
        tickets = game.getTickets();
        ArrayList<Ticket> playerTickets;
        ArrayList<Card> playerTrainCards;
        for (PlayerUser player : game.getPlayers()) {
            playerTrainCards = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Card card = trainCards.get(0);
                playerTrainCards.add(card);
                trainCards.remove(0);
            }
            player.setCards(playerTrainCards);

            if (tickets.size() < 3) {
                playerTickets =  tickets;
            }else {
                playerTickets = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    playerTickets.add(tickets.get(0));
                    tickets.remove(0);
                }
            }
            player.setTickets(playerTickets);
        }

        return game;
    }

}
