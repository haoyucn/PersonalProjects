package Server;

import android.app.Activity;

import Database.*;
import Database.Model.ActivityModel;
import Database.Model.MotionModel;
import Database.Model.UserModel;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;

import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoyucn on 5/23/19.
 */

public class Server {


    public static void main(String[] argvs) {
        SQLDBConnection conn =  new SQLDBConnection();
        conn.createDBFile();
        UserDAO userDAO = new UserDAO();
        MotionDAO motionDAO = new MotionDAO();
        ActivityDAO activityDAO = new ActivityDAO();

        userDAO.clear();
        motionDAO.clear();
        activityDAO.clear();

        // populating the DAO
        UserModel user = new UserModel("J", "password", "@@@", 16, "weeb");
        userDAO.insert(user);
        user = new UserModel("T", "password", "@@@", 18, "weeb");
        userDAO.insert(user);
        user = new UserModel("M", "password", "@@@", 20, "weeb");
        userDAO.insert(user);

        // testing update user table
        user = new UserModel("R", "password", "@@@", 22, "weeb");
        int updateStat = userDAO.update(user);
        System.out.println(user.toString() + " update return stat: " + String.valueOf(updateStat));
        userDAO.insert(user);
        user.setAge(1);
        updateStat = userDAO.update(user);
        System.out.println(user.toString() + " update return stat: " + String.valueOf(updateStat));

        // testing motion table
        MotionModel motion = new MotionModel();
        motion.setStartTime(101010);
        motion.setAvgHumdity(50);
        motionDAO.insert("J", motion);
        MotionModel mm = motionDAO.getMotion("J", 101010);
        if (mm == null) {
            System.out.println("failed to get motion");
        }
        else {
            System.out.println(mm.getAvgHumidity());
        }

        motion.setStartTime(2020);
        updateStat = motionDAO.update("J", 101010, motion);
        System.out.println("motion update stat: " +String.valueOf(updateStat));
        motionDAO.deleteMotion("J", 101010);
        mm = motionDAO.getMotion("J",101010);
        if (mm == null) {
            System.out.println("no motion found after deleting");
        }
        else {
            System.out.println(mm.getAvgHumidity());
        }



        System.out.println("\n\n\ntesting activity");
        ActivityModel activity = new ActivityModel("J", 101010, 101010, "Born", "world");
        activityDAO.insert(activity);
        activity = activityDAO.getActivity("J", 101010);
        System.out.println("activity got: " + activity.toString());
        activity.setActivityType("born in");
        activityDAO.update(101010, 101010, activity);
        activity = activityDAO.getActivity("J", 101010);
        System.out.println("activity got: " + activity.toString());

        Server serv = new Server();
        serv.testMongo();
    }



    public void testMongo(){



        MongoDAO mongoDAO = new MongoDAO();
        MongoDAO.startClient();

        //populating
        UserModel user = new UserModel("J", "password", "@@@", 16, "weeb");
        mongoDAO.insert(user);
        user = new UserModel("T", "password", "@@@", 18, "weeb");
        mongoDAO.insert(user);
        user = new UserModel("M", "password", "@@@", 20, "weeb");
        mongoDAO.insert(user);

        // testing motion table
        MotionModel motion = new MotionModel();
        motion.setStartTime(101010);
        motion.setAvgHumdity(50);
        mongoDAO.insert("J", motion);
        List<String> motions = mongoDAO.getMotions("J");
        if (motions == null) {
            System.out.println("failed to get motion from Mongo");
        }
        else {
            for(String m : motions) {
                System.out.println(m);
            }
        }

        motion.setStartTime(2020);
        mongoDAO.updateMotion("J", motion);
        System.out.println("motion updated: " +String.valueOf(motion.getStartTime()));
        mongoDAO.deleteUser("J");
        List<String> usernames= mongoDAO.getUsers();
        if (!usernames.contains("J")) {
            System.out.println("no motion found after deleting");
        }
        else {
            System.out.println("failed to delete user");
        }

        System.out.println("\n\n\ntesting activity");
        ActivityModel activity = new ActivityModel("J", 101010, 101010, "Born", "world");
        mongoDAO.insert(activity.getUsername(),activity);
        List<String> activities = mongoDAO.getActivities("J");
        for(String s : activities){
            System.out.println("retrieved activity and all info: " + s.toString());
        }
        activity.setActivityType("born in");
        mongoDAO.updateActivity("J", activity);
        activities = mongoDAO.getActivities("J");
        for(String s : activities){
            System.out.println("retrieved activity and updated info: " + s.toString());
        }

    }
}
