package com.example.taxiapp;

import com.google.firebase.database.PropertyName;

import java.time.LocalDateTime;
import java.util.Date;

public class History {
    @PropertyName("User")
    private String user;
    @PropertyName("Driver")
    private String driver;
    @PropertyName("Time")
    private Date time;



    public History(String user, String driver,Date time) {
        this.user = user;
        this.driver = driver;
        this.time = time;
    }

    @PropertyName("user")
    public String getUser() {
        return user;
    }

    @PropertyName("driver")
    public String getDriver() {
        return driver;
    }

    @PropertyName("time")
    public Date getTime(){ return time; }



    public void setUser(String user) {
        this.user = user;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setTime(Date time) {this.time = time; }


    public History() {

    }
}
