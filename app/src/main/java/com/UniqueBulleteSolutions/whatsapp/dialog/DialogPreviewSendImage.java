package com.UniqueBulleteSolutions.whatsapp.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Models.MessageModel;
import com.UniqueBulleteSolutions.whatsapp.Models.groupMessages;
import com.UniqueBulleteSolutions.whatsapp.R;
import com.UniqueBulleteSolutions.whatsapp.utils.ChatServices;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class DialogPreviewSendImage {
    private Context context;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private Uri imageUri;
    private Uri videoUri;
    private ImageView arrow, share, image, btnplay;
    private VideoView playVideo;
    TextView totalTime , startTime ,name, tvtime;
    ConstraintLayout layout;
    SeekBar seekBar;
    double current_pos, total_duration;

    private FloatingActionButton btnSend;
    int index = 0;

    MessageModel messageModel = new MessageModel();
    groupMessages messages = new groupMessages();


    public DialogPreviewSendImage(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        this.dialog = new Dialog(context);
        this.progressDialog = new ProgressDialog(context);
        initialize();
    }

    public DialogPreviewSendImage(Context context, MessageModel messageModel, int index) {
        this.context = context;
        this.dialog = new Dialog(context);
        this.messageModel = messageModel;
        this.progressDialog = new ProgressDialog(context);
        this.index = index;
        initialize();
    }

    public DialogPreviewSendImage(Context context, Uri uri, int index) {
        this.context = context;
        this.dialog = new Dialog(context);
        imageUri = uri;
        this.progressDialog = new ProgressDialog(context);
        initialize();
    }

    public DialogPreviewSendImage(Context context, Uri uri, String index) {
        this.context = context;
        this.dialog = new Dialog(context);
        videoUri = uri;
        this.progressDialog = new ProgressDialog(context);
        initialize();
    }

    public DialogPreviewSendImage(Context context, groupMessages messages, int index, int index2) {
        this.context = context;
        this.dialog = new Dialog(context);
        this.messages = messages;
        this.progressDialog = new ProgressDialog(context);
        this.index = index;
        initialize();
    }

    public void initialize() {
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.zoom_chat_image);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        image = dialog.findViewById(R.id.dialog_selectImage);
        arrow = dialog.findViewById(R.id.dialog_leftArrow);
        share = dialog.findViewById(R.id.dialog_share_image);
        btnSend = dialog.findViewById(R.id.dialog_fabImageSend);
        name = dialog.findViewById(R.id.dialog_user_name);
        tvtime = dialog.findViewById(R.id.dialog_user_date);
        playVideo = dialog.findViewById(R.id.dialog_play_video);
        layout = dialog.findViewById(R.id.dialog_play_layout);
        btnplay = dialog.findViewById(R.id.video_play);
        seekBar = dialog.findViewById(R.id.sb_Progress);
        totalTime = dialog.findViewById(R.id.tv_total_time);
        startTime = dialog.findViewById(R.id.tv_start_time);

    }

    public void show(final OnCallBacks onCallBacks) {

        dialog.show();
        share.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);


        image.setImageBitmap(bitmap);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBacks.onButtonSendClick();
                dialog.dismiss();
            }
        });
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }


    public void zoomImage() {

        dialog.show();
        btnSend.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        tvtime.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
        tvtime.setText(messageModel.getMessage_time());
//        name.setText(messageModel.);

//        String path = ApiClient.BASE_URL + "ApiAuthentication/images/" + messageModel.getFile_path();
//
//
//        Glide.with(context).load(path)
//                .placeholder(R.drawable.avatar)
//                .into(image);

        image.setImageURI(imageUri);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "share clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void zoomGroupImage() {

        dialog.show();
        btnSend.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        tvtime.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
        tvtime.setText(messageModel.getMessage_time());
//        name.setText(messageModel.);

        String path = ApiClient.BASE_URL + "ApiAuthentication/groupData/images/" + messages.getFile_path();


        Glide.with(context).load(path)
                .placeholder(R.drawable.avatar)
                .into(image);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "share clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void setPlayVideo() {
        dialog.show();
        btnSend.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        tvtime.setVisibility(View.VISIBLE);
        tvtime.setText(messageModel.getMessage_time());
//        name.setText(messageModel.);

        String path = ApiClient.BASE_URL + "ApiAuthentication/videos/" + messageModel.getFile_path();


        // playVideo.setVideoPath(path);
          playVideo.setVideoURI(videoUri);

        //holder.mainvideo.setVideoURI(Uri.parse(videos.getVideourl()));

        MediaController mediaController = new MediaController(context);
        playVideo.setMediaController(mediaController);
        mediaController.setAnchorView(playVideo);
        playVideo.start();
       MediaPlayer mp = MediaPlayer.create(context ,videoUri);
        total_duration = mp.getDuration();
     //   Toast.makeText(context, "Duration :" + playVideo.getDuration(), Toast.LENGTH_SHORT).show();

        btnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
      //  mp.start();
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playVideo.isPlaying()) {
                    btnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                    playVideo.pause();
                } else {
                    btnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                    playVideo.start();
                }
            }
        });

        //get the video duration
        current_pos = playVideo.getCurrentPosition();

        Toast.makeText(context, total_duration+"", Toast.LENGTH_SHORT).show();

        //display video duration
        totalTime.setText(ChatServices.timeConversion((long) total_duration));
        startTime.setText(ChatServices.timeConversion((long) current_pos));
        seekBar.setMax((int) total_duration);
      //  seekBar.setProgress((int)current_pos);

        final Handler handler = new Handler();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (playVideo.isPlaying()) {
                        btnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                    } else {
                        btnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));

                    }
                    current_pos = playVideo.getCurrentPosition();
                    startTime.setText(ChatServices.timeConversion((long) current_pos));
                    seekBar.setProgress((int) current_pos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed) {
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);



        //seekbar change listner
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_pos = seekBar.getProgress();
                playVideo.seekTo((int) current_pos);
                playVideo.start();
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "share clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void setGroupPlayVideo() {
        dialog.show();
        btnSend.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        tvtime.setVisibility(View.VISIBLE);
        tvtime.setText(messageModel.getMessage_time());
