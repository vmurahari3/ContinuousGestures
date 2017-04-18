package edu.gatech.ubicomp.continuousgestures.data.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by batman on 3/11/16.
 */


@Parcel
public class LinearAccelerationData extends SensorData {
    @SerializedName("sensor_name")
    public String mSensorName;

    public LinearAccelerationData() {}

    public LinearAccelerationData(double x, double y, double z, long timestamp) {
        super(x, y, z, timestamp);
        this.mSensorName = "Linear Acceleration";
    }
}
