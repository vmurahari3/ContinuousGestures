package com.example.ubicomp.continuousgestures;

import android.app.Activity;
import android.graphics.DashPathEffect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.ubicomp.continuousgestures.Constants.Constants;
import com.example.ubicomp.continuousgestures.Learning.DataAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    Sensor gyroscope;
    private String status = "OFF";
    //TODO remove this flag only for testing purpose
    boolean flag_sound_generated = false;
    public static int  buffer_size = 10;
    public static final double threshold = 0.3;
    private ArrayList<ArrayList<Double>>  sensor_values = new ArrayList<>();
    private boolean first_fire = false;
    private long prev_fire_timestamp, cur_timestamp;
    private DataAnalyzer mDataAnalyzer;

    private HashMap<String, String> labelNameMap = new HashMap<String, String>();

    private TextView resultTV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize sensor stuff
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        gyroscope =  mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mDataAnalyzer = new DataAnalyzer(this);
        for (int i  = 0; i<6 ; i++){
            sensor_values.add(new ArrayList<Double>());
        }

        labelNameMap.put("10", "Clockwise");
        labelNameMap.put("11", "Nod");
        labelNameMap.put("12", "Whatsup");
        labelNameMap.put("1_4_2_3_5", "Not detected");

        resultTV = (TextView) findViewById(R.id.resultTV);

        // initialize classification stuff
        mDataAnalyzer.init();
        // load arff file
        mDataAnalyzer.loadArffFile("file:///android_asset/null_class_model_final.arff");
        mDataAnalyzer.buildClassifier();
    }

    protected void onResume(){

        super.onResume();
        mSensorManager.registerListener(this,accelerometer,20000);
        mSensorManager.registerListener(this,gyroscope,20000);
    }

    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



    public void onSensorChanged(SensorEvent event) {

        // store in a buffer
        int type = event.sensor.getType();
        cur_timestamp = event.timestamp;

        if (type == Sensor.TYPE_LINEAR_ACCELERATION) {

            float[] acceleration = event.values;
            for(int i = 0; i<acceleration.length; i++ ){
                sensor_values.get(i).add((double)acceleration[i]);
            }
            if(sensor_values.get(0).size() > Constants.WIN_SIZE){
                for(int i =0; i< acceleration.length; i++){
                    sensor_values.get(i).remove(0);
                }
            }


        } else if (type == Sensor.TYPE_GYROSCOPE) {

            float[] gyro = event.values;
            for(int i = 0; i<gyro.length; i++ ){
                sensor_values.get(i+3).add((double)gyro[i]);
            }
            if(sensor_values.get(3).size() > Constants.WIN_SIZE){
                for(int i =0; i< gyro.length; i++){
                    sensor_values.get(i+3).remove(0);
                }
            }
        }

        if( sensor_values.get(0).size() == Constants.WIN_SIZE && sensor_values.get(3).size() == Constants.WIN_SIZE) {
            // fire classifier
            if (!first_fire) {
                first_fire = true;
                prev_fire_timestamp = event.timestamp;
                fire_classifier();
            }
            else{
                // 10 percent overlap for a 3 sec window
                if (( cur_timestamp - prev_fire_timestamp) > Constants.SLIDE_SIZE){
                    fire_classifier();
                    prev_fire_timestamp = cur_timestamp;
                }
            }

        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
    private void fire_classifier(){
        ArrayList<Double[]> ret = new ArrayList<>();
        for(ArrayList<Double> ch : sensor_values ){
            ret.add(ch.toArray(new Double[ch.size()]));
        }
        mDataAnalyzer.addTestSample(ret);
        String label = mDataAnalyzer.classifyGesture();
        String name = labelNameMap.get(label);
        Log.d(TAG, "result: " + name);
        resultTV.setText(name);
    }


}
