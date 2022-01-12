package com.UniqueBulleteSolutions.whatsapp.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.ChatDetailActivity;
import com.UniqueBulleteSolutions.whatsapp.GroupChatActivity;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.R;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {
    //List<Users> list;
    ArrayList<Users> list ;
    ArrayList<Users> backup;
    Context context;
    String CUID;
    private Dialog dialog;

    public UserAdapter(ArrayList<Users> list, Context context , String id) {
       this.list = new ArrayList<>(list);
        this.context = context;
        CUID = id;
        this.backup = list;
    }

    public UserAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void updateList(ArrayList<Users> list){
    list.clear();
    this.list = list;
    notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        if(users.getIndex().equals("user")) {

          //  String path = ApiClient.BASE_URL + "ApiAuthentication/profileImages/" + users.getUserPic();
            String path = ApiClient.BASE_URL + "profileImages/" + users.getUserPic();

            Picasso.get().load(path).placeholder(R.drawable.avatar).into(holder.image);

            holder.lastMessage.setText(users.getLastMessage() + "");
            holder.userTime.setText(users.getTime() + "");
            holder.userName.setText(users.getName());


            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(context);
                    ImageView chat, audiocall, videocall, about, dialodImage;
                    TextView dialogname;

//                dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
                    dialog.setContentView(R.layout.popup_user_image);

//                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(lp);


                    dialog.setCancelable(true);


                    dialog.show();

                    chat = dialog.findViewById(R.id.dialog_chat);
                    audiocall = dialog.findViewById(R.id.dialog_audio_call);
                    videocall = dialog.findViewById(R.id.dialog_video_call);
                    about = dialog.findViewById(R.id.dialog_user_about);
                    dialodImage = dialog.findViewById(R.id.user_dialog_image);
                    dialogname = dialog.findViewById(R.id.dialog_user_name);

                    dialogname.setText(users.getName());
                    Picasso.get().load(path).placeholder(R.drawable.avatar).into(dialodImage);


                    chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "chat clicked", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, ChatDetailActivity.class);
                            intent.putExtra("userId", users.getId());
                            intent.putExtra("userName", users.getName());
                            intent.putExtra("currentUid", CUID);
                            intent.putExtra("currentUPic", users.getUserPic());
                            intent.putExtra("currentUStatus", users.getUserStatus());
                            // intent.putExtra("profilePic", users.getProfilePic());

                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    audiocall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "audio call clicked", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    videocall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "video call clicked", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    about.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "about clicked", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            });


            holder.layout.setOnClickListener(new View.OnClickListener() {
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
        else{
            String path = ApiClient.BASE_URL + "ApiAuthentication/groupImages/" + users.getGroupIcon();

            Picasso.get().load(path).placeholder(R.drawable.avatar).into(holder.image);

            holder.userName.setText(users.getName());
            holder.lastMessage.setText(users.getGroupLastMsg() + "");
            holder.userTime.setText(users.getGroupTime() + "");


            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(context);
                    ImageView chat, audiocall, videocall, about, dialodImage;
                    TextView dialogname;

//                dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
                    dialog.setContentView(R.layout.popup_user_image);

//                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(lp);


                    dialog.setCancelable(true);


                    dialog.show();

                    chat = dialog.findViewById(R.id.dialog_chat);
                    audiocall = dialog.findViewById(R.id.dialog_audio_call);
                    videocall = dialog.findViewById(R.id.dialog_video_call);
                    about = dialog.findViewById(R.id.dialog_user_about);
                    dialodImage = dialog.findViewById(R.id.user_dialog_image);
                    dialogname = dialog.findViewById(R.id.dialog_user_name);

                    dialogname.setText(users.getName());


                  Picasso.get().load(path).placeholder(R.drawable.avatar).into(dialodImage);


                    chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ChatDetailActivity.class);
                            intent.putExtra("userId", users.getId());
                            intent.putExtra("userName", users.getName());
                            intent.putExtra("currentUid", CUID);
                            intent.putExtra("currentUPic", users.getGroupIcon());
                            intent.putExtra("currentUStatus", users.getUserStatus());

                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    audiocall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "audio call clicked", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    videocall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "video call clicked", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    about.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "about clicked", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            });


            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent = new Intent(context, GroupChatActivity.class);
                        intent.putExtra("groupId", users.getGroupID());
                        intent.putExtra("groupTitle", users.getName());
//                    intent.putExtra("currentUid", CUID);
                    intent.putExtra("currentUPic", users.getGroupIcon());
//                    intent.putExtra("currentUStatus", users.getUserStatus());
                        // intent.putExtra("profilePic", users.getProfilePic());

                        context.startActivity(intent);
                    }catch(Exception e){
                        Toast.makeText(context, "group error"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }


                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.  ViewHolder {
        ImageView image;
        TextView userName, lastMessage , userTime;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image_user);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            userTime =itemView.findViewById(R.id.tvUserTime);
            layout = itemView.findViewById(R.id.layout_sender);

        }

//        public  void showInterface(onItemCallBack onitemCallBack){
//            layout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    onitemCallBack.onCall(1);
//
////                new AlertDialog.Builder(context)
////                        .setTitle("Delete chat with '"+users.getName()+"'?")
////                        .setMessage("Delete media in this chat")
////                        .setIcon(R.drawable.ic_check_box)
////                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
//////                                apiInterface.deleteMessages(messageModel.getId()).enqueue(new Callback<UserResponse>() {
//////                                    @Override
//////                                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//////                                        if (response != null) {
//////                                            if (response.body().getStatus().equals("1")) {
//////                                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//////                                            }
//////                                        }
//////                                    }
//////
//////                                    @Override
//////                                    public void onFailure(Call<UserResponse> call, Throwable t) {
//////
//////                                    }
//////                                });
////                            }
////                        })
////                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                dialog.dismiss();
////                            }
////                        }).show();
//                    Toast.makeText(context, "long click ", Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });
//
//        }

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
            if (keyword.toString().trim().isEmpty()) {
                filtereddata.addAll(backup);
                Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "not", Toast.LENGTH_SHORT).show();
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
            list.addAll((ArrayList<Users>)results.values);
           notifyDataSetChanged();

        }
    };


}
