package sqldao;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import communication.Result;

/**
 * Created by haoyucn on 12/5/18.
 */

public class SQLDBConnection {

    String url;
    static Connection conn;
    static int numberProcess = 0;

    public SQLDBConnection() {
        String fileDirectory = System.getProperty("user.dir") + "/server/src/main/java/dao/SQL/";

        String fileName="server.db";
        this.url = fileDirectory + fileName;
        System.out.println(url);
    }

    public Boolean createDBFile() {
        File file = new File(url);
        try {
            if(!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("file has been created");
                    createTables();
                    return true;
                } else {
                    System.out.println("failed to create a file");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private Boolean createTables(){
        String createUserTable = "CREATE TABLE users (id varchar(25) PRIMARY KEY, password varchar(25), authtoken varchar(25));";
        String createLobbyTable = "CREATE TABLE lobbies(id varchar(25) PRIMARY KEY, lobby BLOB);";
        String createGamesTable = "CREATE TABLE games(id varchar(25) PRIMARY KEY, game BLOB, cmdlist BLOB);";
        openConnection();
        PreparedStatement u = getPreparedStatment(createUserTable);
        PreparedStatement l = getPreparedStatment(createLobbyTable);
        PreparedStatement g = getPreparedStatment(createGamesTable);
        try{
            u.execute();
            l.execute();
            g.execute();
            conn.commit();
            closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
        }
        return false;
    }

    private Boolean openConnection() {
        try {
            createDBFile();
            String connectionUrl = "jdbc:sqlite:" + url;
            if (conn == null) {
                this.conn = DriverManager.getConnection(connectionUrl);
                conn.setAutoCommit(false);
            }
            else if (conn.isClosed()) {
                this.conn = DriverManager.getConnection(connectionUrl);
                conn.setAutoCommit(false);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet executeCommand (String stmtString) {
        if (openConnection()) {
            try {
                conn.setAutoCommit(false);
                Statement stmt = conn.createStatement();
                numberProcess--;
                if (stmt.execute(stmtString)) {
                    conn.commit();
                    closeConnection();
                    return stmt.getResultSet();
                }
                else {
                    conn.rollback();
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
            closeConnection();
        }
        return null;
    }

    // return null for false result
    public PreparedStatement getPreparedStatment(String sql){
        PreparedStatement preparedStatement = null;
        if (openConnection()) {
            try {
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setQueryTimeout(1);
                numberProcess++;
            }
            catch(SQLException e) {
                e.printStackTrace();
                closeConnection();
            }
        }
        return preparedStatement;
    }

    public Statement getStatement() {
        if (openConnection()) {
            try {
                numberProcess++;
                return conn.createStatement();
            }
            catch(SQLException e) {
                e.printStackTrace();
                closeConnection();
            }

        }
        return null;
    }

    public Result executeQueryStatement(PreparedStatement p) {
        try{
            numberProcess--;
            ResultSet rs  = p.executeQuery();

            if (rs.getFetchSize() == 0) {
                conn.rollback();
                closeConnection();
                System.out.println("Execute result is false.");
                System.out.println(p.getWarnings());
                return new Result(false, p.getWarnings(), "SQL Failure");
            }
            conn.commit();
            closeConnection();
            return new Result(true, rs, null);

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return new Result(false, e,e.getMessage());
        }
    }

    public Result executeUpdateStatement(PreparedStatement p) {
        try{
            numberProcess--;
            int count = p.executeUpdate();
            if (count == 0) {
                conn.rollback();
                closeConnection();
                System.out.println("Execute result is falise.");
                return new Result(false, p.getWarnings(), "SQL Failure");
            }
            conn.commit();
            closeConnection();
            return new Result(true, false, null);

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return new Result(false, e,e.getMessage());
        }
    }

    private boolean closeConnection() {
        if (conn != null) {
            try {
                if (numberProcess == 0) {
                    conn.close();
                    conn = null;
                }
                return true;
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Blob getBlob() {
        openConnection();
        try {
            return conn.createBlob();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
