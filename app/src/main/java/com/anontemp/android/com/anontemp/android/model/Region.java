package com.anontemp.android.com.anontemp.android.model;

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
    private String regiontext;

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

    public String getRegiontext() {
        return regiontext;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("regiontext", regiontext);
        result.put("regionId", regionId);


        return result;
    }
}
