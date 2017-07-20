package com.anontemp.android.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaydee on 14.07.17.
 */

@IgnoreExtraProperties
public class Region {

    private DatabaseReference ref;
    private String key;

    private String regionId;
    private String regionText;


    public Region(String regionId, String regionText) {
        this.regionId = regionId;
        this.regionText = regionText;
    }

    public Region() {
    }

    public DatabaseReference getRef() {
        return ref;
    }

    public String getKey() {
        return key;
    }

    public String getRegionId() {
        return regionId;
    }

    public String getRegionText() {
        return regionText;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("regionText", regionText);
        result.put("regionId", regionId);


        return result;
    }
}
