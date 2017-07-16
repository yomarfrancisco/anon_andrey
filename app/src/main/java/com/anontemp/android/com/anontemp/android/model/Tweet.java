package com.anontemp.android.com.anontemp.android.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaydee on 14.07.17.
 */

@IgnoreExtraProperties
public class Tweet {

    private String key;
    private String username;
    private String firstName;
    private String tweetId;
    private String tweetText;
    @PropertyName("votes")
    private Integer tweetVotes;
    private Boolean allowComment;
    private Integer countDown;
    private String date;
    @PropertyName("regionname")
    private String regionName;
    private String userId;
    private String moodText;
    @PropertyName("regionid")
    private String regionId;
    private String location;
    private Long _id;

    public Tweet() {
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getLocation() {
        return location;
    }

    public String getRegionId() {
        return regionId;
    }

    public String getMoodText() {
        return moodText;
    }

    public Boolean getAllowComment() {
        return allowComment;
    }

    public Integer getCountDown() {
        return countDown;
    }

    public String getDate() {
        return date;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getUserId() {
        return userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getTweetId() {
        return tweetId;
    }

    public String getTweetText() {
        return tweetText;
    }

    public Integer getTweetVotes() {
        return tweetVotes;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tweetId", tweetId);
        result.put("userId", userId);
        result.put("username", username);
        result.put("firstName", firstName);
        result.put("tweetText", tweetText);
        result.put("votes", tweetVotes);
        result.put("date", date);
        result.put("countdown", countDown);
        result.put("moodText", moodText);
        result.put("allowComment", allowComment);
        result.put("regionid", regionId);
        result.put("regionname", regionName);
        result.put("location", location);
        return result;

    }


}
