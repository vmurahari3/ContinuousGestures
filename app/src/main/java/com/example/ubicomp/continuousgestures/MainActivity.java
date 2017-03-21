package com.example.ubicomp.continuousgestures;

import android.graphics.DashPathEffect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.ubicomp.continuousgestures.Helpers.Buffer;
import com.example.ubicomp.continuousgestures.Learning.DataAnalyzer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
//    Buffer[] buffer_accel ;
//
//    Buffer buffer_accel_timestamp;
//
//    Buffer[] buffer_mag;
//    Buffer buffer_mag_timestamp;
//
//    Buffer[] buffer_gyro;
//    Buffer buffer_gyro_timestamp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // initialize sensor stuff
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        gyroscope =  mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Button test_mode = (Button) findViewById(R.id.test);

//        // initialize buffers
//        buffer_accel = new Buffer[]{new Buffer(buffer_size,3),new Buffer(buffer_size,3),new Buffer(buffer_size,3)};
//        buffer_accel_timestamp = new Buffer(buffer_size,3);
//
//        buffer_mag = new Buffer[]{new Buffer(buffer_size,3),new Buffer(buffer_size,3),new Buffer(buffer_size,3)};
//        buffer_mag_timestamp = new Buffer(buffer_size,3);
//
//        buffer_gyro =  new Buffer[]{new Buffer(buffer_size,3),new Buffer(buffer_size,3),new Buffer(buffer_size,3)};
//        buffer_gyro_timestamp = new Buffer(buffer_size,3);

//        test_mode.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                change_sensor_mode();
//            }
//        });
        mDataAnalyzer = new DataAnalyzer(this);
        for (int i  = 0; i<6 ; i++){
            sensor_values.add(new ArrayList<Double>());
        }

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
//        mSensorManager.unregisterListener(this);
    }
//    private void change_sensor_mode() {
//        // @TODO change string literals to enum
////        if ( status.equals("ON")){
////            status = "OFF";
////            mSensorManager.unregisterListener(this);
////        }else{
////            status = "ON";
////            mSensorManager.registerListener(this,accelerometer,SensorMan        if( sensor_values.get(0).size() == 150 && sensor_values.get(3).size() == 150  ){
//
//
//
////            mSensorManager.registerListener(this,magnetometer,SensorManager.SENSOR_DELAY_FASTEST);
////            mSensorManager.registerListener(this,gyroscope,SensorManager.SENSOR_DELAY_FASTEST);
////
////        }


    public void onSensorChanged(SensorEvent event) {

        // store in a buffer
        int type = event.sensor.getType();
        cur_timestamp = event.timestamp;

        if (type == Sensor.TYPE_LINEAR_ACCELERATION) {

            float[] acceleration = event.values;
            for(int i = 0; i<acceleration.length; i++ ){
                sensor_values.get(i).add((double)acceleration[i]);
            }
            if(sensor_values.get(0).size() > 150){
                for(int i =0; i< acceleration.length; i++){
                    sensor_values.get(i).remove(0);
                }
            }


        } else if (type == Sensor.TYPE_GYROSCOPE) {

            float[] gyro = event.values;
            for(int i = 0; i<gyro.length; i++ ){
                sensor_values.get(i+3).add((double)gyro[i]);
            }
            if(sensor_values.get(3).size() > 150){
                for(int i =0; i< gyro.length; i++){
                    sensor_values.get(i+3).remove(0);
                }
            }
        }

        if( sensor_values.get(0).size() == 150 && sensor_values.get(3).size() == 150  ) {
            // fire classifier
            if (!first_fire) {
                first_fire = true;
                prev_fire_timestamp = event.timestamp;
                fire_classifier();
            }
            else{
                // 10 percent overlap for a 3 sec window
                if (( cur_timestamp - prev_fire_timestamp) > Math.pow(10,9)/3){
                    fire_classifier();
                    prev_fire_timestamp = cur_timestamp;
                }
            }

        }




//        if (buffer_gyro_timestamp.is_main_full) {
//            double cur_energy = buffer_gyro[0].getEnergy(buffer_gyro_timestamp.getRawData());
////            Log.d("hello", "this is the energy !!!!!!!!!!!!1" + cur_energy + "");
//            if (cur_energy > threshold && !flag_sound_generated) {
//                //generate sound
//                Log.d("", "detected");
//                flag_sound_generated = true;
//                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//                toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP2, 150);
//            }
//        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void fire_classifier(){
        ArrayList<Double[]> ret = new ArrayList<>();
        for(ArrayList<Double> ch : sensor_values ){
            ret.add(ch.toArray(new Double[ch.size()]));
        }
        mDataAnalyzer.addTestSample(ret);
        String label = mDataAnalyzer.classifyGesture();
        Log.d("hello", "result !!!!!!! " + label);
    }


}
