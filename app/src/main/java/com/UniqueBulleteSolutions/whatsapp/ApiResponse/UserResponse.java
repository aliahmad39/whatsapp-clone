package com.UniqueBulleteSolutions.whatsapp.ApiResponse;

import com.UniqueBulleteSolutions.whatsapp.Models.ChatUsers;
import com.UniqueBulleteSolutions.whatsapp.Models.MessageModel;
import com.UniqueBulleteSolutions.whatsapp.Models.Status;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.Models.groupMessages;
import com.UniqueBulleteSolutions.whatsapp.Models.userGroups;

import java.util.ArrayList;
import java.util.List;

public class UserResponse {
    String status, message , userName , userAbout , userPic;

    List<Users> data;
    List<Users> showUser;

    List <Users> groupMembers ;
    List<Users> groupList;
    List<MessageModel> messagelist;
    List<groupMessages> groupMessagelist;
    List<groupMessages> deleteLog;
    List<Status> liststatus;




    public UserResponse() {
    }



    public List<Users> getShowUser() {
        return showUser;
    }

    public UserResponse(String status, String message, List<Users> data, List<Users> groupMembers, List<Users> groupList, List<MessageModel> messagelist, List<groupMessages> groupMessagelist, List<Status> liststatus) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.groupMembers = groupMembers;
        this.groupList = groupList;
        this.messagelist = messagelist;
        this.groupMessagelist = groupMessagelist;
        this.liststatus = liststatus;
    }


    public List<groupMessages> getDeleteLog() {
        return deleteLog;
    }

    public List<groupMessages> getGroupMessagelist() {
        return groupMessagelist;
    }

    public List<Users> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<Users> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public List<Users> getGroupList() {
        return groupList;
    }

    public List<Status> getListstatus() {
        return liststatus;
    }

    public List<MessageModel> getMessagelist() {
        return messagelist;
    }

    public void setMessagelist(List<MessageModel> messagelist) {
        this.messagelist = messagelist;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Users> getData() {
        return data;
    }

    public void setData(List<Users> data) {
        this.data = data;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAbout() {
        return userAbout;
    }

    public void setUserAbout(String userAbout) {
        this.userAbout = userAbout;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }


    // Getter Methods


    // Setter Methods



}
