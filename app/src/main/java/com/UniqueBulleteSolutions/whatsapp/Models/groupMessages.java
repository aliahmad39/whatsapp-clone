package com.UniqueBulleteSolutions.whatsapp.Models;

public class groupMessages {
    private String msgID;
    private String extension;
    private String fileName;
    private String fileSize;
    private String file_path;
    private String message;
    private String message_time;
    private String sender_id;
    private String receiver_id;
    private String groupID;
    private String id;
    private String name;
    private String phoneNo;
    private String password;
    private String userStatus;
    private String userPic;
    private String time;
    private String groupAdmin;
    private String lastMessage;


    public groupMessages() {
    }

    public groupMessages(String msgID, String extension, String fileName, String fileSize, String file_path, String message, String message_time, String sender_id, String receiver_id, String groupID, String id, String name, String phoneNo, String password, String userStatus, String userPic, String time, String groupAdmin, String lastMessage) {
        this.msgID = msgID;
        this.extension = extension;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.file_path = file_path;
        this.message = message;
        this.message_time = message_time;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.groupID = groupID;
        this.id = id;
        this.name = name;
        this.phoneNo = phoneNo;
        this.password = password;
        this.userStatus = userStatus;
        this.userPic = userPic;
        this.time = time;
        this.groupAdmin = groupAdmin;
        this.lastMessage = lastMessage;
    }


    // Getter Methods

    public String getMsgID() {
        return msgID;
    }

    public String getExtension() {
        return extension;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage_time() {
        return message_time;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public String getGroupID() {
        return groupID;
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

    public String getPassword() {
        return password;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getTime() {
        return time;
    }

    public String getGroupAdmin() {
        return groupAdmin;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    // Setter Methods

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage_time(String message_time) {
        this.message_time = message_time;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setGroupAdmin(String groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
