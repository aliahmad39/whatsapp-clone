package com.UniqueBulleteSolutions.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Adapter.ChatAdapter;
import com.UniqueBulleteSolutions.whatsapp.Adapter.GroupMessagesAdapter;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.Models.MessageModel;
import com.UniqueBulleteSolutions.whatsapp.Models.groupMessages;


import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityGroupChatBinding;
import com.UniqueBulleteSolutions.whatsapp.dialog.DialogPreviewSendImage;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.Thread.sleep;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    private String groupId;
    List<groupMessages> list = new ArrayList<>();
    List<groupMessages> logList = new ArrayList<>();
    private ArrayList<groupMessages> data ;
    private GroupMessagesAdapter chatAdapter;

    ApiInterface apiInterface;



    private static final int REQUEST_CODE_PERMISSION = 332;
    private static final int SELECT_VIDEO = 3;
    MediaPlayer player;
    private static DecimalFormat df2 = new DecimalFormat("#.##");


    Bitmap bitmap;
    String encodedImage;
    Uri sFile;
    String videoPath;


    String senderId, receiverId;
    String userName, userStatus;
    String groupIcon;
    ProgressDialog dialog;

    private static final int CAMERA_REQUEST_CODE = 11;
    private static final int SELECT_FILE = 23;
    private static final int SELECT_AUDIO = 43;
    private String size;

    FloatingActionButton fab;


    private String audio_path;


    private AudioRecorder audioRecorder;
    private File recordFile;
    int x =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        groupId = getIntent().getStringExtra("groupId");
        senderId = MainActivity.getCUID();
        userName =getIntent().getStringExtra("groupTitle");
        groupIcon =getIntent().getStringExtra("currentUPic");




        String path = ApiClient.BASE_URL + "ApiAuthentication/groupImages/" + groupIcon;
        Picasso.get().load(path).placeholder(R.drawable.avatar).into(binding.gcProfileImage);

        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        getMessages();
        binding.gcUserName.setText(userName);

        fab = binding.gcFabSend;

       loadData();

        chatAdapter = new GroupMessagesAdapter(data, this, senderId, binding.gcImagelayout, binding.gcUserChatImage);

        //  binding.chatdetailRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.gcChatdetailRecyclerView.setLayoutManager(layoutManager);
        binding.gcChatdetailRecyclerView.setAdapter(chatAdapter);


        new Thread(new Runnable() {
            @Override
            public void run() {
                //   getMessages();
                while (x >= 0){
                    try {
                        sleep(30000);
                        getMessages();
                        Log.d("timer","timer call");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();



    //    getDeleteLog();

        binding.gcLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.gcUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this , GroupInfoActivity.class);
                intent.putExtra("groupID" , groupId);
                intent.putExtra("groupTitle" , userName);
                intent.putExtra("groupIcon" , groupIcon);
                startActivity(intent);
            }
        });

        binding.gcEtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.gcEtMessage.getText().toString())) {
                    fab.setVisibility(View.INVISIBLE);
                    binding.gcRecordButton.setVisibility(View.VISIBLE);
                    binding.gcCamera.setVisibility(View.VISIBLE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                    binding.gcRecordButton.setVisibility(View.INVISIBLE);
                    binding.gcCamera.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.gcAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.gcMultipleItems.getVisibility() == View.GONE) {
                    binding.gcMultipleItems.setVisibility(View.VISIBLE);
                } else {

                    binding.gcMultipleItems.setVisibility(View.GONE);
                }
            }
        });


       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.gcEtMessage.getText().toString())) {
                    String message = binding.gcEtMessage.getText().toString();
                    sendMessage(message);

                    binding.gcEtMessage.setText("");
                    Toast.makeText(GroupChatActivity.this, "send click", Toast.LENGTH_SHORT).show();
                    binding.gcCamera.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(GroupChatActivity.this, "send recording", Toast.LENGTH_SHORT).show();
                }

            }
        });


        binding.gcCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupChatActivity.this, "camera clicked", Toast.LENGTH_SHORT).show();
                openCamera();
            }
        });


        audioRecorder = new AudioRecorder();

        RecordView recordView = (RecordView) findViewById(R.id.gc_record_view);
        final RecordButton recordButton = (RecordButton) findViewById(R.id.gc_record_button);

        //IMPORTANT
        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(true);

        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupChatActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                Log.d("RecordButton", "RECORD BUTTON CLICKED");
            }
        });


        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);


