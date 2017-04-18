package edu.gatech.ubicomp.continuousgestures.common;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by Aman Parnami on 10/2/14.
 */
public final class Config {

   /** Specify whether we want to listen to one device or multiple devices.  */
   public static final int LISTENING_MODE = Constants.LISTENING_MODE_SINGLE;

   public static final long GESTURE_LENGTH_IN_TIME = 2000; //milliseconds
    public static final long COUNTDOWN_DURATION_IN_MS = 3100; //3 seconds
    public static final long SETUP_COUNTDOWN_DURATION_IN_MS = 10000; //10 seconds

    public static final int SENSOR_DELAY_50_HZ_U_SEC = 20000; // 20000 microseconds

   public static final JSONObject DEFAULT_SENSOR_CHANNEL_INFO = new JSONObject();
   static
   {
       String[] sensorNames = Constants.DEFAULT_SENSOR_NAMES;
       String[] channelLabels = Constants.DEFAULT_CHANNEL_LABELS;
       boolean isChannelActive = true;

       for (String sensor: sensorNames)
       {
           JSONObject singleSensorChannels = new JSONObject();
           for (String channel : channelLabels)
           {
               try {
                   singleSensorChannels.put(channel, isChannelActive);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
           try {
               DEFAULT_SENSOR_CHANNEL_INFO.put(sensor, singleSensorChannels);
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
   }

    public static final String DIR_PATH_EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getAbsolutePath()+'/'+Constants.APP_NAME;
    public static final String DIR_PATH_MEDIA = DIR_PATH_EXTERNAL_STORAGE + "/media";
    public static final String DIR_PATH_MODELS = DIR_PATH_EXTERNAL_STORAGE + "/models";
    public static final String DIR_PATH_MISC = DIR_PATH_EXTERNAL_STORAGE + "/misc";
    public static final String DIR_PATH_LOG = DIR_PATH_EXTERNAL_STORAGE + "/logs";

    /**
     The caller should be prevented from constructing objects of
     this class, by declaring this private constructor.
     */

    private Config()
    {
        // This prevents even the native class from
        // calling this constructor as well
        throw new AssertionError();
    }
}
