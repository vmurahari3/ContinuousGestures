package edu.gatech.ubicomp.continuousgestures.data.models;

import android.hardware.Sensor;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import edu.gatech.ubicomp.continuousgestures.common.Constants;

/**
 * Created by batman on 3/11/16.
 */

@Parcel
public class SampleSensorData {
    private static final String TAG = SampleSensorData.class.getSimpleName();

    @SerializedName("startedAt")
    public long mStartedAt;
    @SerializedName("stoppedAt")
    public long mStoppedAt;
    @SerializedName("duration")
    public long mDurationInMilliSeconds;
    @SerializedName("deviceID")
    public long mDeviceId;
    @SerializedName("linearAcceleration")
    public ArrayList<LinearAccelerationData> mLinearAccelerationData = new ArrayList<>();
    @SerializedName("gyro")
    public ArrayList<GyroData> mGyroData = new ArrayList<>();

    public SampleSensorData() {}

    public Double[] getLinearAccelerationX() {
        Double[] linearAccelerationX = new Double[mLinearAccelerationData.size()];
        for (int i = 0; i < mLinearAccelerationData.size(); i++) {
            linearAccelerationX[i] = mLinearAccelerationData.get(i).mX;
        }
        return linearAccelerationX;
    }

    public Double[] getLinearAccelerationY() {
        Double[] linearAccelerationY = new Double[mLinearAccelerationData.size()];
        for (int i = 0; i < mLinearAccelerationData.size(); i++) {
            linearAccelerationY[i] = mLinearAccelerationData.get(i).mY;
        }
        return linearAccelerationY;
    }

    public Double[] getLinearAccelerationZ() {
        Double[] linearAccelerationZ = new Double[mLinearAccelerationData.size()];
        for (int i = 0; i < mLinearAccelerationData.size(); i++) {
            linearAccelerationZ[i] = mLinearAccelerationData.get(i).mZ;
        }
        return linearAccelerationZ;
    }

    public Double[] getGyroX() {
        Double[] gyroX = new Double[mGyroData.size()];
        for (int i = 0; i < mGyroData.size(); i++) {
            gyroX[i] = mGyroData.get(i).mX;
        }
        return gyroX;
    }

    public Double[] getGyroY() {
        Double[] gyroY = new Double[mGyroData.size()];
        for (int i = 0; i < mGyroData.size(); i++) {
            gyroY[i] = mGyroData.get(i).mY;
        }
        return gyroY;
    }

    public Double[] getGyroZ() {
        Double[] gyroZ = new Double[mGyroData.size()];
        for (int i = 0; i < mGyroData.size(); i++) {
            gyroZ[i] = mGyroData.get(i).mZ;
        }
        return gyroZ;
    }

    public ArrayList<Double[]> getAllData() {
        ArrayList<Double[]> allData = new ArrayList<>();
        allData.add(getLinearAccelerationX());
        allData.add(getLinearAccelerationY());
        allData.add(getLinearAccelerationZ());
        allData.add(getGyroX());
        allData.add(getGyroY());
        allData.add(getGyroZ());
        return allData;
    }

    public void makeLinearAccAndGyroDataSizeEqual() {
        int difference = mLinearAccelerationData.size() - mGyroData.size();
        if(difference == 0) {
            return;
        } else if (difference < 0) {
            //Gyro got more data
            for(int i = difference ; i < 0; i++) {
                //Remove the last element added
                mGyroData.remove((mGyroData.size() - 1));
            }
        } else {
            // LinAccel got more data
            for(int i = difference ; i > 0; i--) {
                //Remove the last element added
                mLinearAccelerationData.remove((mLinearAccelerationData.size() - 1));
            }
        }
    }

    public void storeData(String receivedData) {
        if (!receivedData.isEmpty()) {
            String splits[] = receivedData.split(",");
            String sensorName = splits[1];
            long timestamp = Long.parseLong(splits[2]);
            double x = Double.parseDouble(splits[3]);
            double y = Double.parseDouble(splits[4]);
            double z = Double.parseDouble(splits[5]);

            switch (Constants.MAP_SENSOR_NAME_TO_TYPE.get(sensorName)) {
                case Sensor.TYPE_GYROSCOPE:
                    mGyroData.add(new GyroData(x, y, z, timestamp));
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    mLinearAccelerationData.add(new LinearAccelerationData(x, y, z, timestamp));
                    break;
            }
        } else {
            Log.d(TAG, "Received Empty Sensor Sample String. Discarding it.");
        }
    }
}
