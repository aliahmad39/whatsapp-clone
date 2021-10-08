package com.UniqueBulleteSolutions.whatsapp.Models;

public class userGroups {
    String id , memberID , groupId , memberTime ;

    public userGroups(String id, String memberID, String groupId, String memberTime) {
        this.id = id;
        this.memberID = memberID;
        this.groupId = groupId;
        this.memberTime = memberTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMemberTime() {
        return memberTime;
    }

    public void setMemberTime(String memberTime) {
        this.memberTime = memberTime;
    }
}
