package com.UniqueBulleteSolutions.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Adapter.CallListAdapter;
import com.UniqueBulleteSolutions.whatsapp.Adapter.UserAdapter;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.MainActivity;
import com.UniqueBulleteSolutions.whatsapp.MakeCallActivity;
import com.UniqueBulleteSolutions.whatsapp.Models.Calllist;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.R;
import com.UniqueBulleteSolutions.whatsapp.SettingActivity;
import com.UniqueBulleteSolutions.whatsapp.databinding.FragmentCallBinding;
import com.UniqueBulleteSolutions.whatsapp.databinding.FragmentChatBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CallFragment extends Fragment {
    List<Calllist> list = new ArrayList<>();
    public CallFragment() {
        // Required empty public constructor
    }

    FragmentCallBinding binding;
//    List<Users> list = new ArrayList<>();
    ArrayList<Users> data = new ArrayList<>();
    FirebaseDatabase database;
    private String currentId;
    ApiInterface apiInterface;
    String CUID="";
    CallListAdapter adapter;
    public static int pos = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCallBinding.inflate(inflater, container, false);
        changeFabicon();

//        CUID = MainActivity.getCUID();
//
//        data = ChatFragment.getData();
//
//
//        adapter = new UserAdapter(data ,getContext() , CUID);
//
//
//        binding.chatRecyclerView.showShimmerAdapter();
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        binding.chatRecyclerView.setLayoutManager(layoutManager);
//
//        Retrofit retrofit = ApiClient.getClient();
//        apiInterface = retrofit.create(ApiInterface.class);
//
//        apiInterface.getUsers().enqueue(new Callback<UserResponse>() {
//            @Override
//            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                try{
//                    if(response != null){
//                        if(response.body().getStatus().equals("1")){
//                            list.clear();
//                            data.clear();
//                            list = response.body().getData();
//
//                            for(int i =0 ; i< list.size() ; i++){
//                                Users users = list.get(i);
//                                if(users.getId().equals(CUID)){
//                                    // list.remove(i);
//                                    // break;
//                                }else{
//                                    data.add(users);
//                                }
//                            }
//
//                            binding.chatRecyclerView.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();
//                            binding.chatRecyclerView.hideShimmerAdapter();
//                        }else{
//                            Toast.makeText(getContext(), response.body().getMessage()+"", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                }catch(Exception e){
//                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserResponse> call, Throwable t) {
//                Toast.makeText(getContext(), "Error :" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });






        list.clear();



        list.add(new Calllist(
                "1" ,
                "Ali" ,
                "2021/7/30 , 9:24 pm" ,
                "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimages.unsplash.com%2Fphoto-1503023345310-bd7c1de61c7d%3Fixid%3DMnwxMjA3fDB8MHxzZWFyY2h8Mnx8aHVtYW58ZW58MHx8MHx8%26ixlib%3Drb-1.2.1%26w%3D1000%26q%3D80&imgrefurl=https%3A%2F%2Funsplash.com%2Fs%2Fphotos%2Fhuman&tbnid=wp1tdfttzeGYZM&vet=12ahUKEwivl4quqoryAhWH04UKHRRkDXIQMygDegUIARDOAQ..i&docid=ZaycYywhXLmIVM&w=1000&h=1250&q=images&ved=2ahUKEwivl4quqoryAhWH04UKHRRkDXIQMygDegUIARDOAQ"
                ,                   "income"


        ));
        list.add(new Calllist(
                "2" ,
                "Umer" ,
                "2021/5/20 , 7:24 pm" ,
                "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimages.unsplash.com%2Fphoto-1503023345310-bd7c1de61c7d%3Fixid%3DMnwxMjA3fDB8MHxzZWFyY2h8Mnx8aHVtYW58ZW58MHx8MHx8%26ixlib%3Drb-1.2.1%26w%3D1000%26q%3D80&imgrefurl=https%3A%2F%2Funsplash.com%2Fs%2Fphotos%2Fhuman&tbnid=wp1tdfttzeGYZM&vet=12ahUKEwivl4quqoryAhWH04UKHRRkDXIQMygDegUIARDOAQ..i&docid=ZaycYywhXLmIVM&w=1000&h=1250&q=images&ved=2ahUKEwivl4quqoryAhWH04UKHRRkDXIQMygDegUIARDOAQ"
                ,                   "missed"


        ));
        list.add(new Calllist(
                "3" ,
                "Awais" ,
                "2021/9/25 , 8:24 pm" ,
                "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimages.unsplash.com%2Fphoto-1503023345310-bd7c1de61c7d%3Fixid%3DMnwxMjA3fDB8MHxzZWFyY2h8Mnx8aHVtYW58ZW58MHx8MHx8%26ixlib%3Drb-1.2.1%26w%3D1000%26q%3D80&imgrefurl=https%3A%2F%2Funsplash.com%2Fs%2Fphotos%2Fhuman&tbnid=wp1tdfttzeGYZM&vet=12ahUKEwivl4quqoryAhWH04UKHRRkDXIQMygDegUIARDOAQ..i&docid=ZaycYywhXLmIVM&w=1000&h=1250&q=images&ved=2ahUKEwivl4quqoryAhWH04UKHRRkDXIQMygDegUIARDOAQ"
                ,                   "out"


        ));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.CallRecyclerView.setLayoutManager(layoutManager);
        binding.CallRecyclerView.showShimmerAdapter();

        binding.CallRecyclerView.setAdapter(new CallListAdapter(list , getContext()));


        binding.CallRecyclerView.hideShimmerAdapter();




    return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.call_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear_call_log:

                Toast.makeText(getContext(), "clear_call_log", Toast.LENGTH_SHORT).show();
            break;
            case R.id.call_setting:
                startActivity(new Intent(getContext() , SettingActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeFabicon() {
        //  binding.fab.hide();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //    binding.fab.show();
                binding.fab.setImageResource(R.drawable.ic_call);
                pos = 2;

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