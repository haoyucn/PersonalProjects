package sqldao;

import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import communication.LobbyGame;
import communication.Result;
import communication.Serializer;
import dao.ILobbyDao;

public class SQLLobbyDAO implements ILobbyDao {
    SQLDBConnection connection = new SQLDBConnection();

    public static void main(String argv[]) {
        SQLLobbyDAO sqlLobbyDAO = new SQLLobbyDAO();
        try {
//            LobbyGame lobbyGame = new LobbyGame("hello");
//            lobbyGame.setGameID("Gs AMEID");
//
//            sqlLobbyDAO.addLobby(lobbyGame.getGameID(), lobbyGame);
            sqlLobbyDAO.updateLobby("GsAMEID", new LobbyGame("SDFSDF"));

            ArrayList<LobbyGame> lobbies = new ArrayList<>();
            lobbies.addAll(sqlLobbyDAO.getLobbies());
            System.out.println(new Serializer().serializeObject(lobbies));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addLobby(String id, LobbyGame lobbyGame) {
        Result result = null;
        String statement = "INSERT INTO lobbies (id, lobby) " +
                "VALUES (?, ?)";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            ps.setString(1,id);
            Serializer serializer = new Serializer();
            String lobbyString = serializer.serializeObject(lobbyGame);
            ps.setString(2, lobbyString);
            result = connection.executeUpdateStatement(ps);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (result == null) {
            return false;
        }
        return result.isSuccess();
    }

    @Override
    public boolean removeLobby(String id) {
        Result result = null;
        String statement = "DELETE FROM lobbies WHERE id = ?";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            ps.setString(1,id);
            result = connection.executeUpdateStatement(ps);


        } catch (SQLException e) {
            return false;
        }

        if (result == null) {
            return false;
        }
        return result.isSuccess();
    }



    @Override
    public boolean updateLobby(String gameID, LobbyGame lobbyGame) {
        Result result = null;
        String statement = "UPDATE lobbies SET lobby = ? WHERE id = ?";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            String lobbyString = new Serializer().serializeObject(lobbyGame);
            ps.setString(1,lobbyString);
            ps.setString(2,gameID);
            result = connection.executeUpdateStatement(ps);

        } catch (SQLException e) {
            return false;
        }

        if (result == null) {
            return false;
        }
        return result.isSuccess();
    }

    @Override
    public List<LobbyGame> getLobbies() {
        ArrayList<LobbyGame> lobbyGames = new ArrayList<>();
        Result result = null;
        String statement = "SELECT * FROM lobbies";
//        PreparedStatement ps = connection.getPreparedStatment(statement);
        Statement ps = connection.getStatement();
        try {
            ResultSet result1 = ps.executeQuery(statement);
            System.out.println("PRINTING");
//            System.out.println(result1.getInt("id")); //this cause a problem
//            Gson gson = new Gson();
//            System.out.println(gson.toJson(result1));
            System.out.println("FINISHED");
//            while (result1.)
            result = new Result(true, result1,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (result == null) {
            return null;
        }
        else if (result.isSuccess()){
            System.out.println(result.toString());
            ResultSet rs = (ResultSet) result.getData();
            try {
                ArrayList<String> lobbyJsons = new ArrayList<String>();
                System.out.println("RESULT SIZE " + rs.getFetchSize());
                while(rs.next()) {
                    //rs.getInt()
                    System.out.println(rs.getString("id"));
                    System.out.println(rs.getString("lobby"));
                    Serializer serializer = new Serializer();

                    lobbyGames.add(serializer.deserializeGameLobby(rs.getString("lobby")));
                    System.out.println(rs.toString());

//                    String gameJson = new String(b.getBytes(1,(int) b.length()));
//                    gameJsons.add(gameJson);
//                    System.out.println(gameJson);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return lobbyGames;
    }

    @Override
    public boolean clear() {
        Result result = null;
        String statement = "DELETE FROM lobbies";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        result = connection.executeUpdateStatement(ps);

        if (result == null) {
            return false;
        }
        return result.isSuccess();
    }
}
