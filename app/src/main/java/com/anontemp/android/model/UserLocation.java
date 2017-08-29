package com.anontemp.android.model;

/**
 * Created by jaydee on 28.08.17.
 */

public class UserLocation {

    private String uid;
    private Double lat;
    private Double lng;


    public UserLocation(String uid, Double lat, Double lng) {
        this.uid = uid;
        this.lat = lat;
        this.lng = lng;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
