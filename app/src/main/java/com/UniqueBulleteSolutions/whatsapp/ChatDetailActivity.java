package com.UniqueBulleteSolutions.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.apache.commons.io.FileUtils;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Adapter.ChatAdapter;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.Models.MessageModel;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;

import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityChatDetailBinding;
import com.UniqueBulleteSolutions.whatsapp.dialog.DialogPreviewSendImage;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.Thread.sleep;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    private List<MessageModel> list = new ArrayList<>();
    private ArrayList<MessageModel> data = new ArrayList<>();
    ChatAdapter chatAdapter;
    private static final int REQUEST_CODE_PERMISSION = 332;
    private static final int SELECT_VIDEO = 3;
    MediaPlayer player;
    //    NullPointerException  nullPointer = new NullPointerException();
    private static DecimalFormat df2 = new DecimalFormat("#.##");


    Bitmap bitmap;
    String encodedImage;
    Uri sFile;
    String videoPath;
    private RecordView recordView;
    int x =0;


    FirebaseDatabase database;
    FirebaseAuth auth;
    String senderId, receiverId;
    String userName, userStatus;
    ProgressDialog dialog;
    String senderRoom, receiverRoom;
    String randomkey;
    MessageModel message1;
    StorageReference mstorage;
    private static final int CAMERA_REQUEST_CODE = 11;
    private static final int SELECT_FILE = 23;
    private static final int SELECT_AUDIO = 43;
    private String size;
    Thread thread;

    ApiInterface apiInterface;
    FloatingActionButton fab;

    //    private MediaRecorder mediaRecorder;
    private String audio_path;
    //    private String sTime;
    Handler handler = new Handler();
    Runnable runnable;

    private AudioRecorder audioRecorder;
    private File recordFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
        fab = findViewById(R.id.fabSend);
        // checkPermissionFromDevice();

        setSupportActionBar(binding.toolbar);

        receiverId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        senderId = getIntent().getStringExtra("currentUid");
        userStatus = getIntent().getStringExtra("currentUStatus");
        String path = ApiClient.BASE_URL + "ApiAuthentication/profileImages/" + getIntent().getStringExtra("currentUPic");

        Toast.makeText(this, "CUID :"+senderId, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "RID :"+receiverId, Toast.LENGTH_SHORT).show();

        binding.userName.setText(userName);

        Picasso.get().load(path).placeholder(R.drawable.avatar).into(binding.profileImage);
        loadData();


//        Handler handler = new Handler();
//
//               new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        this.recreate();
//                    }
//                };
//
//            }
//        }).start();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        chatAdapter = new ChatAdapter(data, this, senderId, receiverId, binding.imagelayout, binding.userChatImage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatdetailRecyclerView.setLayoutManager(layoutManager);
        binding.chatdetailRecyclerView.setAdapter(chatAdapter);

//        new Thread()
//        {
//            public void run()
//            {
//                ChatDetailActivity.this.runOnUiThread(new Runnable()
//                {
//                    public void run()
//                    {
//                        while (x >= 0){
//                            try {
//                                sleep(1000);
//                                getMessages();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                        //Do your UI operations like dialog opening or Toast here
//                    }
//                });
//            }
//        }.start();

//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                getMessages();
//            }
//        };
//        handler.post(runnable);
//        thread =new Thread(new Runnable() {
//            @Override
//            public void run() {
//             //   getMessages();
////                while (x >= 0){
////                    try {
////                        sleep(1000);
////                        getMessages();
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////
////                }
////                Toast.makeText(ChatDetailActivity.this, "hello", Toast.LENGTH_SHORT).show();
////                try {
////                    while(true){
////                        getMessages();
////                    }
////
////                } catch (Exception e) {
////
//////                    Toast.makeText(ChatDetailActivity.this, "thread catch run", Toast.LENGTH_SHORT).show();
////                   // thread.start();
////                }
//
//            }
//        });

//thread.start();


