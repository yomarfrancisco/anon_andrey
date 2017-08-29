package com.anontemp.android.misc;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jaydee on 05.07.17.
 */

public class GeoRegion {

    private float radius;
    private LatLng latLng;
    private int color;
    private String title;
    private String menuTitle;


    public GeoRegion(String title, LatLng latLng, float radius, String menuTitle, int color) {
        this.radius = radius;
        this.latLng = latLng;
        this.title = title;
        this.menuTitle = menuTitle;
        this.color = color;
    }

    public GeoRegion(LatLng latLng, float radius) {
        this.radius = radius;
        this.latLng = latLng;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
