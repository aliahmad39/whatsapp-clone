package com.UniqueBulleteSolutions.whatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Models.Status;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.Models.UsersStatus;
import com.UniqueBulleteSolutions.whatsapp.R;
import com.UniqueBulleteSolutions.whatsapp.databinding.ItemStatusBinding;
import com.UniqueBulleteSolutions.whatsapp.fragment.StatusFragment;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> implements Filterable {
    Context context;
    ArrayList<UsersStatus> usersStatuses;
    ArrayList<UsersStatus> backup;

    public TopStatusAdapter(Context context, ArrayList<UsersStatus> usersStatuses) {
        this.context = context;
        this.usersStatuses = usersStatuses;
        backup = usersStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);


        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {
        UsersStatus usersStatus = usersStatuses.get(position);

        Status lastStatus = usersStatus.getStatuses().get(usersStatus.getStatuses().size()-1);

        //String path= ApiClient.BASE_URL+"ApiAuthentication/statuses/"+lastStatus.getStatus_path();
        //String path2=ApiClient.BASE_URL+"ApiAuthentication/profileImages/"+usersStatus.getProfileImage();

        String path= ApiClient.BASE_URL+"statuses/"+lastStatus.getStatus_path();
        String path2=ApiClient.BASE_URL+"profileImages/"+usersStatus.getProfileImage();

        Glide.with(context).load(path).into(holder.binding.circleImage);
        holder.binding.userNameStatus.setText(usersStatus.getName());

//
//        // String str = new String("2014-09-01 10:00:00.000");
//        // String time = str.split("\\s")[1].split("\\.")[0];
//        //System.out.print(time);
//        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
//        // Date date = new Date();
//        // Timestamp ts=new Timestamp(messageModel.getTimestamp());
//        Date date=new Date(lastStatus.getTimestamp());
//        String time=dateFormat.format(date);



        holder.binding.statusTime.setText(lastStatus.getStatus_time());

        holder.binding.circularStatusView.setPortionsCount(usersStatus.getStatuses().size());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status: usersStatus.getStatuses()){
                  //  myStories.add(new MyStory(ApiClient.BASE_URL+"ApiAuthentication/statuses/"+status.getStatus_path()));
                    myStories.add(new MyStory(ApiClient.BASE_URL+"statuses/"+status.getStatus_path()));
                }

                new StoryView.Builder(((FragmentActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(usersStatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(path2) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                Toast.makeText(context, "play", Toast.LENGTH_SHORT).show();
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return usersStatuses.size();
    }



    public class TopStatusViewHolder extends RecyclerView.ViewHolder {
      ItemStatusBinding binding;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
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
            ArrayList<UsersStatus> filtereddata = new ArrayList<>();
            if (keyword.toString().isEmpty()) {
                filtereddata.addAll(backup);
            } else {

                for (UsersStatus obj : backup) {
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

            usersStatuses.clear();
            usersStatuses.addAll((ArrayList<UsersStatus>)results.values);
            notifyDataSetChanged();
        }
    };
}
