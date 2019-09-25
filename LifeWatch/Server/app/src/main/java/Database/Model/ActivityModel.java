package Database.Model;

/**
 * Created by jasontd on 5/23/19.
 */

public class ActivityModel {
    private String username;
    private float startTime;
    private float endTime;
    private String activityType;
    private String location;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ActivityModel(String username, float startTime, float endTime, String activityType, String location) {
        this.username = username;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activityType = activityType;
        this.location = location;
    }

    public  ActivityModel () {

    }

    @Override
    public String toString() {
        return "ActivityModel{" +
                "username='" + username + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", activityType='" + activityType + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
