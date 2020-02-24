package com.example.android.quakereport;

public class Earthquake {
    private String location;
    private long timeInMilliseconds;
    private double magnitude;
    private String url;

    public Earthquake(String city, long timeInMilliseconds, double magnitude, String url) {
        this.location = city;
        this.timeInMilliseconds = timeInMilliseconds;
        this.magnitude = magnitude;
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public void setTimeInMilliseconds(long timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
