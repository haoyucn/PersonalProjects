package Database;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


//import communication.Result;

public class SQLDBConnection {

    String url;
    static Connection conn;
    static int numberProcess = 0;

    public SQLDBConnection() {
        String fileDirectory = System.getProperty("user.dir") + "/";

        String fileName="server.db";
        this.url = fileDirectory + fileName;
    }


    public Boolean createDBFile() {
        File file = new File(url);
        try {
            if(!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("DB file: created successfully");
                    createTables();
                    return true;
                } else {
                    System.out.println("DB file: failed to create");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private Boolean createTables(){
        String createUserTable =
                "CREATE TABLE user (" +
                "username varchar(25) PRIMARY KEY, " +
                "password varchar(25)," +
                "email varchar(50)," +
                "age int(2)," +
                "profession varchar(25)" +
                ");";
        String createMotionTable =
                "CREATE TABLE motion (" +
                "username varchar(25), " +
                "time datetime," +
                "motionBlob BLOB," +
                "PRIMARY KEY(username, time)" +
                ");";
        String createActivityTable =
                "CREATE TABLE activity (" +
                "username varchar(25), " +
                "startTime datetime, " +
                "endTime datetime," +
                "activityType varchar(25)," +
                "location varchar(25)," +
                "PRIMARY KEY(username, startTime, endTime)" +
                ");";
        openConnection();
        PreparedStatement u = getPreparedStatment(createUserTable);
        PreparedStatement m = getPreparedStatment(createMotionTable);
        PreparedStatement a = getPreparedStatment(createActivityTable);
        try{
            u.execute();
            m.execute();
            a.execute();
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

     public ResultSet executeQueryStatement(PreparedStatement p) {
         try{
             numberProcess--;
             ResultSet rs  = p.executeQuery();
             return rs;

         } catch (SQLException e) {
             e.printStackTrace();
             closeConnection();
             return null;
         }
     }

     public int executeUpdateStatement(PreparedStatement p) {
         try{
             numberProcess--;
             int count = p.executeUpdate();
             if (count == 0) {
                 conn.rollback();
                 return count;
             }
             conn.commit();
             return count;

         } catch (SQLException e) {
             e.printStackTrace();
             return -1;
         }
     }

    public boolean closeConnection() {
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
