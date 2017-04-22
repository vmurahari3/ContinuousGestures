package edu.gatech.ubicomp.continuousgestures;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import edu.gatech.ubicomp.continuousgestures.common.Constants;
import edu.gatech.ubicomp.continuousgestures.common.DependencyUtil;
import edu.gatech.ubicomp.continuousgestures.common.rx.RxEventBus;
import edu.gatech.ubicomp.continuousgestures.data.learning.DataAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import edu.gatech.ubicomp.continuousgestures.data.learning.Segmenter;
import io.realm.Realm;

public class TestingActivity extends Activity implements SensorEventListener {
    private static final String TAG = TestingActivity.class.getSimpleName();

    @Inject
    public RxEventBus mRxEventBus;

    private Realm realm;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor gyroscope;
    private String status = "OFF";
    //TODO remove this flag only for testing purpose
    boolean flag_sound_generated = false;
    public static int  buffer_size = 10;
    public static final double threshold = 0.2;
    private ArrayList<ArrayList<Double>> sensorValues = new ArrayList<>();

    private ArrayList<ArrayList<Double>> possibleGestureData = new ArrayList<>();
    private ArrayList<Long> possibleGestureDataTimestamp = new ArrayList<>();

    private ArrayList<Long> gyroTimestamp = new ArrayList<>();

    private boolean first_fire = false;
    private long prev_fire_timestamp, curTimestamp;
    private DataAnalyzer mDataAnalyzer;

    private HashMap<String, String> labelNameMap = new HashMap<String, String>();
    private boolean isGestureExecuting = false;
    private int overlapControl = 0;

    private TextView resultTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(edu.gatech.ubicomp.continuousgestures.R.layout.activity_testing);
        DependencyUtil.inject(this, this);

        // initialize sensor stuff
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroscope =  mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mDataAnalyzer = new DataAnalyzer(this);
        for (int i  = 0; i<6 ; i++){
            sensorValues.add(new ArrayList<Double>());
            possibleGestureData.add(new ArrayList<Double>());
        }
        //@todo hardcoded here might need to change as gesture classes increase
        labelNameMap.put("10", "Clockwise");
        labelNameMap.put("11", "Nod");
        labelNameMap.put("12", "Whatsup");
        labelNameMap.put("13", "Not detected");

        resultTV = (TextView) findViewById(edu.gatech.ubicomp.continuousgestures.R.id.resultTV);

        // Create the Realm instance
        realm = Realm.getDefaultInstance();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }


    public void onSensorChanged(SensorEvent event) {

        // store in a buffer
        int type = event.sensor.getType();
        curTimestamp = event.timestamp;

        if (type == Sensor.TYPE_LINEAR_ACCELERATION) {

            float[] acceleration = event.values;
            for(int i = 0; i<acceleration.length; i++ ){
                sensorValues.get(i).add((double)acceleration[i]);
                if(isGestureExecuting) {
                    possibleGestureData.get(i).add((double)acceleration[i]);

                }
            }
            if(sensorValues.get(0).size() > Constants.WIN_SIZE){
                for(int i =0; i< acceleration.length; i++){
                    sensorValues.get(i).remove(0);
                }
            }


        } else if (type == Sensor.TYPE_GYROSCOPE) {

            float[] gyro = event.values;
            gyroTimestamp.add(curTimestamp);
            // overlapControl helps us calculate energy only at certain times
            overlapControl = (overlapControl + 1) % Constants.OVERLAP;

            for(int i = 0; i<gyro.length; i++ ){
                sensorValues.get(i+3).add((double)gyro[i]);
                if(isGestureExecuting) {
                    possibleGestureData.get(i+3).add((double)gyro[i]);
                    possibleGestureDataTimestamp.add(curTimestamp);
                }
            }
            if(sensorValues.get(3).size() > Constants.WIN_SIZE){
                for(int i =0; i< gyro.length; i++){
                    sensorValues.get(i+3).remove(0);
                }
                // also trim the timestamp list
                gyroTimestamp.remove(0);
            }
        }

//        if( sensorValues.get(0).size() == Constants.WIN_SIZE && sensorValues.get(3).size() == Constants.WIN_SIZE) {
//            // fire classifier
//            if (!first_fire) {
//                first_fire = true;ArrayList<ArrayList<Double>>
//                prev_fire_timestamp = event.timestamp;
//                fireClassifier();
//            }
//            else{
//                // 10 percent overlap for a 3 sec window
//                if (( curTimestamp - prev_fire_timestamp) > Constants.SLIDE_SIZE){
//                    fireClassifier();
//                    prev_fire_timestamp = curTimestamp;
//                }
//            }

//        }

//        Log.d(TAG, "size of gyro sensor values is " + sensorValues.get(0).size());
        // detection code

        if(!isGestureExecuting && overlapControl == 0){

            if( Segmenter.segmentStartTest(sensorValues, gyroTimestamp)){

                isGestureExecuting = true;

            }
            // deleting previous data in possible gesture data
            possibleGestureData = new ArrayList<>();
            for (int i  = 0; i<6 ; i++){
                possibleGestureData.add(new ArrayList<Double>());
            }
            possibleGestureDataTimestamp.clear();

        }
        else if(isGestureExecuting && overlapControl == 0){
            if(Segmenter.segmentStopTest(sensorValues, gyroTimestamp)){

                isGestureExecuting = false;
                // do gesture pruning here. test whether the length of the gesture is greater than 0.4 seconds
                // if so fire classifier with that data
                // the pruning represent about 0.4 seconds of data

                Log.d(TAG, " condition: " + ( possibleGestureDataTimestamp.get(possibleGestureDataTimestamp.size()-1) - possibleGestureDataTimestamp.get(0))/Math.pow(10,9) );
                if(( possibleGestureDataTimestamp.get(possibleGestureDataTimestamp.size()-1) - possibleGestureDataTimestamp.get(0) )> 40 * Math.pow(10,7)){
                    fireClassifier(possibleGestureData);
                }

            }
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
    private void fireClassifier(ArrayList<ArrayList<Double>> gestureData){
//        ArrayList<Double[]> ret = new ArrayList<>();
//        for(ArrayList<Double> ch : gestureData ){
//            ret.add(ch.toArray(new Double[ch.size()]));
//        }
//        Log.d(TAG, "fired classifier");
//        String label = mDataAnalyzer.classifyTestSample(ret);
//        String name = labelNameMap.get(label);
//        Log.d(TAG, "result: " + name);
//        resultTV.setText(name);
    }

}
