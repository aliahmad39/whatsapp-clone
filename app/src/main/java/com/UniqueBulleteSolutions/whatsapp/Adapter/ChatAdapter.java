package com.UniqueBulleteSolutions.whatsapp.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;

import com.UniqueBulleteSolutions.whatsapp.Models.MessageModel;

import com.UniqueBulleteSolutions.whatsapp.R;
import com.UniqueBulleteSolutions.whatsapp.dialog.DialogPreviewSendImage;
import com.UniqueBulleteSolutions.whatsapp.utils.ChatServices;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.UniqueBulleteSolutions.whatsapp.utils.ChatServices.*;


public class ChatAdapter extends RecyclerView.Adapter {
    //List<MessageModel> messageModels;
    ArrayList<MessageModel> messageModels = new ArrayList<>();
    Context context;
    String rid, sid;
    int SENDER_VIEW_TYPE = 1 , RECEIVER_VIEW_TYPE = 2;
    ApiInterface apiInterface;
    private ImageButton tmpbtnPlay;
    RelativeLayout showimagelayout , showchatlayout;
    ImageView showImage;
    private Uri downloadImageUri , downloadAudioUri , downloadVideoUri;
    MediaPlayer player;

    String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels.addAll(messageModels);
        this.context = context;
        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    public ChatAdapter(Context context, String rid, String sid) {
        this.context = context;
        this.rid = rid;
        this.sid = sid;
        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
    }



    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String sid, String rid, RelativeLayout showimagelayout, ImageView showImage) {
        this.messageModels = messageModels;
        this.context = context;
        this.rid = rid;
        this.sid = sid;
        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
        this.showimagelayout = showimagelayout;
        this.showImage = showImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getSender_id().equals(sid)) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                apiInterface.deleteMessages(messageModel.getId()).enqueue(new Callback<UserResponse>() {
                                    @Override
                                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                        if (response != null) {
                                            if (response.body().getStatus().equals("1")) {
                                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserResponse> call, Throwable t) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

                return false;
            }
        });
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;

            if (messageModel.getMessage().equals("photo")) {
                setLayoutVisibility(viewHolder.senderMsg, View.GONE, viewHolder.senderImage, View.VISIBLE, viewHolder.layoutVoice, View.GONE, viewHolder.layoutVideo, View.GONE, viewHolder.documents, View.GONE, viewHolder.layoutDocumentSize, View.GONE);
                // setProgressVisibility(viewHolder.pb_image,View.VISIBLE,viewHolder.pb_video,View.GONE,viewHolder.pb_voice,View.GONE,viewHolder.pb_document,View.GONE);

                String path = ApiClient.BASE_URL + "images/" + messageModel.getFile_path();
                viewHolder.pb_image.setVisibility(View.VISIBLE);
                downloadFile(path, viewHolder.senderImage, viewHolder.pb_image, null, null, null, null);

                viewHolder.senderImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        downloadFile(path, viewHolder.senderImage, viewHolder.pb_image, null, null, null, null);
                        new DialogPreviewSendImage(context, downloadImageUri, 1).zoomImage();

                    }
                });


