package com.example.khfy6tme.runningtracker.utilities;

/* CLASS TO ENCLOSE ALL OF A RUN LOG DATA IN ONE OBJECT */

public class RunLogDetail {

    private int id;
    private String time = "";
    private String date = "";
    private String distance = "";
    private String duration = "";
    private String avgSpeed = "";
    private String highSpeed = "";

    public RunLogDetail () { }

    public RunLogDetail(int id, String time, String date, String distance, String duration, String avgSpeed, String highSpeed) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.distance = distance;
        this.duration = duration;
        this.avgSpeed = avgSpeed;
        this.highSpeed = highSpeed;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getHighSpeed() {
        return highSpeed;
    }

    public void setHighSpeed(String highSpeed) {
        this.highSpeed = highSpeed;
    }
} // end of class RunLogDetail.java
