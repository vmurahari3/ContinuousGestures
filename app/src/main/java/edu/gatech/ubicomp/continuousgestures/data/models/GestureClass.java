package edu.gatech.ubicomp.continuousgestures.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by greyes on 11/11/14.
 */

@org.parceler.Parcel
public class GestureClass
{
    private static final String TAG = GestureClass.class.getSimpleName();

    @SerializedName("id")
    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("updatedAt")
    public String updatedAt;


    public GestureClass(String name, String description) {
        this.name = name;
    }
    public GestureClass() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


}