//
//                Glide.with(context).load(path)
//                        .placeholder(R.drawable.avatar)
//                        .into(viewHolder.senderImage);
            }
            else if (messageModel.getMessage().equals("audio")) {
                setLayoutVisibility(viewHolder.senderMsg, View.GONE, viewHolder.senderImage, View.GONE, viewHolder.layoutVoice, View.VISIBLE, viewHolder.layoutVideo, View.GONE, viewHolder.documents, View.GONE, viewHolder.layoutDocumentSize, View.GONE);
                String path = ApiClient.BASE_URL + "audio/" + messageModel.getFile_path();
                downloadFile(path, null, viewHolder.pb_voice, "audio", viewHolder.tv_duration, viewHolder.musicProgress, viewHolder.play);

            } else if (messageModel.getMessage().equals("video")) {
                setLayoutVisibility(viewHolder.senderMsg, View.GONE, viewHolder.senderImage, View.GONE, viewHolder.layoutVoice, View.GONE, viewHolder.layoutVideo, View.VISIBLE, viewHolder.documents, View.GONE, viewHolder.layoutDocumentSize, View.GONE);
                String path = ApiClient.BASE_URL + "videos/" + messageModel.getFile_path();
                downloadFile(path, null, viewHolder.pb_video, null, null, null, null);
                viewHolder.playVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFile(path, null, viewHolder.pb_video, null, null, null, null);
                        new DialogPreviewSendImage(context, downloadVideoUri, "2").setPlayVideo();
                    }
                });


            } else if (messageModel.getMessage().equals("document")) {
                setLayoutVisibility(viewHolder.senderMsg, View.GONE, viewHolder.senderImage, View.GONE, viewHolder.layoutVoice, View.GONE, viewHolder.layoutVideo, View.GONE, viewHolder.documents, View.VISIBLE, viewHolder.layoutDocumentSize, View.VISIBLE);
                if (messageModel.getExtension().toLowerCase().equals("pdf")) {
                    viewHolder.documentImage.setImageResource(R.drawable.ic_pdf);
                } else {
                    viewHolder.documentImage.setImageResource(R.drawable.ic_file);
                }

                String path = ApiClient.BASE_URL + "document/" + messageModel.getFile_path();

                viewHolder.title.setText(messageModel.getFileName());
                viewHolder.size.setText(messageModel.getFileSize() + ".");
                viewHolder.extn.setText(messageModel.getExtension());

                viewHolder.documents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFile(path, null, viewHolder.pb_document, null, null, null, null);
                    }
                });


            } else {
                setLayoutVisibility(viewHolder.senderMsg, View.VISIBLE, viewHolder.senderImage, View.GONE, viewHolder.layoutVoice, View.GONE, viewHolder.layoutVideo, View.GONE, viewHolder.documents, View.GONE, viewHolder.layoutDocumentSize, View.GONE);
                viewHolder.senderMsg.setText(messageModel.getMessage());
            }

            viewHolder.senderTime.setText(messageModel.getMessage_time());
        }
        else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;

            if (messageModel.getMessage().equals("photo")) {
                setLayoutVisibility(viewHolder.receiverMsg, View.GONE, viewHolder.receiverImage, View.VISIBLE, viewHolder.layoutVoice, View.GONE, viewHolder.layoutVideo, View.GONE, viewHolder.documents, View.GONE, viewHolder.layoutDocumentSize, View.GONE);

                String path = ApiClient.BASE_URL + "images/" + messageModel.getFile_path();
                downloadFile(path, viewHolder.receiverImage, viewHolder.pb_image, null, null, null, null);

                viewHolder.receiverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        downloadFile(path, viewHolder.receiverImage, viewHolder.pb_image, null, null, null, null);
                        new DialogPreviewSendImage(context, downloadImageUri, 1).zoomImage();

                    }
                });


