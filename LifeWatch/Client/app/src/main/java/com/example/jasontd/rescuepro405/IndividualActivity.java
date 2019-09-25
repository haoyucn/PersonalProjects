package com.example.jasontd.rescuepro405;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;

public class IndividualActivity extends AppCompatActivity implements SensorEventListener {

    private TextView mStatus;
    private Button mDone;
    private SensorManager sensorManager;
    private long lastUpdate;
    private boolean crashMode;
    private float testMag;
    private float highestMag = 0;
    private float lowestMag = 10;
    private float fallMag = 0;
    private float accelMagnitude;
    private float testTime;
    private StringBuilder testLog = new StringBuilder();
    private String green = new String("#7cff00");
    private float startTime = 0;
    private int countXAcceleration = 0;
    private int countYAcceleration = 0;
    private int countZAcceleration = 0;
    private int countXGyro = 0;
    private int countYGyro = 0;
    private int countZGyro = 0;
    private int countTempCel = 0;
    private int countPressure = 0;
    private int countHumidity = 0;
    private MotionRAW motionRAW = new MotionRAW();

    //private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setBackground();

        //getDefaultSensor(SENSOR_TYPE_ACCELEROMETER);
        mDone = (Button) findViewById(R.id.alert);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        mDone.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view){
                saveData();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (startTime == 0){
            startTime = System.currentTimeMillis()/1000;
            motionRAW.setStartTime(startTime);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            getPressure(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            getStep(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            getHumidity(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            getTemp(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            getGyro(event);
        }
    }

    private void saveData(){
        motionRAW.setEndTime(System.currentTimeMillis()/1000);
        startTime = 0;
        countXAcceleration = 0;
        countYAcceleration = 0;
        countZAcceleration = 0;
        countXGyro = 0;
        countYGyro = 0;
        countZGyro = 0;
        countTempCel = 0;
        countPressure = 0;
        countHumidity = 0;
        motionRAW = new MotionRAW();
        Toast.makeText(this, "Motion Event Saved!",
                Toast.LENGTH_LONG).show();
        //store in SQL database
    }

    private float Average(float existing, float add, int talley){
        float avg = (existing * (talley-1) + add)/talley;
        return avg;
    }

    private void getAccelerometer(SensorEvent event){
        float[] values = event.values;
        //Zero movement = 9.81
        float x = values[0]; //laying flat on table, pushed from left to right is (+, aka >9.81)
        float y = values[1]; //laying on table, raised to sky is (+, >9.81)
        float z = values[2]; //laying flat on desk, z is away from/closer from user

        countXAcceleration += 1;
        countYAcceleration += 1;
        countZAcceleration += 1;

        motionRAW.setAvgXAcceleration(Average(motionRAW.getAvgXAcceleration(), x, countXAcceleration));
        motionRAW.setAvgYAcceleration(Average(motionRAW.getAvgYAcceleration(), y, countYAcceleration));
        motionRAW.setAvgZAcceleration(Average(motionRAW.getAvgXAcceleration(), z, countZAcceleration));
    }

    private void getGyro(SensorEvent event){
        float[] values = event.values;
        float x = values[0]; //laying flat on table, pushed from left to right is (+, aka >9.81)
        float y = values[1]; //laying on table, raised to sky is (+, >9.81)
        float z = values[2]; //laying flat on desk, z is away from/closer from user

        countXGyro += 1;
        countYGyro += 1;
        countZGyro += 1;

        motionRAW.setAvgXGyro(Average(motionRAW.getAvgXGyro(), x, countXGyro));
        motionRAW.setAvgYGyro(Average(motionRAW.getAvgYGyro(), y, countYGyro));
        motionRAW.setAvgZGyro(Average(motionRAW.getAvgXGyro(), z, countZGyro));
    }

    private void getPressure(SensorEvent event){
        float pressure = event.values[0]; //laying flat on table, pushed from left to right is (+, aka >9.81)
        countPressure += 1;
        motionRAW.setAvgPressure(Average(motionRAW.getAvgPressure(), pressure, countPressure));
    }

    private void getHumidity(SensorEvent event){
        float humidity = event.values[0]; //laying flat on table, pushed from left to right is (+, aka >9.81)
        countHumidity += 1;
        motionRAW.setAvgHumdity(Average(motionRAW.getAvgHumidity(), humidity, countHumidity));
    }

    private void getTemp(SensorEvent event){
        float temp = event.values[0]; //laying flat on table, pushed from left to right is (+, aka >9.81)
        countTempCel += 1;
        motionRAW.setAvgTempCel(Average(motionRAW.getAvgTempCel(), temp, countTempCel));
    }

    private void getStep(SensorEvent event){
        float step = event.values[0]; //laying flat on table, pushed from left to right is (+, aka >9.81)
        motionRAW.setTotalSteps(motionRAW.getTotalSteps() + 1);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
        saveData();
    }

    //Up button
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                startTopActivity(this.getApplicationContext(), false);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Logging out or just returning to MapFragment
    private void startTopActivity(Context context, boolean newInstance)
    {
        Intent intent = new Intent(context, LoginActivity.class);
        if (newInstance)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }

    private void setBackground(String color){
        View v = findViewById(android.R.id.content);
        int h = v.getHeight();
        ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setShader(new LinearGradient(0, 0, 0, h, Color.parseColor("#330000FF"),
                Color.parseColor(color), Shader.TileMode.REPEAT));
        v.setBackgroundDrawable(mDrawable);
    }
}
