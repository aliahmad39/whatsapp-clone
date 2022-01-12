package com.UniqueBulleteSolutions.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;

import com.UniqueBulleteSolutions.whatsapp.ChatDetailActivity;
import com.UniqueBulleteSolutions.whatsapp.MainActivity;
import com.UniqueBulleteSolutions.whatsapp.MainViewModel;
import com.UniqueBulleteSolutions.whatsapp.MakeGroupActivity;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.R;
import com.UniqueBulleteSolutions.whatsapp.fragment.CallFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CallAdapter extends RecyclerView.Adapter implements Filterable {
    //List<Users> list;
    ArrayList<Users> list;
    public static ArrayList<Users> selectList;
    ArrayList<Users> backup;
    Context context;
    String CUID;
    int index;
    int CALL_VIEW_TYPE = 2;
    int CHAT_VIEW_TYPE = 0;
    MainViewModel mainViewModel;
    boolean isEnable = false;
    boolean isSelectAll = false;
    View lineview;
    View view;
    Toolbar toolbar;
    int holderClickAction = 0;
    RecyclerView rv;
    FloatingActionButton fab;
    GroupUserAdapter adapter;


    public static ArrayList<Users> getSelectList() {
        return selectList;
    }

    public CallAdapter(ArrayList<Users> list, Context context, String id) {
        this.list = list;
        this.context = context;
        CUID = id;
        backup = list;
    }

    public CallAdapter(ArrayList<Users> list, Context context, String id, Toolbar toolbar) {
        this.list = list;
        this.context = context;
        CUID = id;
        backup = list;
        this.toolbar = toolbar;
    }

    public CallAdapter(ArrayList<Users> list, Context context, String id, Toolbar toolbar, int n) {
        this.list = list;
        this.context = context;
        CUID = id;
        backup = list;
        this.toolbar = toolbar;
        holderClickAction = n;
    }

    public CallAdapter(ArrayList<Users> list, Context context, String id, Toolbar toolbar, int n, FloatingActionButton fab, RecyclerView makeGroup, View view) {
        this.list = list;
        this.context = context;
        CUID = id;
        backup = list;
        this.toolbar = toolbar;
        this.fab = fab;
        rv = makeGroup;
        holderClickAction = n;
        selectList = new ArrayList<>();
        lineview = view;
    }

    public CallAdapter(Context context, String CUID) {
        this.context = context;
        this.CUID = CUID;
    }

    public void updateList(ArrayList<Users> list) {
        list.clear();
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CHAT_VIEW_TYPE) {
            view = LayoutInflater.from(context).inflate(R.layout.demo_chat, parent, false);

            mainViewModel = ViewModelProviders.of((FragmentActivity) context).get(MainViewModel.class);


            return new ChatViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.demo_calls_2, parent, false);

            mainViewModel = ViewModelProviders.of((FragmentActivity) context).get(MainViewModel.class);

            return new CallViewHolder(view);
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Users users = list.get(position);
      //  String path = ApiClient.BASE_URL + "ApiAuthentication/profileImages/" + users.getUserPic();
        String path = ApiClient.BASE_URL + "profileImages/" + users.getUserPic();


        if (holder.getClass() == ChatViewHolder.class) {
            CallAdapter.ChatViewHolder viewHolder = (CallAdapter.ChatViewHolder) holder;
            Picasso.get().load(path).placeholder(R.drawable.avatar).into(viewHolder.image);
            viewHolder.userName.setText(users.getName());
            viewHolder.lastmessage.setText(users.getPhoneNo()+"");
            viewHolder.ivCheckBox.setVisibility(View.GONE);
            selectList.clear();


            if (holderClickAction == 1) {
                selectList.clear();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.ivCheckBox.getVisibility() == View.GONE) {
                            viewHolder.ivCheckBox.setVisibility(View.VISIBLE);
                            selectList.add(users);
                            showRecyclerView(selectList);

                        } else {
                            viewHolder.ivCheckBox.setVisibility(View.GONE);
                            selectList.remove(users);
                            showRecyclerView(selectList);
                        }
                    }
                });


                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectList.size() >= 1) {
                            Toast.makeText(context, "selected contact is :" + selectList.size(), Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, MakeGroupActivity.class));
                        } else {
                            Toast.makeText(context, "At least 1 contact must be selected", Toast.LENGTH_SHORT).show();
                        }


                    }

                });


            } else {

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, ChatDetailActivity.class);
                        intent.putExtra("userId", users.getId());
                        intent.putExtra("userName", users.getName());
                        intent.putExtra("currentUid", CUID);
                        intent.putExtra("currentUPic", users.getUserPic());
                        intent.putExtra("currentUStatus", users.getUserStatus());
                       //  intent.putExtra("profilePic", users.getProfilePic());

                        context.startActivity(intent);

                        Toast.makeText(context, "Chat click", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }
        else {
            CallAdapter.CallViewHolder viewHolder = (CallAdapter.CallViewHolder) holder;


            Picasso.get().load(path).placeholder(R.drawable.avatar).into(viewHolder.image);
            viewHolder.userName.setText(users.getName());
       viewHolder.calltime.setText(users.getPhoneNo()+"");

            viewHolder.ivcall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ChatDetailActivity.class);
                    intent.putExtra("userId", users.getId());
                    intent.putExtra("userName", users.getName());
                    intent.putExtra("currentUid", CUID);
                    intent.putExtra("currentUPic", users.getUserPic());
                    intent.putExtra("currentUStatus", users.getUserStatus());
                    // intent.putExtra("profilePic", users.getProfilePic());

                    context.startActivity(intent);
                }
            });
        }
    }

    private void ClickItem(ChatViewHolder holder) {
        Users users = list.get(holder.getAdapterPosition());

        if (holder.ivCheckBox.getVisibility() == View.GONE) {
            holder.ivCheckBox.setVisibility(View.VISIBLE);

            holder.itemView.setBackgroundColor(Color.LTGRAY);
            selectList.add(users);

        } else {

            holder.ivCheckBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            selectList.remove(users);

        }
        mainViewModel.setText(String.valueOf(selectList.size()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
      // int pos =  context.getIntent().getIntExtra("index" , 0);
        if (CallFragment.pos == 0) {
            return CHAT_VIEW_TYPE;
        } else {
            return CALL_VIEW_TYPE;
        }
    }


    public class CallViewHolder extends RecyclerView.ViewHolder {
        ImageView image, ivcall;
        TextView userName, calltime;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image_c);
            userName = itemView.findViewById(R.id.userName_c);
            ivcall = itemView.findViewById(R.id.iv_call);
            calltime =itemView.findViewById(R.id.user_status);


        }

    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView image, ivCheckBox;
        TextView userName, lastmessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image_ch);
            userName = itemView.findViewById(R.id.userName_ch);
            lastmessage = itemView.findViewById(R.id.user_status_chat);
            ivCheckBox = itemView.findViewById(R.id.check);


        }

    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    //Anonymous inner class
    Filter filter = new Filter() {
        @Override  //background thread
        protected FilterResults performFiltering(CharSequence keyword) {
            ArrayList<Users> filtereddata = new ArrayList<>();
            if (keyword.toString().isEmpty()) {
                filtereddata.addAll(backup);
            } else {

                for (Users obj : backup) {
                    if (obj.getName().toString().toLowerCase().contains(keyword.toString().toLowerCase())) {
                        filtereddata.add(obj);
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filtereddata;
            return results;
        }

        @Override  //main UI thread
        protected void publishResults(CharSequence constraint, FilterResults results) {

            list.clear();
            list.addAll((ArrayList<Users>) results.values);
            notifyDataSetChanged();
        }
    };

    private void showRecyclerView(ArrayList<Users> list2) {
        if (list2.size() >= 1) {
            rv.setVisibility(View.VISIBLE);
            lineview.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            adapter = new GroupUserAdapter(list2, context, 1);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        } else {
            rv.setVisibility(View.GONE);
            lineview.setVisibility(View.GONE);
            rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            adapter = new GroupUserAdapter(list2, context, 1);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}


