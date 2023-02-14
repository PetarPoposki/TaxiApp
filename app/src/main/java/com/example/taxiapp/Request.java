package com.example.taxiapp;

import com.google.firebase.database.PropertyName;

public class Request {
    @PropertyName("User")
    private String user;
    @PropertyName("Driver")
    private String driver;



    public Request(String user, String driver) {
        this.user = user;
        this.driver = driver;
    }

    @PropertyName("user")
    public String getUser() {
        return user;
    }

    @PropertyName("driver")
    public String getDriver() {
        return driver;
    }



    public void setUser(String user) {
        this.user = user;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }


    public Request() {

    }
}
