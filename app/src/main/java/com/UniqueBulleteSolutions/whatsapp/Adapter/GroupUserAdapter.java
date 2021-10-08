package com.UniqueBulleteSolutions.whatsapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.R;
import com.UniqueBulleteSolutions.whatsapp.dialog.DialogPreviewSendImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupUserAdapter extends  RecyclerView.Adapter<GroupUserAdapter.ViewHolder> {
    ArrayList<Users> list;
    Context context;
    int index = 0;


    public GroupUserAdapter() {
    }

    public GroupUserAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public GroupUserAdapter(ArrayList<Users> list, Context context , int index) {
        this.list = list;
        this.context = context;
        this.index = index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.smaple_create_group, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);
        String path= ApiClient.BASE_URL+"ApiAuthentication/profileImages/"+users.getUserPic();




        Picasso.get().load(path).placeholder(R.drawable.avatar).into(holder.image);
        holder.userName.setText(users.getName());



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image ;
        TextView userName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.group_user_ProfileImage);
            userName = itemView.findViewById(R.id.group_user_name);



        }


    }

}
