package edu.gatech.ubicomp.continuousgestures.data.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;

import edu.gatech.ubicomp.continuousgestures.data.learning.FeatureExtractor;

/**
 * Created by batman on 4/11/16.
 */

@Parcel
public class SampleFeatureVector {
    @SerializedName("deviceId")
    public long deviceID;
    @SerializedName("featureVector")
    public String featureVector = "";

    public SampleFeatureVector() {}

    public SampleFeatureVector(ArrayList<Double[]> data, long deviceID) {
        this.deviceID = deviceID;
        this.featureVector = convertFeatureVectorToCommaSeperatedString(data);
    }

    private String convertFeatureVectorToCommaSeperatedString(ArrayList<Double[]> data) {
        ArrayList<Double> returnedValues;
        FeatureExtractor featureExtractor = new FeatureExtractor();
        returnedValues = featureExtractor.calculateFeatures(data);
        return StringUtils.join(returnedValues, ",");
    }

    public ArrayList<Double> getFeatureVector() {
        ArrayList<Double> features = new ArrayList<>();
        String[] featuresString = this.featureVector.split(",");
        for (String fetre : featuresString) {
            features.add(Double.valueOf(fetre));
        }
        return features;
    }
}
