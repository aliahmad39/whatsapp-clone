package com.UniqueBulleteSolutions.whatsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Adapter.CallAdapter;
import com.UniqueBulleteSolutions.whatsapp.Adapter.GroupUserAdapter;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityGroupChatBinding;
import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityMakeGroupBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MakeGroupActivity extends AppCompatActivity {
    ArrayList<Users> list = new ArrayList<>();
    ArrayList<Users> list2 = new ArrayList<>();
    ActivityMakeGroupBinding binding;
    GroupUserAdapter adapter;
    ProgressDialog progressDialog;
    private String sid;
    Uri sFile;
    Bitmap bitmap;
    String encodedImage;


    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMakeGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        list.clear();

        list.addAll(CallAdapter.getSelectList());
        list2.addAll(CallAdapter.getSelectList());
        sid = MainActivity.getCUID();
        Users user = new Users();
        user.setId(sid);
        list2.add(user);


        Toast.makeText(this, "sid :"+sid, Toast.LENGTH_SHORT).show();

        binding.rvGroup.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new GroupUserAdapter(list, this);

        binding.rvGroup.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.floatingActionButton.setVisibility(View.VISIBLE);

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MakeGroupActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        binding.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MakeGroupActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


        binding.fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etGroupTitle.getText().toString().isEmpty()) {
                    binding.etGroupTitle.setError("group title...");
                    Toast.makeText(MakeGroupActivity.this, "please enter the group title", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    createGroup(binding.etGroupTitle.getText().toString().trim());
                }
            }
        });

    }

    private void createGroup(String title) {
     //   String receiversId = String.valueOf(SystemClock.currentThreadTimeMillis());
        String groupId = String.valueOf(SystemClock.currentThreadTimeMillis());
        Toast.makeText(this, "admin Id ;"+sid, Toast.LENGTH_SHORT).show();
        apiInterface.createGroup(groupId , title, sid , encodedImage ).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                progressDialog.dismiss();
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                           Toast.makeText(MakeGroupActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                           for(Users users :list2){
                              apiInterface.groupMembers(users.getId() , groupId).enqueue(new Callback<UserResponse>() {
                                  @Override
                                  public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                                      if(response.body().getStatus().equals("1")){
//                                          Toast.makeText(MakeGroupActivity.this, "memeber add successfully", Toast.LENGTH_SHORT).show();
//                                      }
                                  }

                                  @Override
                                  public void onFailure(Call<UserResponse> call, Throwable t) {

                                  }
                              });
                           }
                            Toast.makeText(MakeGroupActivity.this, "group created successfully...", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(MakeGroupActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

    public void finishActivity(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null){
            binding.floatingActionButton.setVisibility(View.GONE);
            sFile = data.getData();
           // binding.userProfileImage.setImageURI(sFile);
            try {
                InputStream inputStream = getContentResolver().openInputStream(sFile);
                bitmap = BitmapFactory.decodeStream(inputStream);
                // bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sFile);
                binding.userProfileImage.setImageBitmap(bitmap);
                imageStore(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            binding.floatingActionButton.setVisibility(View.VISIBLE);
        }

    }

    private void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

    }
}