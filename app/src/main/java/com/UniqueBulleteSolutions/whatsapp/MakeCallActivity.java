package com.UniqueBulleteSolutions.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Adapter.CallAdapter;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;

import com.UniqueBulleteSolutions.whatsapp.Models.Users;


import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityMakeCallBinding;
import com.UniqueBulleteSolutions.whatsapp.fragment.CallFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MakeCallActivity extends AppCompatActivity {
    ActivityMakeCallBinding binding;

    private static ArrayList<Users> data = new ArrayList<>();

    private ArrayList<Users> list;
    private List<Users> listUsers = new ArrayList<>();
    ArrayList<String> number = new ArrayList<>();
    private static ArrayList<Users> contacts = new ArrayList<>();


    ApiInterface apiInterface;
    String CUID = "";
    CallAdapter adapter;
    int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMakeCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
     //getSupportActionBar().setTitle(null);

       // setSupportActionBar(binding.calltoolbar);
        CUID = MainActivity.getCUID();


        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
       // getServerUsers();

//        getContact();

       // index = CallFragment.pos;

       index = getIntent().getIntExtra("index", 0);
        if (index == 1) {
            binding.fabCall.setVisibility(View.VISIBLE);
        } else {
            binding.fabCall.setVisibility(View.GONE);
        }

        loadData();
        adapter = new CallAdapter(list, MakeCallActivity.this, CUID, binding.calltoolbar, index, binding.fabCall, binding.makegroupRecyclerView, binding.view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.makecallRecyclerView.setLayoutManager(layoutManager);
        binding.makecallRecyclerView.setAdapter(adapter);

        binding.leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.tvTotalContacts.setText(list.size() + "");
        // getContact();

        Dexter.withActivity(MakeCallActivity.this)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                       getServerUsers();
                  //      getContact();
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

    private void getContact() {
        Toast.makeText(this, "getContact()", Toast.LENGTH_SHORT).show();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        contacts.clear();
        number.clear();
        String phnno;

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phn = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    //        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

            if(!phn.startsWith("+")){
                phnno = "+92"+phn.substring(1);
            }else{
                phnno = phn;
            }
            if (!number.contains(phnno)) {
                number.add(phnno);
                contacts.add(new Users( phnno, name));
             //   Toast.makeText(this, "with + :"+phn, Toast.LENGTH_SHORT).show();
            }
        }

        list.clear();
        for (Users user1 : contacts) {
            for (Users user2 : data) {
                if (user1.getPhoneNo().equals(user2.getPhoneNo())) {
                    Toast.makeText(this, ""+user2.getId(), Toast.LENGTH_SHORT).show();
                     list.add(new Users(user2.getId(),user1.getPhoneNo() , user1.getName()));


                    break;
                }
            }
        }
      saveData();
    }
    private void getServerUsers() {
        apiInterface.getUsers().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            Toast.makeText(MakeCallActivity.this, "status 1", Toast.LENGTH_SHORT).show();
                            listUsers.clear();
                            data.clear();
                            listUsers = response.body().getData();

//                            this loop is for matching contact with phone directory
                            for(int i =0 ; i< listUsers.size() ; i++){
                                Users users = listUsers.get(i);
                                if(users.getId().equals(CUID)){
                                    // list.remove(i);
                                    // break;
                                }else{
                                    if(!users.getId().isEmpty()){
                                        data.add(users);
                                    }

                                }
                            }

                           getContact();
                        }
                        else
                        {
//                                adapter.notifyDataSetChanged();
//                                binding.chatRecyclerView.hideShimmerAdapter();
                    //        Toast.makeText(getContext(), response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                  //  Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

               Toast.makeText(MakeCallActivity.this, "Error in:" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData(){
        Toast.makeText(this, "save call data", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getSharedPreferences("aliApp" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("phoneDirectory" , json);
        editor.apply();
    }

    private void loadData(){
        Toast.makeText(this, "load call data", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getSharedPreferences("aliApp" , Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("phoneDirectory" , null);
        Type type = new TypeToken<ArrayList<Users>>() {}.getType();

        list = gson.fromJson(json , type);


        if(list == null){
            list = new ArrayList<>();
        }
    }
}