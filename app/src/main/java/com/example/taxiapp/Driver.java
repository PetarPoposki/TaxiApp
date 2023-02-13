package com.example.taxiapp;

import android.net.Uri;

import com.google.firebase.database.PropertyName;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Driver {
    @PropertyName("Name")
    private String name;
    @PropertyName("Image")
    private String image;
    @PropertyName(("Busy"))
    private Integer busy;


    public Driver(String name, String image, Integer busy) {
        this.name = name;
        this.image = image;
        this.busy = busy;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("image")
    public String getImage() {
        return image;
    }

    @PropertyName("busy")
    public Integer getBusy() {
        return busy;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBusy(Integer busy) { this.busy = busy; }


    public Driver() {

    }
}