//         getMessages();

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                getMessages();
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        getMessages();
////                      //      Toast.makeText(ChatDetailActivity.this, "timer called", Toast.LENGTH_SHORT).show();
////
////                    }
////                });
//
//            }
//        }, 0, 1000);



        //  loadData();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (x >= 0) {
                    getMessages();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            if(!binding.etMessage.getText().toString().isEmpty()){
//                                binding.etMessage.requestFocus();
//                                // binding.etMessage.clearFocus();
//                                //     Toast.makeText(ChatDetailActivity.this, "empty", Toast.LENGTH_SHORT).show();
//                            }
                         //   binding.etMessage.requestFocus();
                            apiInterface.getStatus(receiverId).enqueue(new Callback<Users>() {
                                @Override
                                public void onResponse(Call<Users> call, Response<Users> response) {
                                    if (response != null) {
                                        if (response.body().getStatus().equals("1")) {
                                            if (response.body().getUserStatus() != null) {
                                                if (response.body().getUserStatus().equals("offline")) {
                                                    binding.tvTyping.setVisibility(View.GONE);
                                                } else {
                                                    binding.tvTyping.setText(response.body().getUserStatus().toString());
                                                    binding.tvTyping.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<Users> call, Throwable t) {

                                }
                            });

                        }
                    });

                    try {
                        sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();



        binding.leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;


        Handler handler1 = new Handler();
        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.etMessage.requestFocus();
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.etMessage.requestFocus();
                if (TextUtils.isEmpty(binding.etMessage.getText().toString())) {
                    fab.setVisibility(View.INVISIBLE);
                    binding.recordButton.setVisibility(View.VISIBLE);
                    binding.camera.setVisibility(View.VISIBLE);

                } else {
                    fab.setVisibility(View.VISIBLE);
                    binding.recordButton.setVisibility(View.INVISIBLE);
                    binding.camera.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.etMessage.requestFocus();
                apiInterface.setStatus("typing...", senderId).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response != null) {
                            if (response.body().getStatus().equals("1")) {
                                Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(ChatDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                // handler.removeCallbacksAndMessages("null");
                handler1.postDelayed(userStoppedTyping, 1000);
            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {

                    apiInterface.setStatus("online", senderId).enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response != null) {
                                if (response.body().getStatus().equals("1")) {
                                    Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Toast.makeText(ChatDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.multipleItems.getVisibility() == View.GONE) {
                    binding.multipleItems.setVisibility(View.VISIBLE);
                } else {

                    binding.multipleItems.setVisibility(View.GONE);
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.etMessage.getText().toString())) {
                    String message = binding.etMessage.getText().toString();
                    callApi(senderId, receiverId, message);

                    binding.etMessage.setText("");
                    Toast.makeText(ChatDetailActivity.this, "send click", Toast.LENGTH_SHORT).show();
                    binding.camera.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ChatDetailActivity.this, "send recording", Toast.LENGTH_SHORT).show();
                }

            }
        });


        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatDetailActivity.this, "camera clicked", Toast.LENGTH_SHORT).show();
                openCamera();
            }
        });


        audioRecorder = new AudioRecorder();

        recordView = (RecordView) findViewById(R.id.record_view);
        final RecordButton recordButton = (RecordButton) findViewById(R.id.record_button);

        //IMPORTANT
        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(true);

        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermissionFromDevice()){
                    startRecording();
                }else{
                    requestPermission();
                }
                Toast.makeText(ChatDetailActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
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

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.etMessage.setVisibility(View.VISIBLE);
                binding.emoji.setVisibility(View.VISIBLE);
                binding.camera.setVisibility(View.VISIBLE);
                binding.attachment.setVisibility(View.VISIBLE);
                Log.d("RecordView", "Basket Animation Finished");
            }
        });


    }


    private void callApi(String sid, String rid, String msg) {
        apiInterface.sendMessage(sid, rid, msg).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            getMessages();
                            Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

                        new DialogPreviewSendImage(ChatDetailActivity.this, bitmap).show(new DialogPreviewSendImage.OnCallBacks() {
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


                new DialogPreviewSendImage(ChatDetailActivity.this, bitmap).show(new DialogPreviewSendImage.OnCallBacks() {
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

    private void uploadDocument(String message, String path, String extension, String size, String fileName) {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Are you sure you want to send this Media File to " + userName + "?")
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apiInterface.sendDocument(
                                senderId, receiverId, message, extension, size, fileName, path
                        ).
                                enqueue(new Callback<UserResponse>() {
                                    @Override
                                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                        if (response.body().getStatus().equals("1")) {
                                            getMessages();
                                            dialog.dismiss();

                                            Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserResponse> call, Throwable t) {
                                        Log.v("videoError", t.getLocalizedMessage());
                                        Toast.makeText(ChatDetailActivity.this, "video error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void startRecording(){
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                binding.etMessage.setVisibility(View.INVISIBLE);
                binding.emoji.setVisibility(View.INVISIBLE);
                binding.camera.setVisibility(View.INVISIBLE);
                binding.attachment.setVisibility(View.INVISIBLE);

                recordFile = new File(getFilesDir(), UUID.randomUUID().toString() + ".opus");

                try {
                    audioRecorder.start(recordFile.getPath());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("RecordView", "onStart");
                Toast.makeText(ChatDetailActivity.this, "OnStartRecord", Toast.LENGTH_SHORT).show();

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(100);
                }
            }
            @Override
            public void onCancel() {
                try {
                    stopRecording(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ChatDetailActivity.this, "onCancel", Toast.LENGTH_SHORT).show();

                    Log.d("RecordView", "onCancel");
                }


            }

            @Override
            public void onFinish(long recordTime) {
                binding.etMessage.setVisibility(View.VISIBLE);
                binding.emoji.setVisibility(View.VISIBLE);
                binding.camera.setVisibility(View.VISIBLE);
                binding.attachment.setVisibility(View.VISIBLE);
                stopRecording(false);


                String time = getHumanTimeText(recordTime);

                sendVoice();
            }

            @Override
            public void onLessThanSecond() {
                binding.etMessage.setVisibility(View.VISIBLE);
                binding.emoji.setVisibility(View.VISIBLE);
                binding.camera.setVisibility(View.VISIBLE);
                binding.attachment.setVisibility(View.VISIBLE);
                stopRecording(true);

                Toast.makeText(ChatDetailActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });
    }

    private void uploadVideo(String message, String path) {

        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Are you sure you want to send this Media File to " + userName + "?")
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apiInterface.sendvideo(
                                senderId, receiverId, message, path
                        ).
                                enqueue(new Callback<UserResponse>() {
                                    @Override
                                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                        if (response.body().getStatus().equals("1")) {
                                            getMessages();
                                            dialog.dismiss();
                                            Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserResponse> call, Throwable t) {
                                        Log.v("videoError", t.getLocalizedMessage());
                                        Toast.makeText(ChatDetailActivity.this, "video error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();

        apiInterface.setStatus("online", senderId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response != null) {
                    if (response.body().getStatus().equals("1")) {
                        //              Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                //    Toast.makeText(ChatDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {

        apiInterface.setStatus("offline", senderId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response != null) {
                    if (response.body().getStatus().equals("1")) {
                        //   Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                //     Toast.makeText(ChatDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        super.onPause();
        //  player.pause();
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
                        callApis();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void callApis() {
        apiInterface.sendFile(senderId, receiverId, "photo", encodedImage).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            getMessages();
                            Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                binding.multipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "camera", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutaudio:
                openAudio();
                binding.multipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "audio", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutcontact:
                binding.multipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "contact", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutDocument:
                openDocument();
                binding.multipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "document", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutgallery:
                openGallery();
                binding.multipleItems.setVisibility(View.GONE);
                Toast.makeText(this, "gallery", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutlocation:
                binding.multipleItems.setVisibility(View.GONE);
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
                        intent.setType("*/*");
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

    @NonNull
    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MultipartBody.FORM, value);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File myfile = new File(fileUri.getEncodedPath());
        try {
            FileUtils.touch(myfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        myfile

                );

        return MultipartBody.Part.createFormData(partName, myfile.getName(), requestFile);
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
        //   x++;
//        Toast.makeText(this, "message calles "+x, Toast.LENGTH_SHORT).show();
        apiInterface.getMessag().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            list.clear();
                            data.clear();
                            list = response.body().getMessagelist();
                            //   Toast.makeText(ChatDetailActivity.this, "size b:" + list.size(), Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < list.size(); i++) {
                                MessageModel model = list.get(i);
                                if (model.getSender_id().equals(senderId) && model.getReceiver_id().equals(receiverId)) {
                                    data.add(model);
                                    //  chatAdapter.updateList(data);
                                    chatAdapter.notifyDataSetChanged();

                                } else if (model.getReceiver_id().equals(senderId) && model.getSender_id().equals(receiverId)) {
                                    data.add(model);
                                    //  chatAdapter.updateList(data);
                                    chatAdapter.notifyDataSetChanged();
                                } else {
                                    //  list.remove(i);
                                }

                            }
                            saveData();
                            // chatAdapter.updateList(list);

                            //  Toast.makeText(ChatDetailActivity.this, "size :" + data.size(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //   chatAdapter.notifyDataSetChanged();
                            Toast.makeText(ChatDetailActivity.this, "in chat else", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(ChatDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    // Toast.makeText(ChatDetailActivity.this, "Exp :" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // data.addAll(data);
                loadData();
                //   Toast.makeText(ChatDetailActivity.this, "get messages failure called", Toast.LENGTH_SHORT).show();
                chatAdapter.notifyDataSetChanged();
                //  getMessages();
                //   handler.postDelayed(runnable,1000);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                // handler.post(runnable);


//thread.suspend();
                //              thread.stop();
                //            thread.start();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (binding.multipleItems.getVisibility() == View.VISIBLE) {
            binding.multipleItems.setVisibility(View.GONE);
        } else if(binding.etMessage.getText().toString().isEmpty()) {
          //  binding.etMessage.clearFocus();
        }else{
            startActivity(new Intent(this ,MainActivity.class));
            finishAffinity();
            super.onBackPressed();
        }
    }

    private void saveData(){
        //   Toast.makeText(this, "save data", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getSharedPreferences("aliApp" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(receiverId , json);
        editor.apply();
        editor.commit();
        //    handler.postDelayed(runnable,1000);
    }

    private void loadData(){

        //   Toast.makeText(this, "load data", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getSharedPreferences("aliApp" , Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(receiverId , null);
        Type type = new TypeToken<ArrayList<MessageModel>>() {}.getType();

        data = gson.fromJson(json , type);

        //   chatAdapter.notifyDataSetChanged();
        //  binding.chatRecyclerView.hideShimmerAdapter();

//        if(data == null){
//            data = new ArrayList<>();
//        }
    }

}

