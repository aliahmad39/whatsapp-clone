package com.UniqueBulleteSolutions.whatsapp.Models;

public class Status {
   String id , name ,  phoneNo , userPic  , status_path , sender_id , status_time;

    public Status(String id, String name, String phoneNo, String userPic, String status_path, String sender_id, String status_time) {
        this.id = id;
        this.name = name;
        this.phoneNo = phoneNo;
        this.userPic = userPic;
        this.status_path = status_path;
        this.sender_id = sender_id;
        this.status_time = status_time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getStatus_path() {
        return status_path;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getStatus_time() {
        return status_time;
    }
}
