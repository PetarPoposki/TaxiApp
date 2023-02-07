package com.example.taxiapp;

import android.net.Uri;

import com.google.firebase.database.PropertyName;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Driver {
    @PropertyName("Name")
    private String name;
    @PropertyName("Image")
    private StorageReference image;


    public Driver(String name, StorageReference image) {
        this.name = name;
        this.image = image;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("image")
    public StorageReference getImage() {
        return image;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setImage(StorageReference image) {
        this.image = image;
    }


    public Driver() {

    }
}
