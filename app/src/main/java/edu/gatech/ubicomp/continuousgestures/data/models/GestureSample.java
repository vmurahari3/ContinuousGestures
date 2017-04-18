package edu.gatech.ubicomp.continuousgestures.data.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Aman Parnami on 11/4/14.
 */

@Parcel
public class GestureSample
{
    private static final String TAG = GestureSample.class.getSimpleName();

    @SerializedName("id")
    public long id;
    @SerializedName("classID")
    public long classId;
    @SerializedName("data")
    public ArrayList<SampleSensorData> data = new ArrayList<>();
    @SerializedName("featureVectors")
    public ArrayList<SampleFeatureVector> featureVectors = new ArrayList<>();
    @SerializedName("createdAr")
    public String createdAt;

    public GestureSample() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
