package edu.gatech.ubicomp.continuousgestures.Constants;

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

    public static final long MS_IN_SEC = 1000;

    public static final long US_IN_SEC = 1000000; // US is microseconds

    public static final long NS_IN_SEC = 1000000000; // NS is nanoseconds

    public static final String[] DEFAULT_SENSOR_NAMES = {"LinearAccel", "Gyroscope"};//, "Magnetometer"};

    public static final String[] DEFAULT_CHANNEL_LABELS = {"x", "y", "z"};

    public static final String PREF_NAME = "MogestePref";


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


    /** Minimum acceptable difference between a peak and adjoining trough. */
    public final static double DELTA_BETW_PEAK_TROUGH = 1000;

    /** Training mode */
    public static final int TRAINING_MODE = 0;

    /** Testing mode */
    public static final int TESTING_MODE = 1;

    /** Runtime modes for our system */
    public static final String[] RUNTIME_MODE_LABELS = {"trainingMode", "testingMode"};

    /** Size of window used for classification */
    public static final int WIN_SIZE = 20; // 0.4 sec @50Hz sampling rate
    public static final long SLIDE_SIZE = 60*US_IN_SEC; // time in nano seconds. Duration is 0.06 second
    public static final int OVERLAP = 3; // Overlap is used in the segmentation algorithm. The segmentation algorithm is only called after OVERLAP number of points
    public static final double ENERGY_THRESHOLD = 0.2;

    public static final HashMap<Integer, String> MAP_SENSOR_TYPE_TO_NAME = new HashMap<Integer, String>();
    static
    { //Ref: http://stackoverflow.com/questions/507602/how-can-i-initialize-a-static-map
        MAP_SENSOR_TYPE_TO_NAME.put(Sensor.TYPE_LINEAR_ACCELERATION, "LinearAccel");
        MAP_SENSOR_TYPE_TO_NAME.put(Sensor.TYPE_GYROSCOPE, "Gyroscope");
        MAP_SENSOR_TYPE_TO_NAME.put(Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer");
        MAP_SENSOR_TYPE_TO_NAME.put(Sensor.TYPE_ROTATION_VECTOR, "RotationVector");
    }

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