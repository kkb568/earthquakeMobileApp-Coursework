package com.example.earthquakeapp;

public class Earthquake {
    private String title;
    private String description;
    private String publishedDate;
    private String latitude;
    private String longitude;

    public Earthquake()
    {
        title = "";
        description = "";
        publishedDate = "";
        latitude = "";
        longitude = "";
    }

    public Earthquake(String tt,String dp,String pd, String lat, String lon)
    {
        title = tt;
        description = dp;
        publishedDate = pd;
        latitude = lat;
        longitude = lon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
