package com.UniqueBulleteSolutions.whatsapp.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.UniqueBulleteSolutions.whatsapp.R;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ChatServices {
    static double current_pos;

    static public void playAudio(Context context, Uri uri, TextView duration, SeekBar progress, ImageView play) {
        double total_duration;

        MediaPlayer player1 = MediaPlayer.create(context, uri);
        total_duration = player1.getDuration();
        current_pos = player1.getCurrentPosition();

        //display video duration
        duration.setText(timeConversion((long) total_duration));

        progress.setMax((int) total_duration);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (player1.isPlaying()) {
                    current_pos = player1.getCurrentPosition();
                    duration.setText(timeConversion((long) current_pos));
                    progress.setProgress((int) current_pos);
                }
            }
        }, 0, 200);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  viewHolder.play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                if (player1.isPlaying()) {
                    play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                    player1.pause();
                } else {
                    play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                    player1.start();
                }

            }
        });
        player1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                progress.setProgress(0);
                duration.setText(timeConversion((long) total_duration));
            }
        });

    }

    static public void openDocument(Context context, Uri uri) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(uri, "application/*");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(i);
    }

    //time conversion
    public static String timeConversion(long value) {
        String videoTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }

    //date time conversion

    // String str = new String("2014-09-01 10:00:00.000");
    // String time = str.split("\\s")[1].split("\\.")[0];
    //System.out.print(time);
    //   DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
    // Date date = new Date();
    // Timestamp ts=new Timestamp(messageModel.getTimestamp());
    // Date date = new Date(messageModel.getTimestamp());
    // String time = dateFormat.format(date);

}
