package com.UniqueBulleteSolutions.whatsapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.health.PackageHealthStats;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;


import com.UniqueBulleteSolutions.whatsapp.databinding.ActivitySettingBinding;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;


import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    ProgressDialog progressDialog;

    Bitmap bitmap;
    String encodedImage;
    public static final String UPLOAD_URL = "http://192.168.0.105/ApiAuthentication/updateProfile.php";
    private static final int STORAGE_PERMISSION_CODE = 4655;

    Uri sFile;
    ApiInterface apiInterface;

    String CUID = "";
    String CUN = "";
    String CUP = "";
    String CUI = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar();
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateProfileData();




        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setTitle("Updating profile");

        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        Toast.makeText(this, "ID :" + CUID, Toast.LENGTH_SHORT).show();



        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(SettingActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), 33);
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
        });


//        binding.backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
////                startActivity(intent);
//                finish();
//            }
//        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //     uploadtodb();
                progressDialog.show();
                callApi();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void callApi() {
        apiInterface.updateSetting(CUID, binding.etUserName.getText().toString(),
                binding.etAbout.getText().toString(), encodedImage).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            saveData(response.body().getUserName() , response.body().getUserAbout() , response.body().getUserPic());

                        } else {
                            Toast.makeText(SettingActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("exp", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("failure", t.getLocalizedMessage());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 33 && data != null && data.getData() != null) {
            sFile = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(sFile);
                bitmap = BitmapFactory.decodeStream(inputStream);
                // bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sFile);
                binding.userProfileImage.setImageBitmap(bitmap);
                imageStore(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            SharedPreferences sp = getSharedPreferences("UserCredentials", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            // editor.remove("username");
            // editor.remove("password");
            editor.remove("currentuid");
            editor.remove("currentuphn");
            // editor.remove("currentupic");
            editor.commit();
            editor.apply();
            startActivity(new Intent(SettingActivity.this, PhoneNumberActivity.class));
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveData(String name , String about , String path){
        SharedPreferences sp = getSharedPreferences("UserCredentials" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("CUPN" , name);
        editor.putString("CUPA" , about);
        editor.putString("CUPI" , path);
        editor.commit();
        editor.apply();
        updateProfileData();
    }

    private void updateProfileData(){
        SharedPreferences sp = getSharedPreferences("UserCredentials" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String name , about , pic ;

            CUID = sp.getString("CUID" , "0");
            CUP = sp.getString("CUP" , "0");
            name = sp.getString("CUPN" , "user name");
            about = sp.getString("CUPA" , "About");
            pic = sp.getString("CUPI" , "avatar.png");



        String path = ApiClient.BASE_URL+"ApiAuthentication/profileImages/" + pic;
        Picasso.get().load(path).
                placeholder(R.drawable.avatar).into(binding.userProfileImage);

        binding.etUserName.setText(name);
        binding.etAbout.setText(about);

        }
    }
