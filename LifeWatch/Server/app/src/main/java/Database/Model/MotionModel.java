package Database.Model;

import java.io.Serializable;

/**
 * Created by jasontd on 5/23/19.
 */

public class MotionModel implements Serializable {
    private float avgXAcceleration;
    private float avgYAcceleration;
    private float avgZAcceleration;
    private float avgXGyro;
    private float avgYGyro;
    private float avgZGyro;
    private float avgTempCel;
    private float avgPressure;
    private float avgHumidity;
    private float totalSteps;
    private float startTime;
    private float endTime;

    private int ID;

    public float getDuration(){
        return endTime-startTime;
    }

    public float getMagnitudeAcceleration(){
        float squareX = (float) Math.pow(avgXAcceleration,2);
        float squareY = (float) Math.pow(avgYAcceleration,2);
        float squareZ = (float) Math.pow(avgZAcceleration,2);
        return (float) Math.sqrt( squareX + squareY + squareZ );
    }

    public float getMagnitudeGyro(){
        float squareX = (float) Math.pow(avgXGyro,2);
        float squareY = (float) Math.pow(avgYGyro,2);
        float squareZ = (float) Math.pow(avgZGyro,2);
        return (float) Math.sqrt( squareX + squareY + squareZ );
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public float getAvgXAcceleration() {
        return avgXAcceleration;
    }

    public void setAvgXAcceleration(float avgXAcceleration) {
        this.avgXAcceleration = avgXAcceleration;
    }

    public float getAvgYAcceleration() {
        return avgYAcceleration;
    }

    public void setAvgYAcceleration(float avgYAcceleration) {
        this.avgYAcceleration = avgYAcceleration;
    }

    public float getAvgZAcceleration() {
        return avgZAcceleration;
    }

    public void setAvgZAcceleration(float avgZAcceleration) {
        this.avgZAcceleration = avgZAcceleration;
    }

    public float getAvgXGyro() {
        return avgXGyro;
    }

    public void setAvgXGyro(float avgXGyro) {
        this.avgXGyro = avgXGyro;
    }

    public float getAvgYGyro() {
        return avgYGyro;
    }

    public void setAvgYGyro(float avgYGyro) {
        this.avgYGyro = avgYGyro;
    }

    public float getAvgZGyro() {
        return avgZGyro;
    }

    public void setAvgZGyro(float avgZGyro) {
        this.avgZGyro = avgZGyro;
    }

    public float getAvgTempCel() {
        return avgTempCel;
    }

    public void setAvgTempCel(float avgTempCel) {
        this.avgTempCel = avgTempCel;
    }

    public float getAvgPressure() {
        return avgPressure;
    }

    public void setAvgPressure(float avgPressure) {
        this.avgPressure = avgPressure;
    }

    public float getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumdity(float avgHumdity) {
        this.avgHumidity = avgHumdity;
    }

    public float getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(float totalSteps) {
        this.totalSteps = totalSteps;
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

}

