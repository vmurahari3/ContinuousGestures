//package com.example.ubicomp.continuousgestures;
//
//import android.app.Service;
//import android.content.Intent;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.View;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//
//public class SensorActivity extends AppCompatActivity implements SensorEventListener {
//
//    private final static boolean DEBUG = false;
//    private SensorManager mSensorManager;
//    private Sensor mLinearAcceleration;
//    private Sensor mGyroscope;
//    private Sensor mMagneticField;
//    private Sensor mRotationVector;
//
////    static final String EXTRA_SENSOR_DATA_LINE = "edu.gatech.ubicomp.mogeste.EXTRA_SENSOR_DATA_LINE";
////    static final String ACTION_SEND_PHONE_SENSOR_DATA = deviceName + "DataReceived";
//
//    String[] sensorChannels = Constants.DEFAULT_CHANNEL_LABELS;
//
//    public JSONObject currentSensorDataLineJSON = null;
//    JSONObject currentSensorPacket = new JSONObject();
//
//    //member variable used for passing sensor info in intent
//    private float[] values;
//    private String sensorType;
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        Log.d("glasssensorlistener", "on create called");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mSensorManager.unregisterListener(this);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        currentSensorPacket = new JSONObject();
//        Sensor sensor = event.sensor;
//        String sensorName = "";
//
//        // Check our hashmap of sensor names
//        if (Constants.MAP_SENSOR_TYPE_TO_NAME.containsKey(sensor.getType())) {
//            sensorName = Constants.MAP_SENSOR_TYPE_TO_NAME.get(sensor.getType());
//        } else {
//            //TODO: What happens here?
//        }
//
//        // Convert event values into JSONObject
//        currentSensorDataLineJSON = Utils.floatArray2JSONObject(sensorChannels, event.values);
//        values = event.values;
//        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
//            sensorType = "LinearAcceleration";
//        else
//            sensorType = "None";
//        // Create sensor packet JSONObject
//        try {
//            JSONArray sensorArr = new JSONArray();
//            sensorArr.put(sensorName);
//
//            //Putting a JSONArray instead of a single value to accommodate for custom devices that
//            // can send data for multiple sensors in the same packet.
//            currentSensorPacket.put(Constants.KEY_SENSOR_ARRAY, sensorArr);
//
//            JSONArray sensorDataArr = new JSONArray();
//            sensorDataArr.put(currentSensorDataLineJSON);
//
//            //Putting a JSONArray instead of single value to accommodate for scenarios in which data
//            //is received in batches. mDataManager.startNewFile(getNewFileName());
//
//            currentSensorPacket.put(sensorName, sensorDataArr);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        dataReceivedMessageToActivity();
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//
//    private void dataReceivedMessageToActivity() {
////        Intent intent = new Intent(ACTION_SEND_PHONE_SENSOR_DATA);
////        sendLocalBroadcast(intent);
//    }
//
//    private void sendLocalBroadcast(Intent intent) {
//        intent.putExtra(EXTRA_SENSOR_DATA_LINE, String.valueOf(currentSensorPacket));
////        intent.putExtra("RAWDATA", values[0] + "," + values[1] + "," + values[2]);
////        intent.putExtra("SensorType", sensorType);
//////        Log.i("stringvalueof", String.valueOf(currentSensorPacket));
////        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }
//}
//}
