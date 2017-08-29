package com.anontemp.android.model;

import com.anontemp.android.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by jaydee on 28.08.17.
 */

public class Capital {

    private Tweet info;
    private MarkerOptions options;

    public static Capital newInstance(String title, String subtitle, LatLng coordinates, Tweet info) {

        Capital capital = new Capital();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(title).snippet(subtitle).position(coordinates);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1));
        markerOptions.infoWindowAnchor(0.5f, 0.5f).draggable(false);
        capital.setOptions(markerOptions);
        capital.setInfo(info);


        return capital;
    }

    public Tweet getInfo() {
        return info;
    }

    public void setInfo(Tweet info) {
        this.info = info;
    }

    public MarkerOptions getOptions() {
        return options;
    }

    public void setOptions(MarkerOptions options) {
        this.options = options;
    }


}
