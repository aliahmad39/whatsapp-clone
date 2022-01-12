package com.UniqueBulleteSolutions.whatsapp.Models;



public class Users  {
    String id , email , password , phoneNo , name , status , message  ,userPic , time ,userStatus , personStatus , lastMessage;
    String index , userAbout , userName;
    String receiver_id , sender_id;


    public Users(String phoneNo, String name) {
        this.phoneNo = phoneNo;
        this.name = name;
    }
    public Users(String id ,String phoneNo, String name) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.name = name;
    }

    public String getUserAbout() {
        return userAbout;
    }

    public void setUserAbout(String userAbout) {
        this.userAbout = userAbout;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    private String ID , memberID , groupID ,groupTime , title , groupLastMsg , groupIcon;


    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getGroupLastMsg() {
        return groupLastMsg;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

// Getter Methods

    public String getID() {
        return ID;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupTime() {
        return groupTime;
    }

    public String getTitle() {
        return title;
    }
// Setter Methods





    public Users(){}

    public Users(String id, String phoneNo, String name, String userPic, String time, String userStatus, String lastMessage, String userAbout, String userName) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.name = name;
        this.userPic = userPic;
        this.time = time;
        this.userStatus = userStatus;
        this.lastMessage = lastMessage;
        this.userAbout = userAbout;
        this.userName = userName;
    }

    public Users(String id, String email, String password, String phoneNo, String name, String status, String message, String userPic, String time, String userStatus, String personStatus, String lastMessage, String index, String ID, String memberID, String groupID, String groupTime, String title) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.name = name;
        this.status = status;
        this.message = message;
        this.userPic = userPic;
        this.time = time;
        this.userStatus = userStatus;
        this.personStatus = personStatus;
        this.lastMessage = lastMessage;
        this.index = index;
        this.ID = ID;
        this.memberID = memberID;
        this.groupID = groupID;
        this.groupTime = groupTime;
        this.title = title;
    }

//    public Users(String index, String ID, String memberID, String groupID, String groupTime, String title , String index2) {
//        this.index = index;
//        this.ID = ID;
//        this.memberID = memberID;
//        this.groupID = groupID;
//        this.groupTime = groupTime;
//        this.title = title;
//    }

    public Users(String ID, String memberID, String groupID, String groupTime, String name, String groupLastMsg , String groupIcon) {
        this.ID = ID;
        this.memberID = memberID;
        this.groupID = groupID;
        this.groupTime = groupTime;
        this.name = name;
        this.groupLastMsg = groupLastMsg;
        this.groupIcon = groupIcon;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public String getPersonStatus() {
        return personStatus;
    }

    public void setPersonStatus(String personStatus) {
        this.personStatus = personStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getStatus() {
        return status;
    }

    public String getUserStatus() {
        return userStatus;
    }



    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
