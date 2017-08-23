package com.anontemp.android.model;

import android.support.annotation.NonNull;

import com.anontemp.android.BaseTweetItem;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaydee on 14.07.17.
 */

@IgnoreExtraProperties
public class Tweet extends BaseTweetItem implements Comparable<Tweet> {

    private String key;
    private String username;
    private String firstName;
    private String tweetId;
    private String tweetText;
    @PropertyName("votes")
    private Integer tweetVotes;
    private Boolean allowComment;
    private Integer countdown;
    private String date;
    @PropertyName("regionname")
    private String regionName;
    private String userId;
    private String moodText;
    @PropertyName("regionid")
    private String regionId;
    private String location;
    private String timeToLive;
    private Long realDate;
    private String tweetGif;
    private Double tweetRecord;
    private Map<String, String> comments;
    private Map<String, String> loves;
    private Map<String, String> reporters;


    public Tweet() {
    }

    public Map<String, String> getLoves() {
        return loves;
    }

    public void setLoves(Map<String, String> loves) {
        this.loves = loves;
    }

    public Map<String, String> getReporters() {
        return reporters;
    }

    public void setReporters(Map<String, String> reporters) {
        this.reporters = reporters;
    }

    public Map<String, String> getComments() {
        return comments;
    }

    public void setComments(Map<String, String> comments) {
        this.comments = comments;
    }

    public Double getTweetRecord() {
        return tweetRecord;
    }

    public void setTweetRecord(Double tweetRecord) {
        this.tweetRecord = tweetRecord;
    }

    public String getTweetGif() {
        return tweetGif;
    }

    public void setTweetGif(String tweetGif) {
        this.tweetGif = tweetGif == null ? "" : tweetGif;
    }

    public Long getRealDate() {
        return realDate;
    }

    public void setRealDate(Long realDate) {
        this.realDate = realDate;
    }

    public String getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(String timeToLive) {
        this.timeToLive = timeToLive;
    }



    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getMoodText() {
        return moodText;
    }

    public void setMoodText(String moodText) {
        this.moodText = moodText;
    }

    public Boolean getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    public Integer getCountdown() {
        return countdown;
    }

    public void setCountdown(Integer countdown) {
        this.countdown = countdown;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public Integer getTweetVotes() {
        return tweetVotes;
    }

    public void setTweetVotes(Integer tweetVotes) {
        this.tweetVotes = tweetVotes;
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
        result.put("countdown", countdown);
        result.put("moodText", moodText);
        result.put("allowComment", allowComment);
        result.put("regionid", regionId);
        result.put("regionname", regionName);
        result.put("location", location);
        result.put("tweetGif", tweetGif);
        result.put("tweetRecord", tweetRecord);
        return result;

    }


    @Override
    public int compareTo(@NonNull Tweet tweet) {
        return tweet.getRealDate().compareTo(getRealDate());
    }

    @Override
    public int getType() {
        return Type.TYPE_TWEET;
    }
}
