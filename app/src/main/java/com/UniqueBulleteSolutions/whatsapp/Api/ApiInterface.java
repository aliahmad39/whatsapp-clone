package com.UniqueBulleteSolutions.whatsapp.Api;


import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.Models.groupMessages;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("getUsers.php")
    Call<UserResponse> getUsers();

    @GET("getUser.php")
    Call<UserResponse> getUser();

    @GET("showUser.php")
    Call<UserResponse> showUser();




    @GET("getGroupMembers.php")
    Call<UserResponse> getGroups();

    @GET("getStatuses.php")
    Call<UserResponse> getStatuses();

    @GET("getmessages.php")
    Call<UserResponse> getMessages();

    @GET("getmessage.php")
    Call<UserResponse> getMessag();

    @FormUrlEncoded
    @POST("getGroupMessages.php")
    Call<UserResponse> getGroupMessages(
            @Field("ID") String ID
    );

    @FormUrlEncoded
    @POST("getGroupDetail.php")
    Call<UserResponse> getGroupDetail(
            @Field("ID") String ID
    );



    @FormUrlEncoded
    @POST("getDeleteLog.php")
    Call<UserResponse> getDeleteLog(
            @Field("ID") String ID
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<Users> verifyUser(
            @Field("userPhone") String userPhone,
            @Field("userPassword") String userPassword
    );

    @FormUrlEncoded
    @POST("getUserMessages.php")
    Call<UserResponse> getUserMessages(
            @Field("senderID") String senderID
    );

    @FormUrlEncoded
    @POST("status.php")
    Call<UserResponse> createStatus(
            @Field("senderId") String senderId,
            @Field("fileExtension") String fileExtension,
            @Field("upload") String upload
    );

    @FormUrlEncoded
    @POST("updateProfile.php")
    Call<UserResponse> updateSetting(
            @Field("userId") String uid,
            @Field("userName") String uname,
            @Field("userAbout") String uabout,
            @Field("upload") String upload
    );

    @FormUrlEncoded
    @POST("register.php")
    Call<UserResponse> createUser(
            @Field("userID") String userID,
            @Field("userPhoneNo") String userPhoneNo
    );

    @FormUrlEncoded
    @POST("getUserProfile.php")
    Call<UserResponse> userProfile(
            @Field("ID") String ID
    );


    @FormUrlEncoded
    @POST("updateRegister.php")
    Call<UserResponse> updateUser(
            @Field("userName") String userName,
             @Field("ID") String ID

    );

    @FormUrlEncoded
    @POST("createGroup.php")
    Call<UserResponse> createGroup(
            @Field("groupID") String groupID,
            @Field("title") String title,
            @Field("senderID") String senderID,
            @Field("upload") String upload
    );

    @FormUrlEncoded
    @POST("groupMembers.php")
    Call<UserResponse> groupMembers(
            @Field("memberID") String memberID,
            @Field("groupID") String groupID
    );

    @FormUrlEncoded
    @POST("updateProfile.php")
    Call<UserResponse> updateProfile(
            @Field("userName") String userName,
            @Field("userPhone") String userPhone,
            @Field("userImage") String userImage,
            @Field("userId") String userId
    );

    @FormUrlEncoded
    @POST("sendmessage.php")
    Call<UserResponse> sendMessage(
            @Field("senderId") String senderId,
            @Field("receiverId") String receiverId,
            @Field("senderMessage") String senderMessage

    );

    @FormUrlEncoded
    @POST("sendGroupmessages.php")
    Call<UserResponse> sendGroupMessage(
            @Field("senderId") String senderId,
            @Field("groupId") String groupId,
            @Field("senderMessage") String senderMessage
    );

    @FormUrlEncoded
    @POST("deleteGroupPrsnMsg.php")
    Call<UserResponse> deletePrsnMsg(
            @Field("senderId") String senderId,
            @Field("groupId") String groupId,
            @Field("msgID") String senderMessage
    );


    @FormUrlEncoded
    @POST("sendfile.php")
    Call<UserResponse> sendFile(
            @Field("senderId") String senderId,
            @Field("receiverId") String receiverId,
            @Field("senderMessage") String senderMessage ,
            @Field("upload") String upload
    );

    @FormUrlEncoded
    @POST("sendGroupFile.php")
    Call<UserResponse> sendGroupFile(
            @Field("senderId") String senderId,
            @Field("groupId") String groupId,
            @Field("senderMessage") String senderMessage ,
            @Field("upload") String upload
    );

    @FormUrlEncoded
    @POST("sendVideo.php")
    Call<UserResponse> sendvideo(
            @Field("senderId") String senderId,
            @Field("receiverId") String receiverId,
            @Field("senderMessage") String senderMessage,
            @Field("upload") String upload
    );

    @FormUrlEncoded
    @POST("sendGroupVideo.php")
    Call<UserResponse> sendGroupVideo(
            @Field("senderId") String senderId,
            @Field("groupId") String receiverId,
            @Field("senderMessage") String senderMessage,
            @Field("upload") String upload
    );

    @FormUrlEncoded
    @POST("sendDocument.php")
    Call<UserResponse> sendDocument(
            @Field("senderId") String senderId,
            @Field("receiverId") String receiverId,
            @Field("senderMessage") String senderMessage,
            @Field("fileExtension") String fileExtension,
            @Field("fileSize") String fileSize,
            @Field("fileName") String fileName,
            @Field("upload") String upload
    );

    @FormUrlEncoded
    @POST("sendGroupDocument.php")
    Call<UserResponse> sendGroupDocument(
            @Field("senderId") String senderId,
            @Field("groupId") String receiverId,
            @Field("senderMessage") String senderMessage,
            @Field("fileExtension") String fileExtension,
            @Field("fileSize") String fileSize,
            @Field("fileName") String fileName,
            @Field("upload") String upload
    );

//
//    @Multipart
//    @POST("sendVideo.php")
//    Call<UserResponse> sendvideo(
//            @Part("senderId") RequestBody senderId,
//            @Part("receiverId") RequestBody receiverId,
//            @Part("senderMessage") RequestBody senderMessage,
//            @Part MultipartBody.Part photo
//    );





    @FormUrlEncoded
    @POST("deletemessage.php")
    Call<UserResponse> deleteMessages(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("deleteGroupMessage.php")
    Call<UserResponse> deleteGroupMessages(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("setstatus.php")
    Call<UserResponse> setStatus(
            @Field("userStatus") String userStatus,
            @Field("userID") String userID
    );

    @FormUrlEncoded
    @POST("getstatus.php")
    Call<Users> getStatus(
            @Field("userID") String userID
    );


    @GET("search")
    Call<Users> getS(
            @Query("userID") String userID
    );

}