//        recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);

//
//        recordView.setSlideToCancelText("Slide To Cancel");
//
//
//        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if (!checkPermissionFromDevice()) {
                    binding.gcEtMessage.setVisibility(View.INVISIBLE);
                    binding.gcEmoji.setVisibility(View.INVISIBLE);
                    binding.gcCamera.setVisibility(View.INVISIBLE);
                    binding.gcAttachment.setVisibility(View.INVISIBLE);

                    recordFile = new File(getFilesDir(), UUID.randomUUID().toString() + ".mp4");

                    try {
                        audioRecorder.start(recordFile.getPath());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("RecordView", "onStart");
                    Toast.makeText(GroupChatActivity.this, "OnStartRecord", Toast.LENGTH_SHORT).show();

                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(100);
                    }
                } else {
                    requestPermission();
                }
            }
            @Override
            public void onCancel() {
                try {
                    stopRecording(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(GroupChatActivity.this, "onCancel", Toast.LENGTH_SHORT).show();

                    Log.d("RecordView", "onCancel");
                }


            }

            @Override
            public void onFinish(long recordTime) {
                binding.gcEtMessage.setVisibility(View.VISIBLE);
                binding.gcEmoji.setVisibility(View.VISIBLE);
                binding.gcCamera.setVisibility(View.VISIBLE);
                binding.gcAttachment.setVisibility(View.VISIBLE);
                stopRecording(false);


                String time = getHumanTimeText(recordTime);

                sendVoice();
            }

            @Override
            public void onLessThanSecond() {
                binding.gcEtMessage.setVisibility(View.VISIBLE);
                binding.gcEmoji.setVisibility(View.VISIBLE);
                binding.gcCamera.setVisibility(View.VISIBLE);
                binding.gcAttachment.setVisibility(View.VISIBLE);
                stopRecording(true);

                Toast.makeText(GroupChatActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });


        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.gcEtMessage.setVisibility(View.VISIBLE);
                binding.gcEmoji.setVisibility(View.VISIBLE);
                binding.gcCamera.setVisibility(View.VISIBLE);
                binding.gcAttachment.setVisibility(View.VISIBLE);
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getDeleteLog();
//            }
//        },0);



        binding.getRoot();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 48) {
            if (data != null) {
                if (data.getData() != null) {
                    sFile = data.getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(sFile);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        // bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sFile);

                        new DialogPreviewSendImage(GroupChatActivity.this, bitmap).show(new DialogPreviewSendImage.OnCallBacks() {
                            @Override
                            public void onButtonSendClick() {
                                imageStore(bitmap);
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            sFile = data.getData();


            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
            try {
                bitmap = (Bitmap) data.getExtras().get("data");


                new DialogPreviewSendImage(GroupChatActivity.this, bitmap).show(new DialogPreviewSendImage.OnCallBacks() {
                    @Override
                    public void onButtonSendClick() {
                        imageStore(bitmap);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK) {
            sFile = data.getData();
            try {

                InputStream iStream = null;
                iStream = getContentResolver().openInputStream(sFile);
                final byte[] inputData = getBytes(iStream);

                videoPath = android.util.Base64.encodeToString(inputData, android.util.Base64.DEFAULT);

                uploadVideo("video", videoPath);
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            sFile = data.getData();

            // it will return extension of any selected file
            String extension = getMimeType(this, sFile);

            //  String imagePath = getPath(sFile);
            //    File file = new File(imagePath);

//it will return type of selected media e.g video , audio , msword , pdf etc
            String mimeType = getContentResolver().getType(sFile);

            /*
             * Get the file's content URI from the incoming Intent,
             * then query the server app to get the file's display name
             * and size.
             */

            Cursor returnCursor =
                    getContentResolver().query(sFile, null, null, null, null);
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();

            String name = returnCursor.getString(nameIndex);
            double input = returnCursor.getLong(sizeIndex);

            if (input >= 1000 && input < 1000000) {
                double ans = input / 1000;
                size = df2.format(ans) + "kb";

            } else if (input >= 1000000 && input < 1000000000) {
                double ans = input / 1000000;
                size = df2.format(ans) + "Mb";
            } else if (input >= 1000000000) {
                double ans = input / 1000000000;
                size = df2.format(ans) + "Gb";
            }


            Toast.makeText(this, "type :" + mimeType, Toast.LENGTH_SHORT).show();
            try {

                InputStream iStream = null;
                iStream = getContentResolver().openInputStream(sFile);
                final byte[] inputData = getBytes(iStream);

                videoPath = android.util.Base64.encodeToString(inputData, android.util.Base64.DEFAULT);


                uploadDocument("document", videoPath, extension, size, name);

            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == SELECT_AUDIO && resultCode == RESULT_OK) {
            sFile = data.getData();
            //  String fileName = getMimeType(this ,sFile);

            //Toast.makeText(this, "ext :"+fileName, Toast.LENGTH_SHORT).show();
            try {

                InputStream iStream = null;
                iStream = getContentResolver().openInputStream(sFile);
                final byte[] inputData = getBytes(iStream);

                videoPath = android.util.Base64.encodeToString(inputData, android.util.Base64.DEFAULT);

                //  uploadDocument("document" , videoPath , fileName);
                uploadVideo("audio", videoPath);

            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }


    @Override
    public void onBackPressed() {
        if (binding.gcMultipleItems.getVisibility() == View.VISIBLE) {
            binding.gcMultipleItems.setVisibility(View.GONE);

        } else {
            super.onBackPressed();
        }
    }

    private void sendMessage(String msg) {
        apiInterface.sendGroupMessage(senderId , groupId, msg).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            getMessages();
                            Toast.makeText(GroupChatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(GroupChatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("exp", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("failure", t.getLocalizedMessage());
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }



    private void uploadDocument(String message, String path, String extension, String size, String fileName) {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Are you sure you want to send this Media File to " + userName + "?")
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apiInterface.sendGroupDocument(
                                senderId, groupId, message, extension, size, fileName, path
                        ).
                                enqueue(new Callback<UserResponse>() {
                                    @Override
                                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                        if (response.body().getStatus().equals("1")) {
                                            getMessages();
                                            dialog.dismiss();

                                            Toast.makeText(GroupChatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserResponse> call, Throwable t) {
                                        Log.v("videoError", t.getLocalizedMessage());
                                        Toast.makeText(GroupChatActivity.this, "video error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return null;
        }


    }

    private void uploadVideo(String message, String path) {

        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Are you sure you want to send this Media File to " + userName + "?")
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apiInterface.sendGroupVideo(
                                senderId, groupId, message, path
                        ).
                                enqueue(new Callback<UserResponse>() {
                                    @Override
                                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                        if (response.body().getStatus().equals("1")) {
                                            getMessages();
                                            dialog.dismiss();
                                            Toast.makeText(GroupChatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserResponse> call, Throwable t) {
                                        Log.v("videoError", t.getLocalizedMessage());
                                        Toast.makeText(GroupChatActivity.this, "video error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.video:
//                Toast.makeText(this, "video", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.call:
//                Toast.makeText(this, "call", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.groups:
//                Toast.makeText(this, "groups", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.setting:
//                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.invite:
//                Toast.makeText(this, "invite", Toast.LENGTH_SHORT).show();
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Are you sure you want to send this Media File to " + userName + "?")
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callApis(senderId);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void callApis(String senderId) {
        apiInterface.sendGroupFile(senderId, groupId,  "photo", encodedImage).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            getMessages();
                            Toast.makeText(GroupChatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(GroupChatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("exp", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("failure", t.getLocalizedMessage());
            }
        });
    }

    public void sendMedia(View view) {
        switch (view.getId()) {
            case R.id.layoutcamera:
                openCamera();
                binding.gcMultipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "camera", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutaudio:
                openAudio();
                binding.gcMultipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "audio", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutcontact:
                binding.gcMultipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "contact", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutDocument:
                openDocument();
                binding.gcMultipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "document", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutgallery:
                openGallery();
                binding.gcMultipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "gallery", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutlocation:
                binding.gcMultipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "location", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openAudio() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a audio "), SELECT_AUDIO);
    }

    private void openDocument() {
        Dexter.withContext(getApplicationContext())
                .withPermission(WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //  Intent intent = new Intent(Intent.ACTION_PICK ,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        Intent intent = new Intent();
                        intent.setType("application/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Choose a file "), SELECT_FILE);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    private void openGallery() {

        Dexter.withContext(getApplicationContext())
                .withPermission(WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //  Intent intent = new Intent(Intent.ACTION_PICK ,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    private void openCamera() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //  Intent intent = new Intent(Intent.ACTION_PICK ,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                        startActivityForResult(intent, CAMERA_REQUEST_CODE);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int record_media_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_DENIED || record_media_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO

        }, REQUEST_CODE_PERMISSION);
    }


    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }


    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }


    private void sendVoice() {

        Uri uriAudio = Uri.fromFile(new File(recordFile.getPath()));
        Toast.makeText(this, "Uri" + uriAudio, Toast.LENGTH_SHORT).show();
        try {

            InputStream iStream = null;
            iStream = getContentResolver().openInputStream(uriAudio);
            final byte[] inputData = getBytes(iStream);

            videoPath = android.util.Base64.encodeToString(inputData, android.util.Base64.DEFAULT);

            uploadVideo("audio", videoPath);
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }


    private String getRealPathFromUri(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private void getMessages() {
        apiInterface.getGroupMessages(groupId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response)
            {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            list.clear();
                            data.clear();

                            list = response.body().getGroupMessagelist();

                            data.addAll(list);

                            saveData();
                          //  chatAdapter.notifyDataSetChanged();

                        }
                        else {
                           // chatAdapter.notifyDataSetChanged();
                            //Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // chatAdapter.updateList(list);


                        //  Toast.makeText(ChatDetailActivity.this, "size :" + data.size(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Toast.makeText(ChatDetailActivity.this, "Exp :" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
            //    chatAdapter.notifyDataSetChanged();
            }
        });
    }
//    private void getMessages() {
//        apiInterface.getGroupMessages(groupId).enqueue(new Callback<UserResponse>() {
//            @Override
//            public void onResponse(Call<UserResponse> call, Response<UserResponse> response)
//            {
//                try {
//                    if (response != null) {
//                        if (response.body().getStatus().equals("1")) {
//                            list.clear();
//                            data.clear();
//
//                            list = response.body().getGroupMessagelist();
//
//                            if(!logList.isEmpty()){
//                                for(groupMessages message : logList)  {
//                                    String id1 = message.getMsgID().toString().trim();
//
//                                    for (int i = 0; i < list.size(); i++) {
//                                        groupMessages messages = list.get(i);
//                                        String id2 = messages.getMsgID().toString().trim();
//
//                                        if (id1.equals(id2)) {
//                                            if (message.getSender_id().equals(senderId)) {
//                                                list.remove(i);
//                                            }
//                                        }
//                                    }
//
//                                }
//                            }
//
//                            data.addAll(list);
//                            chatAdapter.notifyDataSetChanged();
//
//                        }
//                        else {
//                            chatAdapter.notifyDataSetChanged();
//                            //Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//
//                        // chatAdapter.updateList(list);
//
//
//                        //  Toast.makeText(ChatDetailActivity.this, "size :" + data.size(), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    // Toast.makeText(ChatDetailActivity.this, "Exp :" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onFailure(Call<UserResponse> call, Throwable t) {
//                chatAdapter.notifyDataSetChanged();
//            }
//        });
//    }
//
//    private void getDeleteLog() {
//        apiInterface.getDeleteLog(groupId).enqueue(new Callback<UserResponse>() {
//            @Override
//            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                try {
//                    if (response != null) {
//                        if (response.body().getStatus().equals("1")) {
//                            logList.clear();
//                            logList = response.body().getDeleteLog();
//                            getMessages();
//                        }else{
//                            getMessages();
//                              Toast.makeText(GroupChatActivity.this, "size +++:", Toast.LENGTH_SHORT).show();
//                        }
//                        // chatAdapter.updateList(list);
//
//
//
//                    } else {
//                        Toast.makeText(GroupChatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    // Toast.makeText(ChatDetailActivity.this, "Exp :" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onFailure(Call<UserResponse> call, Throwable t) {
//            //  getMessages();
//            }
//        });
//    }


    private void saveData(){
        Toast.makeText(this, "group save data", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getSharedPreferences("aligroupApp" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(groupId , json);
        editor.apply();
    }

    private void loadData(){
        Toast.makeText(this, "group load data", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getSharedPreferences("aligroupApp" , Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(groupId , null);
        Type type = new TypeToken<ArrayList<groupMessages>>() {}.getType();

        data = gson.fromJson(json , type);
        //   chatAdapter.notifyDataSetChanged();
        //  binding.chatRecyclerView.hideShimmerAdapter();

        if(data == null){
            data = new ArrayList<>();
        }
    }

}




