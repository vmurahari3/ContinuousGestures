package com.example.ubicomp.continuousgestures.Constants;

import android.hardware.Sensor;

import java.util.HashMap;

/**
 * Created by Aman Parnami on 10/2/14.
 */
public final class Constants {

    /** Listening to a single device. */
    public static final int LISTENING_MODE_SINGLE = 0;
    /** Listening to a multiple devices. */
    public static final int LISTENING_MODE_MULTIPLE = 1;

    /** Total number of gyroscopes */
    public static final int NUM_OF_GYROS = 1;

    /** Total number of gyroscopes */
    public static final int NUM_OF_ACCEL = 1;

    /** Total number of sensors in the system. */
    //public static final int NUM_OF_SENSORS = 6;
    public static final int NUM_OF_SENSORS = NUM_OF_GYROS + NUM_OF_ACCEL;

    /** Total number of sensor axes */
    public static final int NUM_OF_SENSOR_AXES = 3;

    /** Total number of gyroscopes channels */
    public static final int NUM_OF_GYRO_CHANNELS = NUM_OF_GYROS * NUM_OF_SENSOR_AXES;

    /** Total number of accelerometer channels */
    public static final int NUM_OF_ACCEL_CHANNELS = NUM_OF_ACCEL * NUM_OF_SENSOR_AXES;

    /** Total number of channels in data. */
    public static final int NUM_OF_CHANNELS = NUM_OF_ACCEL_CHANNELS + NUM_OF_GYRO_CHANNELS;

    /** Total number of channels in data plus the line index */
    public static final int NUM_OF_CHANNELS_WITH_INDEX = NUM_OF_CHANNELS + 1;

    public static final int DEFAULT_NUM_CHANNELS_FOR_MOTION_SENSORS = 3;

    public static final int SAMPLING_RATE_WEAR = 50;

    public static final int SAMPLING_RATE_PHONE = 200;

    public static final long MS_IN_SEC = 1000;

    public static final String[] DEFAULT_SENSOR_NAMES = {"LinearAccel", "Gyroscope"};//, "Magnetometer"};

    public static final String[] DEFAULT_CHANNEL_LABELS = {"x", "y", "z"};

    public static final String PREF_NAME = "MogestePref";

    public static final String INIT_BG_OPERATION_TYPE = "init";

    public static final String CROSSVALIDATION_BG_OPERATION_TYPE = "crossvalidation";

    public static final String LOADGROUP_BG_OPERATION_TYPE = "loadgesturegroup";


    /** List of input channel labels */
    public static final String[] GESTURE_CHANNEL_LABELS = new String[Constants.NUM_OF_CHANNELS];
    static
    {
        int i = 0;
        for(String sensor: DEFAULT_SENSOR_NAMES)
        {
            for(String chLabel: DEFAULT_CHANNEL_LABELS)
            {
                //We want the resulting channel labels to look like LinearAccelX
                GESTURE_CHANNEL_LABELS[i++] = sensor + chLabel.toUpperCase();
            }
        }
    }

    public final static String[] DEMO_CLASSIFIER_LABELS =
            {
                    "A", "B", "C"
            };

    //TODO: This should not be a constant as each device should convey this information per sensor to the phone.
    public final static Float ACCL_VALUE_RANGE = 24.0f;
    public final static Float MOTO360_ACCL_VALUE_RANGE = 0.04f;

    public final static Float PHONE_GYRO_VALUE_RANGE = 24.0f;
    public final static Float MOTO360_GYRO_VALUE_RANGE = 12.0f;

    /** Minimum acceptable difference between a peak and adjoining trough. */
    public final static double DELTA_BETW_PEAK_TROUGH = 1000;

    /** Search radius used by DTW. **/
    public static final int DTW_SEARCH_RADIUS = 30;

    /** Training mode */
    public static final int TRAINING_MODE = 0;

    /** Testing mode */
    public static final int TESTING_MODE = 1;

    /** Runtime modes for our system */
    public static final String[] RUNTIME_MODE_LABELS = {"trainingMode", "testingMode"};

    /** Size of window used for classification */
    public static final int WIN_SIZE = 20; // 0.4 sec @50Hz sampling rate
    public static final long SLIDE_SIZE = (long) 6 *  (long)Math.pow(10,7); // time in nano seconds. Duration is 0.06 second
    public static final int OVERLAP = 3; // Overlap is used in the segmentation algorithm. The segmentation algorithm is only called after OVERLAP number of points
    public static final double ENERGY_THRESHOLD = 0.2;
    /** Index of sensor axis of interest. x(0), y(1), z(2) */
    public static final int AXIS_OF_INTEREST_INDEx = 0;

    public static final HashMap<Integer, String> MAP_SENSOR_TYPE_TO_NAME = new HashMap<Integer, String>();
    static
    { //Ref: http://stackoverflow.com/questions/507602/how-can-i-initialize-a-static-map
        MAP_SENSOR_TYPE_TO_NAME.put(Sensor.TYPE_LINEAR_ACCELERATION, "LinearAccel");
        MAP_SENSOR_TYPE_TO_NAME.put(Sensor.TYPE_GYROSCOPE, "Gyroscope");
        MAP_SENSOR_TYPE_TO_NAME.put(Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer");
        MAP_SENSOR_TYPE_TO_NAME.put(Sensor.TYPE_ROTATION_VECTOR, "RotationVector");
    }

    /** Key used for sensor array in JSON objects. */
    public static final String KEY_SENSOR_ARRAY = "sensors";

    /** Split points used in SAX for conversion to symbols.
     * Below is the algorithm for generating the split points, to be run in Matlab
     startRange = 2;
     stdc= 1;
     endRange = 512;

     table = cell(endRange-startRange,1);
     for r=startRange:endRange
     table{r-startRange+1} = norminv((1:r-1)/r,0,stdc);
     end
     * */
    public static final double [][] SPLIT_POINTS = {
            {0.0},
            {-0.430727299295458,0.430727299295457},
            {-0.674489750196082,0.0,0.674489750196082},
            {-0.841621233572914,-0.253347103135800,0.253347103135800,0.841621233572914},
            {-0.967421566101701,-0.430727299295458,0.0,0.430727299295457,0.967421566101701},
            {-1.06757052387814,-0.565948821932863,-0.180012369792705,0.180012369792705,0.565948821932863,1.06757052387814},
            {-1.15034938037601,-0.674489750196082,-0.318639363964375,0,0.318639363964375,0.674489750196082,1.15034938037601},
            {-1.22064034884735,-0.764709673786387,-0.430727299295458,-0.139710298881862,0.139710298881862,0.430727299295457,0.764709673786387,1.22064034884735},
            {-1.28155156554460,-0.841621233572914,-0.524400512708041,-0.253347103135800,0,0.253347103135800,0.524400512708041,0.841621233572914,1.28155156554460},
            {-1.33517773611894,-0.908457868537385,-0.604585346583237,-0.348755695517045,-0.114185294321428,0.114185294321428,0.348755695517045,0.604585346583237,0.908457868537386,1.33517773611894}
    };

    /** Number of buckets for ECDF features*/
    public static final int ECDF_LENGTH = 15;

    /**
     The caller should be prevented from constructing objects of
     this class, by declaring this private constructor.
     */
    private Constants()
    {
        // This prevents even the native class from
        // calling this constructor as well
        throw new AssertionError();
    }
}