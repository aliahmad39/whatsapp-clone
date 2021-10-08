package com.UniqueBulleteSolutions.whatsapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Adapter.ChatAdapter;
import com.UniqueBulleteSolutions.whatsapp.Adapter.UserAdapter;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.MainActivity;
import com.UniqueBulleteSolutions.whatsapp.MakeCallActivity;
import com.UniqueBulleteSolutions.whatsapp.Models.ChatUsers;
import com.UniqueBulleteSolutions.whatsapp.Models.MessageModel;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.Models.setClass;
import com.UniqueBulleteSolutions.whatsapp.R;
import com.UniqueBulleteSolutions.whatsapp.SettingActivity;


import com.UniqueBulleteSolutions.whatsapp.databinding.FragmentChatBinding;
import com.UniqueBulleteSolutions.whatsapp.utils.Permissions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    FragmentChatBinding binding;
    List<Users> list = new ArrayList<>();
   // List<MessageModel> messageList = new ArrayList<>();
    List<MessageModel> messageList = new ArrayList<>();
    List<String> listid = new ArrayList<>();
    List<Users> groupList = new ArrayList<>();
    private  ArrayList<Users> data = new ArrayList<>();
    private static ArrayList<Users> data2 = new ArrayList<>();
    private static ArrayList<Users> data1 = new ArrayList<>();
    FirebaseDatabase database;
    private String currentId;
    ApiInterface apiInterface;
    String CUID = "";
    UserAdapter adapter;
    boolean isGroupCall = false;
    private int pos = 0;
    private  ArrayList<Users> phoneList;
    private  ArrayList<String> ids = new ArrayList<>();
    private  ArrayList<setClass>  setclass = new ArrayList<setClass>();
    HashSet<String> set = new HashSet<>();

    ArrayList<String> number = new ArrayList<>();
    private static ArrayList<Users> contacts = new ArrayList<>();




    public static ArrayList<Users> getData() {
        return data1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);

          changeFabicon();

          Permissions permissions = new Permissions();
          if(permissions.ContactPermissions(getContext())){
              getContact();
          }

        CUID = MainActivity.getCUID();
        MainActivity.pos = 0;


        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);


        getServerUsersMessages();

        loadData();
        adapter = new UserAdapter(data, getContext(), CUID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        binding.chatRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        apiInterface.setStatus("online", CUID).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response != null) {
                    if (response.body().getStatus().equals("1")) {

                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onPause() {

        apiInterface.setStatus("offline", CUID).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response != null) {
                    if (response.body().getStatus().equals("1")) {

                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

            inflater.inflate(R.menu.chat_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        Toast.makeText(getContext(), newText, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                Toast.makeText(getContext(), "chat search", Toast.LENGTH_SHORT).show();
                break;

            case R.id.new_group:
                startActivity(new Intent(getContext(), MakeCallActivity.class).putExtra("index", 1));
                Toast.makeText(getContext(), "chat new_group", Toast.LENGTH_SHORT).show();
                break;
            case R.id.new_broadcast:
                Toast.makeText(getContext(), "chat new_broadcastsearch", Toast.LENGTH_SHORT).show();
                break;
            case R.id.linked_device:
                Toast.makeText(getContext(), "chat linked_device", Toast.LENGTH_SHORT).show();
                break;
            case R.id.chat_setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                Toast.makeText(getContext(), "chat chat_setting", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getServerUsersMessages() {
        apiInterface.getUserMessages(CUID).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {

                            data.clear();
                            messageList = response.body().getMessagelist();
                            groupList = response.body().getGroupList();
                            list =  response.body().getShowUser();



                            for (MessageModel chat : messageList) {
                                if (!chat.getSender_id().equals(CUID)) {
                                  set.add(chat.getSender_id());
                                } else if (!chat.getReceiver_id().equals(CUID)) {
                                   set.add(chat.getReceiver_id());
                                }
                            }
                            for(String id : set){
                                for(Users users :list){
                                    if(users.getId().equals(id)) {
                                        users.setName(users.getPhoneNo());
                                        if (number.contains(users.getPhoneNo())) {
                                            for (Users users1 : contacts) {
                                                if (users.getPhoneNo().equals(users1.getPhoneNo())) {
                                                    users.setName(users1.getName());
                                                  break;
                                                }
                                            }
                                        }
                                        users.setIndex("user");
                                        data.add(users);
                                        break;
                                    }
                                }
                            }

                            for (Users users : groupList) {
                                if (users.getMemberID().equals(CUID)) {
                                    // user1.setIndex("group");
                                    Toast.makeText(getContext(), "in group", Toast.LENGTH_SHORT).show();
                                    users.setIndex("group");
                                    data.add(users);
                                    binding.chatRecyclerView.hideShimmerAdapter();
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            saveData();

                        }else{
                            Toast.makeText(getContext(), response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // getUsers();
                Toast.makeText(getContext(), "Error :" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void saveData(){
        SharedPreferences sp = getContext().getSharedPreferences("aliApp" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("userList" , json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sp = getContext().getSharedPreferences("aliApp" , Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("userList" , null);
        Type type = new TypeToken<ArrayList<Users>>() {}.getType();

        data = gson.fromJson(json , type);


        if(data == null){
            data = new ArrayList<>();
        }
    }


    private void getContact() {
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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

    }

    private void changeFabicon() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                        binding.fab.setImageResource(R.drawable.ic__chat);
                        CallFragment.pos = 0;

                        binding.fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getContext(), MakeCallActivity.class));
                            }
                        });


            }
        }, 400);

    }



}