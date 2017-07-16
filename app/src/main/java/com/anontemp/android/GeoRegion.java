package com.anontemp.android;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jaydee on 05.07.17.
 */

public class GeoRegion {

    private float radius;
    private LatLng latLng;

    public GeoRegion(LatLng latLng, float radius) {
        this.radius = radius;
        this.latLng = latLng;
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
