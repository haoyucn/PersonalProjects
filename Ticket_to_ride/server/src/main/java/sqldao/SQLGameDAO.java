package sqldao;

import com.obfuscation.server.GenericCommand;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import communication.GameServer;
import communication.ICommand;
import communication.Message;
import communication.Result;
import communication.Serializer;
import dao.IGameDao;


/**
 * Created by haoyucn on 12/5/18.
 */

public class SQLGameDAO implements IGameDao{
    private static final String SERVER_FACADE = "com.obfuscation.server.ServerFacade";
    private static final String STRING = "java.lang.String";
    private static final String INTEGER = "java.lang.Integer";
    //    TODO: helps to find the typeName
    private static final String ARRAYLISTCARD = "??";
    private static final String LIST = List.class.getName();

    SQLDBConnection connection;

    public SQLGameDAO() {
        this.connection = new SQLDBConnection();
    }

//    public void addGame(String gameID, Blob game) {
//
//    }

    public static void main(String argv[]) {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        try {
            GameServer gameServer = new GameServer();
            gameServer.setGameID("gameID");
            GenericCommand genericCommand = new GenericCommand(SERVER_FACADE,
                    "SendMessage", new String[]{STRING,STRING, Message.class.getName()},
                    new Object[]{"masterkey", "gameID", "HELLO WORLD"});
            sqlGameDAO.removeGame("GsAMEID");
            ArrayList<GenericCommand> commands = new ArrayList<>();
            commands.add(genericCommand);
            sqlGameDAO.getGames();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
//    tested
    public boolean addGame(String gameID, GameServer game) {
        Result result = null;
        String statement = "INSERT INTO games (id, game, cmdlist) " +
                            "VALUES (?, ?, \"\")";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            ps.setString(1,gameID);
            Serializer serializer = new Serializer();
            String gameString = serializer.serializeGameServer(game);
            ps.setString(2, gameString);
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

//    tested
    @Override
    public boolean removeGame(String gameID) {
        Result result = null;
        String statement = "DELETE FROM games WHERE id = ?";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            ps.setString(1,gameID);
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
    //    tested
    public boolean updateGame(String gameID, GameServer game) {
        Result result = null;
        String statement = "UPDATE games SET game = ? WHERE id = ?";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            String gameString = new Serializer().serializeGameServer(game);
            System.out.println("SERIALIZED GAME");
            System.out.println(gameString);
            ps.setString(1,gameString);
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
    // tested
    public boolean updateCmdList(String gameID, ArrayList<GenericCommand> cmdlist) {
        Result result = null;
        String statement = "UPDATE games SET cmdlist = ? WHERE id = ?";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Serializer serializer = new Serializer();
            for (GenericCommand c : cmdlist) {
                sb.append(serializer.serializeCommand(c));
                sb.append(",");
            }
            if(sb.charAt(sb.length() -1) == ',') {
                sb.setCharAt(sb.length() - 1,']');
            }
            else {
                sb.append("]");
            }
            ps.setString(1,sb.toString());
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
    public List<GameServer> getGames() {
        ArrayList<GameServer> games = new ArrayList<>();
        Result result = null;
        String statement = "SELECT * FROM games;";
//        PreparedStatement ps = connection.getPreparedStatment(statement);
        Statement ps = connection.getStatement();
        try {
//            result = connection.executeQueryStatement(ps);
            result = new Result(true, ps.executeQuery(statement),null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (result == null) {
            return null;
        }
        else if (result.isSuccess()){
            ResultSet rs = (ResultSet) result.getData();
            try {
                ArrayList<String> gameJsons = new ArrayList<String>();
                System.out.println(rs.getFetchSize());
                while(rs.next()) {
                    Serializer serializer = new Serializer();
                    games.add(serializer.deserializeGameServer(rs.getString("game")));
                    System.out.println("GAME FROM DATABASE");
                    System.out.println(rs.getString("game"));
                    String cmdListString = rs.getString("cmdlist");
                    System.out.println("DESERIALIZING COMMANDS");
                    System.out.println(cmdListString);
                    ArrayList<Object> list = serializer.deserializeList(cmdListString);
                    ArrayList<ICommand> commandList = new ArrayList<>();
                    for (Object o : list) {
                        System.out.println("adding command");
                        System.out.println(o.toString());
                        commandList.add(serializer.deserializeCommand(o.toString()));
                    }
//                    String gameJson = new String(b.getBytes(1,(int) b.length()));
//                    gameJsons.add(gameJson);
//                    System.out.println(gameJson);
                }
                return games;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private String readBlob(){

        return null;
    }

    @Override
    public List<GenericCommand> getCommands() {
        ArrayList<GenericCommand> commandList = new ArrayList<>();
        Result result = null;
        String statement = "SELECT * FROM games;";
//        PreparedStatement ps = connection.getPreparedStatment(statement);
        Statement ps = connection.getStatement();
        try {
//            result = connection.executeQueryStatement(ps);
            result = new Result(true, ps.executeQuery(statement),null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (result == null) {
            return null;
        }
        else if (result.isSuccess()){
            ResultSet rs = (ResultSet) result.getData();
            try {
                ArrayList<String> gameJsons = new ArrayList<String>();
                System.out.println(rs.getFetchSize());
                while(rs.next()) {
                    Serializer serializer = new Serializer();
                    String cmdListString = rs.getString("cmdlist");
                    ArrayList<Object> list = serializer.deserializeList(cmdListString);

                    for (Object o : list) {
                        System.out.println("adding command");
                        System.out.println(o.toString());
                        commandList.add(serializer.deserializeGenericCommand(o.toString()));
                    }

//                    String gameJson = new String(b.getBytes(1,(int) b.length()));
//                    gameJsons.add(gameJson);
//                    System.out.println(gameJson);
                }
                return commandList;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;    }

    @Override
    public boolean clear() {
        Result result = null;
        String statement = "DELETE FROM games";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        result = connection.executeUpdateStatement(ps);

        if (result == null) {
            return false;
        }
        return result.isSuccess();
    }
}
