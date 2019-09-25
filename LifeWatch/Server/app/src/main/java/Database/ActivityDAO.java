package Database;

import android.app.Activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import Database.Model.ActivityModel;
import Database.Model.MotionModel;

/**
 * Created by jasontd on 5/23/19.
 */

public class ActivityDAO {
    SQLDBConnection connection;
    public int insert (ActivityModel activity){
        connection = new SQLDBConnection();
        int result = -1;
        String statement = "INSERT INTO activity (username, startTime, endTime, activityType, location) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.getPreparedStatment(statement);
        try{
            ps.setString(1,activity.getUsername());
            ps.setTimestamp(2, new Timestamp((long) activity.getStartTime()));
            ps.setTimestamp(3, new Timestamp((long) activity.getStartTime()));
            ps.setString(4, activity.getActivityType());
            ps.setString(5, activity.getLocation());
            result = connection.executeUpdateStatement(ps);
        } catch (Exception e) {
            e.printStackTrace();
        }

        connection.closeConnection();
        return result;
    }

    public int deleteActivity(String username, float startTime) {
        connection = new SQLDBConnection();
        int result = -1;
        String statement = "DELETE FROM motion WHERE username = ? And startTime = ? ;";
        PreparedStatement ps = connection.getPreparedStatment(statement);
        try {
            ps.setString(1, username);
            ps.setTimestamp(2, new Timestamp((long) startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = connection.executeUpdateStatement(ps);
        connection.closeConnection();
        return result;
    }


    public ActivityModel getActivity(String username, float startTime){
        connection = new SQLDBConnection();
        ActivityModel m = new ActivityModel();
        try{
            String sql = "SELECT * FROM activity WHERE username= ? and startTime= ?;";
            PreparedStatement ps = connection.getPreparedStatment(sql);
            ps.setString(1, username);
            ps.setTimestamp(2, new Timestamp((long) startTime));
            ResultSet result = connection.executeQueryStatement(ps);

            while(result.next()){
                m.setUsername(result.getString("username"));
                m.setStartTime((float)result.getTime("startTime").getTime());
                m.setEndTime((float)result.getTime("endTime").getTime());
                m.setActivityType(result.getString("activityType"));
                m.setLocation(result.getString("location"));
            }
        }
        catch(Exception e){
            connection.closeConnection();
            e.printStackTrace();
        }
        connection.closeConnection();
        return m;
    }

    public int update (float prevStartTime, float prevEndTime, ActivityModel activityModel) {
        connection = new SQLDBConnection();
        int result = -1;
        String stmt = "Update activity " +
                "Set startTime = ?, " +
                "endTime = ?, " +
                "activityType = ?, " +
                "location = ? " +
                "Where username = ? And startTime = ? And endTime = ?;";
        PreparedStatement ps = connection.getPreparedStatment(stmt);
        try{
            ps.setTimestamp(1, new Timestamp((long) activityModel.getStartTime()));
            ps.setTimestamp(2, new Timestamp((long) activityModel.getEndTime()));
            ps.setString(3, activityModel.getActivityType());
            ps.setString(4, activityModel.getLocation());
            ps.setString(5, activityModel.getUsername());
            ps.setTimestamp(6, new Timestamp((long) prevStartTime));
            ps.setTimestamp(7, new Timestamp((long) prevEndTime));
            result = connection.executeUpdateStatement(ps);
        }catch (Exception e) {
            e.printStackTrace();
        }
        connection.closeConnection();
        return result;
    }

    public void clear() {
        connection = new SQLDBConnection();
        String sql = "DELETE FROM activity";
        PreparedStatement stmt = connection.getPreparedStatment(sql);
        connection.executeUpdateStatement(stmt);
        connection.closeConnection();
    }
}
