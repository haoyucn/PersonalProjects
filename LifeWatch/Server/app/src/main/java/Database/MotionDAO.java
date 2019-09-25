package Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Database.Model.MotionModel;

/**
 * Created by jasontd on 5/23/19.
 */

public class MotionDAO{
    SQLDBConnection connection;
    public int insert (String username, MotionModel motion){
        connection = new SQLDBConnection();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int result = 0;
        String statement = "INSERT INTO motion (username, time, motionBlob) " +
                "VALUES (?, ?, ?)";
        PreparedStatement ps = connection.getPreparedStatment(statement);
        try{
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(motion);
            out.flush();
            byte[] bytes = bos.toByteArray();
            ps.setString(1,username);
            ps.setTimestamp(2, new Timestamp((long) motion.getStartTime()));
            ps.setBytes(3, bytes);

            result = connection.executeUpdateStatement(ps);
        } catch (Exception e) {
            e.printStackTrace();
            connection.closeConnection();
            return -1;
        }
        connection.closeConnection();
        return result;
    }

    public int update(String username, float startTime, MotionModel motion) {
        connection = new SQLDBConnection();
        int result = 0;
        String stmt =   "Update motion " +
                        "Set time = ?, " +
                        "motionBlob = ? " +
                        "Where  username = ? And time = ?;";
        PreparedStatement ps = connection.getPreparedStatment(stmt);

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(motion);
            out.flush();
            byte[] bytes = bos.toByteArray();

            ps.setTimestamp(1, new Timestamp((long) motion.getStartTime()));
            ps.setBytes(2, bytes);
            ps.setString(3,username);
            ps.setTimestamp(4, new Timestamp((long) startTime));
            result = connection.executeUpdateStatement(ps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.closeConnection();
        return result;
    }

    public int deleteMotion(String username, float startTime) {
        connection = new SQLDBConnection();
        int result = -1;
        String statement = "DELETE FROM motion WHERE username= ? and time= ?;";
        PreparedStatement ps = connection.getPreparedStatment(statement);
        try {
            ps.setString(1, username);
            ps.setTimestamp(2, new Timestamp((long) startTime));
            result = connection.executeUpdateStatement(ps);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        connection.closeConnection();
        return result;
    }


    public MotionModel getMotion(String username, float startTime){
        connection = new SQLDBConnection();
        ByteArrayInputStream bis;
        ObjectInput in = null;
        MotionModel m = null;
        try{
            String sql = "SELECT * FROM motion WHERE username = ? and time = ?;";

            PreparedStatement stmt = connection.getPreparedStatment(sql);
            stmt.setString(1, username);
            stmt.setTimestamp(2, new Timestamp((long) startTime));
            ResultSet result = connection.executeQueryStatement(stmt);
            try{
                while(result.next()){
                    bis = new ByteArrayInputStream(result.getBytes("motionBLOB"));
                    in = new ObjectInputStream(bis);
                    m = (MotionModel) in.readObject();
                }
            }
            finally {
                try{
                    if(in != null){
                        in.close();
                    }
                }
                catch (Exception e){
                    connection.closeConnection();
                }
            }
        }
        catch(Exception e){
            connection.closeConnection();
            e.printStackTrace();
        }
        connection.closeConnection();
        return m;
    }

    public void clear() {
        connection = new SQLDBConnection();
        String sql = "DELETE FROM motion;";
        PreparedStatement stmt = connection.getPreparedStatment(sql);
        connection.executeUpdateStatement(stmt);
        connection.closeConnection();
    }

}