//                Glide.with(context).load(path)
//                        .placeholder(R.drawable.avatar)
//                        .into(viewHolder.receiverImage);
            } else if (messageModel.getMessage().equals("audio")) {
                setLayoutVisibility(viewHolder.receiverMsg, View.GONE, viewHolder.receiverImage, View.GONE, viewHolder.layoutVoice, View.VISIBLE, viewHolder.layoutVideo, View.GONE, viewHolder.documents, View.GONE, viewHolder.layoutDocumentSize, View.GONE);
                String path = ApiClient.BASE_URL + "audio/" + messageModel.getFile_path();
                downloadFile(path, null, viewHolder.pb_voice, "audio", viewHolder.tv_duration, viewHolder.musicProgress, viewHolder.play);
            } else if (messageModel.getMessage().equals("video")) {
                setLayoutVisibility(viewHolder.receiverMsg, View.GONE, viewHolder.receiverImage, View.GONE, viewHolder.layoutVoice, View.GONE, viewHolder.layoutVideo, View.VISIBLE, viewHolder.documents, View.GONE, viewHolder.layoutDocumentSize, View.GONE);
                //String path = ApiClient.BASE_URL + "ApiAuthentication/videos/" + messageModel.getFile_path();
                String path = ApiClient.BASE_URL + "videos/" + messageModel.getFile_path();
                downloadFile(path, null, viewHolder.pb_video, null, null, null, null);
                viewHolder.playVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFile(path, null, viewHolder.pb_video, null, null, null, null);
                        new DialogPreviewSendImage(context, downloadVideoUri, "2").setPlayVideo();
                    }
                });

            } else if (messageModel.getMessage().equals("document")) {
                setLayoutVisibility(viewHolder.receiverMsg, View.GONE, viewHolder.receiverImage, View.GONE, viewHolder.layoutVoice, View.GONE, viewHolder.layoutVideo, View.GONE, viewHolder.documents, View.VISIBLE, viewHolder.layoutDocumentSize, View.VISIBLE);
                if (messageModel.getExtension().toLowerCase().equals("pdf")) {
                    viewHolder.documentImage.setImageResource(R.drawable.ic_pdf);
                } else {
                    viewHolder.documentImage.setImageResource(R.drawable.ic_file);
                }

              //  String path = ApiClient.BASE_URL + "ApiAuthentication/document/" + messageModel.getFile_path();
                String path = ApiClient.BASE_URL + "document/" + messageModel.getFile_path();

                viewHolder.title.setText(messageModel.getFileName());
                viewHolder.size.setText(messageModel.getFileSize() + ".");
                viewHolder.extn.setText(messageModel.getExtension());

                viewHolder.documents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFile(path, null, viewHolder.pb_document, null, null, null, null);
                    }
                });

            } else {
                setLayoutVisibility(viewHolder.receiverMsg, View.VISIBLE, viewHolder.receiverImage, View.GONE, viewHolder.layoutVoice, View.GONE, viewHolder.layoutVideo, View.GONE, viewHolder.documents, View.GONE, viewHolder.layoutDocumentSize, View.GONE);
                viewHolder.receiverMsg.setText(messageModel.getMessage());
            }
            viewHolder.receiverTime.setText(messageModel.getMessage_time());
        }

    }

    private void setProgressVisibility(ProgressBar pb_image, int visible, ProgressBar pb_video, int gone, ProgressBar pb_voice, int gone1, ProgressBar pb_document, int gone2) {


    }

    public void updateList(ArrayList<MessageModel> list) {
        messageModels.clear();
       messageModels = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    private void downloadFile(String url, ImageView image, ProgressBar pb, String type, TextView duration, SeekBar progress, ImageView play) {
        String filename = URLUtil.guessFileName(url, null, null);
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return download();
            }

            @Nullable
            private Boolean download() {
                try {
                    File file = makeFile("Download", filename);
                    if (file.exists())
                        return true;
                    //file.delete();
                    FileOutputStream fs = new FileOutputStream(file);

                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();
                    InputStream input = new BufferedInputStream(u.openStream());
                    byte data[] = new byte[contentLength];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress((int) ((total * 100) / contentLength));
                        fs.write(data, 0, count);
                    }
                    fs.flush();
                    fs.close();
                    input.close();
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
//                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                   // Toast.makeText(context, "Download Sucessfully!", Toast.LENGTH_SHORT).show();
                    String exten = filename.substring(filename.lastIndexOf('.'));
                    //   Toast.makeText(context, exten, Toast.LENGTH_LONG).show();

                    switch (exten) {
                        case ".pdf":
                            //   Toast.makeText(context, "i m pdf", Toast.LENGTH_SHORT).show();
                            Uri uri = getFileUri("Download", filename);
                            pb.setVisibility(View.GONE);
                            ChatServices.openDocument(context, uri);
                            break;
                        case ".docx":
                            //  Toast.makeText(context, "i m pdf", Toast.LENGTH_SHORT).show();
                            Uri uri1 = getFileUri("Download", filename);
                            pb.setVisibility(View.GONE);
                            ChatServices.openDocument(context, uri1);
                            break;
                        case ".jpg":
                            //  Toast.makeText(context, "i m mp4", Toast.LENGTH_SHORT).show();
                            // openImage("Download", filename, image);
                            Uri uri2 = getFileUri("Download", filename);
                            downloadImageUri = uri2;
                            pb.setVisibility(View.GONE);
                            image.setImageURI(uri2);
                            break;
                        case ".mp4":
                            //  Toast.makeText(context, "i m mp4", Toast.LENGTH_SHORT).show();
                            if (type != null) {
                                downloadAudioUri = null;
                                pb.setVisibility(View.GONE);
                                downloadAudioUri = getFileUri("Download", filename);
                                playAudio(context, downloadAudioUri, duration, progress, play);
                                //   playAudio("Download", filename, duration, progress, play);

                            } else {
                                pb.setVisibility(View.GONE);
                                downloadVideoUri = getFileUri("Download", filename);
                            }

                            //  openImage("Download" , filename , imageView);
                            break;
                    }

                    //  openPdf(filename);

//                    File file =
//                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/document" + "sample.pdf");

//                    String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//                    File myDir = new File(root + "/docu");
//                    myDir.mkdir();
//                    final File file = new File(myDir, "sample.pdf");

//                    final File file = makeFile("Download", filename);
//                    Uri uri = FileProvider.getUriForFile(read_write_pdf.this, "com.ali.read_write_file_android" + ".provider", file);
//                    iv.setImageURI(uri);


//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setDataAndType(uri, "application/pdf");
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(i);
                } else {
                    //pb.setVisibility(View.);
                    Toast.makeText(context, "Unable to download this file", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                //  pb.setProgress(values[0]);
                //  tvProgress.setText(values[0] + "");
            }
        }.execute();
    }

    public Uri getFileUri(String destination, String filename) {
        final File file = makeFile(destination, filename);

        Uri uri = FileProvider.getUriForFile(context, "com.UniqueBulleteSolutions.whatsapp.provider", file);
        return uri;
    }

    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    public File makeFile(String destination, String filename) {
        String root = Environment.getExternalStorageDirectory().toString();
        //  String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        if (!isStoragePermissionGranted())
            return null;

        File myDir = new File(root, destination);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, filename);


        //  By using this line you will be able to see saved images in the gallery view.
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file) );

        //      Toast.makeText(this, file.getPath() + "", Toast.LENGTH_SHORT).show();


        return file;
    }

    public void deleteFile(String destination, String filename) {
        final File file = makeFile(destination, filename);
        if (file.exists()) {
            file.delete();
            Toast.makeText(context, "file deleted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "file not exist", Toast.LENGTH_SHORT).show();
        }
    }

    public void setLayoutVisibility(TextView msg, int mv, ImageView image, int iv, LinearLayout layoutVoice, int lv, ConstraintLayout layoutVideo, int lvv, CardView layoutDocuments, int dv, LinearLayout layoutDocumentSize, int dsv) {
        msg.setVisibility(mv);
        image.setVisibility(iv);
        layoutVoice.setVisibility(lv);
        layoutVideo.setVisibility(lvv);
        layoutDocuments.setVisibility(dv);
        layoutDocumentSize.setVisibility(dsv);
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime, tv_duration, title, size, extn;
        ImageView receiverImage, playVideo, play, download, documentImage;
        ConstraintLayout layoutVideo;
        CardView documents;
        LinearLayout layoutVoice, layoutDocumentSize;
        SeekBar musicProgress;
        ProgressBar pb_image, pb_video, pb_voice, pb_document;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMsg = itemView.findViewById(R.id.tvReceiver);
            receiverTime = itemView.findViewById(R.id.tvReceiverTime);
            receiverImage = itemView.findViewById(R.id.attachImageReceiver);
            layoutVideo = itemView.findViewById(R.id.layout_video_receiver);
            layoutVoice = itemView.findViewById(R.id.layout_voice_receiver);
            play = itemView.findViewById(R.id.btn_play_audio_receiver);
            playVideo = itemView.findViewById(R.id.video_play);
            musicProgress = itemView.findViewById(R.id.sbProgress);
            tv_duration = itemView.findViewById(R.id.tv_time);
            documents = itemView.findViewById(R.id.layout_document_receiver);
            title = itemView.findViewById(R.id.tvDocument);
            download = itemView.findViewById(R.id.downloadDocument);
            layoutDocumentSize = itemView.findViewById(R.id.layout_document_size);
            size = itemView.findViewById(R.id.tvSize);
            extn = itemView.findViewById(R.id.tvExt);
            documentImage = itemView.findViewById(R.id.document_image);
            pb_document = itemView.findViewById(R.id.pb_document);
            pb_image = itemView.findViewById(R.id.pb_image);
            pb_video = itemView.findViewById(R.id.pb_video);
            pb_voice = itemView.findViewById(R.id.pb_voice);

        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime, tv_duration, title, size, extn;
        ImageView senderImage, playVideo, play, download, documentImage;
        ConstraintLayout layoutVideo;
        CardView documents;
        LinearLayout layoutVoice, layoutDocumentSize;
        ProgressBar pb_image, pb_video, pb_voice, pb_document;

        SeekBar musicProgress;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.tvSender);
            senderTime = itemView.findViewById(R.id.tvSenderTime);
            senderImage = itemView.findViewById(R.id.attachImageSender);
            playVideo = itemView.findViewById(R.id.video_play);
            layoutVideo = itemView.findViewById(R.id.layout_video_sender);
            layoutVoice = itemView.findViewById(R.id.layout_voice_sender);
            play = itemView.findViewById(R.id.btn_play_audio_sender);
            musicProgress = itemView.findViewById(R.id.sbProgress);
            tv_duration = itemView.findViewById(R.id.tv_time);
            documents = itemView.findViewById(R.id.layout_document_sender);
            title = itemView.findViewById(R.id.tvDocument);
            download = itemView.findViewById(R.id.downloadDocument);
            layoutDocumentSize = itemView.findViewById(R.id.layout_document_size);
            size = itemView.findViewById(R.id.tvSize);
            extn = itemView.findViewById(R.id.tvExt);
            documentImage = itemView.findViewById(R.id.document_image);
            pb_document = itemView.findViewById(R.id.pb_document);
            pb_image = itemView.findViewById(R.id.pb_image);
            pb_video = itemView.findViewById(R.id.pb_video);
            pb_voice = itemView.findViewById(R.id.pb_voice);


        }
    }


}