//        name.setText(messageModel.);

        String path = ApiClient.BASE_URL + "ApiAuthentication/groupData/videos/" + messages.getFile_path();


        playVideo.setVideoPath(path);
        //  playVideo.start();
        //holder.mainvideo.setVideoURI(Uri.parse(videos.getVideourl()));

        MediaController mediaController = new MediaController(context);
        playVideo.setMediaController(mediaController);
        mediaController.setAnchorView(playVideo);
        playVideo.start();

        Toast.makeText(context, "Duration :" + playVideo.getDuration(), Toast.LENGTH_SHORT).show();
        btnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playVideo.isPlaying()) {
                    btnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                    playVideo.pause();
                } else {
                    btnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                    playVideo.start();
                }
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "share clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public interface OnCallBacks {
        public void onButtonSendClick();
    }

//    //pause video
//    public void setPause() {
//        pause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (videoView.isPlaying()) {
//                    videoView.pause();
//                    pause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
//                } else {
//                    videoView.start();
//                    pause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
//                }
//            }
//        });
//    }
//
//    // play video file
//    public void playVideo(int pos) {
//        try {
//            videoView.setVideoURI(videoArrayList.get(pos).getVideoUri());
//            videoView.start();
//            pause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
//            video_index = pos;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // display video progress
//    public void setVideoProgress() {
//        //get the video duration
//        current_pos = videoView.getCurrentPosition();
//        total_duration = videoView.getDuration();
//
//        //display video duration
//        total.setText(timeConversion((long) total_duration));
//        current.setText(timeConversion((long) current_pos));
//        seekBar.setMax((int) total_duration);
//        final Handler handler = new Handler();
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    current_pos = videoView.getCurrentPosition();
//                    current.setText(timeConversion((long) current_pos));
//                    seekBar.setProgress((int) current_pos);
//                    handler.postDelayed(this, 1000);
//                } catch (IllegalStateException ed) {
//                    ed.printStackTrace();
//                }
//            }
//        };
//        handler.postDelayed(runnable, 1000);
//
//        //seekbar change listner
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                current_pos = seekBar.getProgress();
//                videoView.seekTo((int) current_pos);
//            }
//        });
//    }


}