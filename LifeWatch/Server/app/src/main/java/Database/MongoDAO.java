package Database;
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

import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

import Database.Model.ActivityModel;
import Database.Model.MotionModel;
import Database.Model.UserModel;

/**
 * Created by haoyucn on 6/14/19.
 */

public class MongoDAO {

    private static MongoClient mongoClient = null;
    private static final String dbname = "DocDB";

    public static  void startClient(){
        if (mongoClient == null) {
            mongoClient = MongoClients.create();
        }
    }

    public MongoDAO(){}

    public boolean insert(String username, ActivityModel activityModel) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);

        if (checkUserExist(username)) {
            Document activity =  new Document("start_time", activityModel.getStartTime())
                    .append("end_time", activityModel.getEndTime())
                    .append("location", activityModel.getLocation())
                    .append("activityType", activityModel.getActivityType())
                    .append("doctype", "activity");

            collection.insertOne(activity);

        }

        return false;
    }

    public boolean insert(String username, MotionModel motionModel) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);

        if (checkUserExist(username)) {
            Document motion =  new Document("start_time", motionModel.getStartTime())
                    .append("end_time", motionModel.getEndTime())
                    .append("avgXAcceleration", motionModel.getAvgXAcceleration())
                    .append("avgYAcceleration", motionModel.getAvgYAcceleration())
                    .append("avgZAcceleration", motionModel.getAvgZAcceleration())
                    .append("avgXGyro", motionModel.getAvgXGyro())
                    .append("avgYGyro", motionModel.getAvgYGyro())
                    .append("avgZGyro", motionModel.getAvgZGyro())
                    .append("avgTempCel", motionModel.getAvgTempCel())
                    .append("avgPressure", motionModel.getAvgPressure())
                    .append("avgHumidity", motionModel.getAvgHumidity())
                    .append("totalStep", motionModel.getTotalSteps())
                    .append("doctype", "motion");

            collection.insertOne(motion);

        }


        return false;
    }
    public boolean insert(UserModel userModel) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(userModel.getUsername());

        if (checkUserExist(userModel.getUsername())) {
            return false;
        }
        Document user = new Document("username", userModel.getUsername())
                .append("password", userModel.getPassword())
                .append("email", userModel.getEmail())
                .append("age", userModel.getAge())
                .append("profession", userModel.getProfession());
        collection.insertOne(user);
        return true;
    }

    public MongoDatabase getDBInstance(){
        return mongoClient.getDatabase(dbname);
    }

    public boolean checkUserExist(String userName) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> check = db.getCollection(userName);

        if (check.count() > 0) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getUsers(){
        ArrayList<String> users = new ArrayList<String>();
        MongoDatabase db = getDBInstance();
        MongoIterable<String> check = db.listCollectionNames();
        for (String s: check) {
            users.add(s);
        }
        return users;
    }

    public ArrayList<String> getMotions(String username){
        ArrayList<String> motions = new ArrayList<String>();
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);
        MongoCursor<Document> cursor = collection.find(gt("doctype", "motion")).iterator();
        try {
            while(cursor.hasNext()) {
                motions.add(cursor.next().toJson());
            }
        }
        finally {
            cursor.close();
        }
        return motions;
    }

    public ArrayList<String> getActivities(String username){
        ArrayList<String> activities = new ArrayList<String>();
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);
        MongoCursor<Document> cursor = collection.find(gt("doctype", "activity")).iterator();
        try {
            while(cursor.hasNext()) {
                activities.add(cursor.next().toJson());
            }
        }
        finally {
            cursor.close();
        }
        return activities;
    }


    public Double getTempByStartTime(String username, float startTime) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);
        Document doc = collection.find(gt("startTime", startTime)).first();
        return doc.getDouble("avgTempCel");
    }

    public long cleanMotion(String username) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);
        DeleteResult deleteResult = collection.deleteMany(gte("doctype", "motion"));
        return deleteResult.getDeletedCount();
    }
    public long cleanActivity(String username) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);
        DeleteResult deleteResult = collection.deleteMany(gte("doctype", "activity"));
        return deleteResult.getDeletedCount();
    }

    public void deleteUser(String username){
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);
        collection.drop();
    }

    public void updateMotion(String username, MotionModel motionModel) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);

        Document motion =  new Document("start_time", motionModel.getStartTime())
                .append("end_time", motionModel.getEndTime())
                .append("avgXAcceleration", motionModel.getAvgXAcceleration())
                .append("avgYAcceleration", motionModel.getAvgYAcceleration())
                .append("avgZAcceleration", motionModel.getAvgZAcceleration())
                .append("avgXGyro", motionModel.getAvgXGyro())
                .append("avgYGyro", motionModel.getAvgYGyro())
                .append("avgZGyro", motionModel.getAvgZGyro())
                .append("avgTempCel", motionModel.getAvgTempCel())
                .append("avgPressure", motionModel.getAvgPressure())
                .append("avgHumidity", motionModel.getAvgHumidity())
                .append("totalStep", motionModel.getTotalSteps())
                .append("doctype", "motion");
        collection.updateOne(and(eq("doctype", "motion"),eq("start_time", motionModel.getStartTime())), motion);
    }

    public void updateActivity(String username, ActivityModel activityModel) {
        MongoDatabase db = getDBInstance();
        MongoCollection<Document> collection = db.getCollection(username);
        Document activity =  new Document("start_time", activityModel.getStartTime())
                .append("end_time", activityModel.getEndTime())
                .append("location", activityModel.getLocation())
                .append("activityType", activityModel.getActivityType())
                .append("doctype", "activity");
        collection.updateOne(and(eq("doctype", "activity"),eq("start_time", activityModel.getStartTime())), activity);


    }
}
