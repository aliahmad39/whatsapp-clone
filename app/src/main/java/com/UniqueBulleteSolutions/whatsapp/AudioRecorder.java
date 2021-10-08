package com.UniqueBulleteSolutions.whatsapp;

import android.media.MediaRecorder;

import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;


    private void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);



//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//            mediaRecorder.setOutputFile(path_save);
    }


    void start(String filePath) throws IOException {

      try {
          if (mediaRecorder == null) {
              initMediaRecorder();
          }

              mediaRecorder.setOutputFile(filePath);
              mediaRecorder.prepare();
              mediaRecorder.start();


      }catch (Exception e){
          e.printStackTrace();
      }
    }

    void stop() {
        try {
            mediaRecorder.stop();
            destroyMediaRecorder();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void destroyMediaRecorder() {
        mediaRecorder.release();
        mediaRecorder = null;
    }

}
