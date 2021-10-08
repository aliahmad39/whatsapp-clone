package com.UniqueBulleteSolutions.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Adapter.UserAdapter;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;

import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityGroupInfoBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GroupInfoActivity extends AppCompatActivity {
    ActivityGroupInfoBinding binding;
    private String groupId;
    private String groupTitle;
    ApiInterface apiInterface;
    List<Users> list = new ArrayList<>();
    private ArrayList<Users> data = new ArrayList<>();
    UserAdapter adapter;
    String groupIcon;
    ArrayList<String> number = new ArrayList<>();
    private static ArrayList<Users> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        getContact();
        groupId = getIntent().getStringExtra("groupID");
        groupTitle = getIntent().getStringExtra("groupTitle");
        groupIcon = getIntent().getStringExtra("groupIcon");

        String path = ApiClient.BASE_URL + "ApiAuthentication/groupImages/" + groupIcon;
        Picasso.get().load(path).placeholder(R.drawable.avatar).into(binding.groupicon);


        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        getGroupDetail();


        binding.textView12.setText(groupTitle);


        loadData();
        setCount(data.size());




        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    private void setAdapter(){
        adapter = new UserAdapter(data, GroupInfoActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        binding.chatRecyclerView.setAdapter(adapter);
    }

    private void getGroupDetail() {
        apiInterface.getGroupDetail(groupId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            list.clear();
                            data.clear();

                            list = response.body().getData();


                            Toast.makeText(GroupInfoActivity.this, "group size :" + list.size(), Toast.LENGTH_SHORT).show();

                            for (Users users : list) {
                                users.setName(users.getPhoneNo());
                                Toast.makeText(GroupInfoActivity.this, "name before :"+users.getName(), Toast.LENGTH_SHORT).show();
                                if (number.contains(users.getPhoneNo())) {
                                    for (Users users1 : contacts) {
                                        if (users.getPhoneNo().equals(users1.getPhoneNo())) {
                                            users.setName(users1.getName());
                                            Toast.makeText(GroupInfoActivity.this, "name after :"+users.getName(), Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                }
                                users.setIndex("user");
                                data.add(users);

//                                binding.chatRecyclerView.hideShimmerAdapter();
                                adapter.notifyDataSetChanged();
                            }
//                            SharedPreferences sp = getSharedPreferences("aligroupApp", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sp.edit();
//                            editor.remove(groupId + "-info");
//                            editor.commit();
//                            editor.apply();
                            saveData();
                            //  setCount(list.size());

                        } else {
                            //  adapter.notifyDataSetChanged();
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
                // adapter.notifyDataSetChanged();
            }
        });
    }

    private void setCount(int size) {
        binding.tvCount.setText(size + "");
    }

    public void searchParticipant(View view) {
        SearchView searchView = (SearchView) view;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                Toast.makeText(GroupInfoActivity.this, newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // Toast.makeText(getContext(), "chat search", Toast.LENGTH_SHORT).show();
    }

    private void saveData() {
        SharedPreferences sp = getSharedPreferences("aligroupApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(groupId + "-info", json);
        editor.apply();
        loadData();
    }

    private void loadData() {
        SharedPreferences sp = getSharedPreferences("aligroupApp", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(groupId + "-info", null);
        Type type = new TypeToken<ArrayList<Users>>() {}.getType();

        data = gson.fromJson(json, type);
        //   chatAdapter.notifyDataSetChanged();
        //  binding.chatRecyclerView.hideShimmerAdapter();

        if (data == null) {
            data = new ArrayList<>();
        }
        setAdapter();

    }
    private void getContact() {

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

    }
}