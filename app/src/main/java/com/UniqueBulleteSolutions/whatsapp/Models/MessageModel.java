package com.UniqueBulleteSolutions.whatsapp.Models;

public class MessageModel {
String id , uid, imageUrl , message , sender_id , receiver_id , message_time , file_path;
String extension , fileSize , fileName ;
int isDownload;
    long timestamp;



    public MessageModel() {
    }

    public MessageModel(String id, String message, String sender_id, String receiver_id, String message_time, String file_path) {
        this.id = id;
        this.message = message;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message_time = message_time;
        this.file_path = file_path;
        isDownload = 0;
    }

    public MessageModel(String uid, String message, long timestamp) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
        isDownload = 0;
    }

    public int getIsDownload() {
        return isDownload;
    }

    public void setIsDownload(int isDownload) {
        this.isDownload = isDownload;
    }

    public String getExtension() {
        return extension;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public String getMessage_time() {
        return message_time;
    }
}
