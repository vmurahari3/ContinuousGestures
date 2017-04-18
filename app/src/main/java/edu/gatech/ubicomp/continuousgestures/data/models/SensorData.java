package edu.gatech.ubicomp.continuousgestures.data.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by batman on 3/11/16.
 */

@Parcel
public class SensorData {
    @SerializedName("x")
    public double mX;
    @SerializedName("y")
    public double mY;
    @SerializedName("z")
    public double mZ;
    @SerializedName("timestamp")
    public long mTimeStamp;

    public SensorData() {}

    public SensorData(double x, double y, double z, long timestamp) {
        this.mX = x;
        this.mY = y;
        this.mZ = z;
        this.mTimeStamp = timestamp;
    }
}
