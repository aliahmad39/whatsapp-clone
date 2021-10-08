package com.UniqueBulleteSolutions.whatsapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.Adapter.TopStatusAdapter;
import com.UniqueBulleteSolutions.whatsapp.Adapter.UserAdapter;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.ChatDetailActivity;
import com.UniqueBulleteSolutions.whatsapp.MainActivity;
import com.UniqueBulleteSolutions.whatsapp.MakeCallActivity;
import com.UniqueBulleteSolutions.whatsapp.Models.Status;
import com.UniqueBulleteSolutions.whatsapp.Models.Users;
import com.UniqueBulleteSolutions.whatsapp.Models.UsersStatus;
import com.UniqueBulleteSolutions.whatsapp.R;
import com.UniqueBulleteSolutions.whatsapp.SettingActivity;
import com.UniqueBulleteSolutions.whatsapp.databinding.FragmentStatusBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class StatusFragment extends Fragment {
    Bitmap bitmap;
    String encodedImage;
    public static final String UPLOAD_URL = ApiClient.BASE_URL + "ApiAuthentication/updateProfile.php";
    private static final int STORAGE_PERMISSION_CODE = 4655;
    FragmentStatusBinding binding;
    ProgressDialog progressDialog;
    int count = 0;
    StatusInterface listener;
    public static int pos = 0;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private String size;
    private String videoPath;
    ArrayList<String> number = new ArrayList<>();
    private static ArrayList<Users> contacts = new ArrayList<>();


    List<Users> userlist = new ArrayList<>();

    HashSet<String> set = new HashSet<String>();


    ArrayList<Users> list2 = new ArrayList<>();

    Uri sFile;
    ApiInterface apiInterface;

    String CUID = "";
    String CUN = "";
    String CUP = "";
    String CUI = "";

    public StatusFragment() {
        // Required empty public constructor
    }


    public interface StatusInterface {
        public void callStatus();
    }


    UserAdapter usersAdapter;
    TopStatusAdapter topStatusAdapter;
    private ArrayList<UsersStatus> usersStatuses = new ArrayList<>();
    ArrayList<Users> UserList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(inflater, container, false);


        CUID = MainActivity.getCUID();
        changeFabicon();
        Dexter.withActivity((Activity) getContext())
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getContact();
                        //      getContact();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
        getServerStatus();



        binding.selectStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadStatus();

            }
        });


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(false);


        if (pos == 1) {
            UploadStatus();
        }

       loadData();
        topStatusAdapter = new TopStatusAdapter(getContext(), usersStatuses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(topStatusAdapter);


//        binding.statusList.showShimmerAdapter();




        return binding.getRoot();
    }
    private void getServerStatus(){
        apiInterface.getStatuses().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response != null) {
                    if (response.body().getStatus().equals("1")) {
                        usersStatuses.clear();
                        List<Status> list = response.body().getListstatus();

                        set.clear();

                        for (int i = 0; i < list.size(); i++) {
                            Status status = list.get(i);
                            String id1 = status.getSender_id();

                            set.add(id1);

                        }


                        for (String id : set) {
                            UsersStatus status1 = new UsersStatus();

                            Log.v("STATUS", "set id is :" + id);
                            ArrayList<Status> statuses = new ArrayList<>();

                            for (Status status : list) {

                                Log.v("STATUS", "list id:" + status.getSender_id());
                                if (status.getSender_id().equals(id)) {

                                    status1.setName(status.getPhoneNo());
                                    Log.v("STATUS", "id match " + status.getSender_id());
                                    if (number.contains(status.getPhoneNo())) {
                                        for (Users users1 : contacts) {
                                            if (status.getPhoneNo().equals(users1.getPhoneNo())) {
                                                status1.setName(users1.getName());
                                                break;
                                            }
                                        }
                                    }


                                    status1.setProfileImage(status.getUserPic());
                                    statuses.add(status);
                                }
                            }
                            status1.setStatuses(statuses);
                            usersStatuses.add(status1);
                        }

//
                        binding.statusList.hideShimmerAdapter();
                        topStatusAdapter.notifyDataSetChanged();
                        saveData();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void UploadStatus() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/* video/*");
        String[] mimetypes = {"image/*", "video/*"};
        //   pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(pickIntent, 45);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 45 && data != null && data.getData() != null) {
            sFile = data.getData();
            if (sFile.toString().contains("image")) {
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(sFile);
                    bitmap = BitmapFactory.decodeStream(inputStream);

                    imageStore(bitmap);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(), "image select", Toast.LENGTH_SHORT).show();
                //handle image
            } else if (sFile.toString().contains("video")) {
                Toast.makeText(getContext(), "video select", Toast.LENGTH_SHORT).show();
                //handle video

                // it will return extension of any selected file
                String extension = getMimeType(getContext(), sFile);

                //  String imagePath = getPath(sFile);
                //    File file = new File(imagePath);

//it will return type of selected media e.g video , audio , msword , pdf etc
                String mimeType = getContext().getContentResolver().getType(sFile);

                /*
                 * Get the file's content URI from the incoming Intent,
                 * then query the server app to get the file's display name
                 * and size.
                 */

                Cursor returnCursor =
                        getContext().getContentResolver().query(sFile, null, null, null, null);
                /*
                 * Get the column indexes of the data in the Cursor,
                 * move to the first row in the Cursor, get the data,
                 * and display it.
                 */
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();

                String name = returnCursor.getString(nameIndex);
                double input = returnCursor.getLong(sizeIndex);

                if (input >= 1000 && input < 1000000) {
                    double ans = input / 1000;
                    size = df2.format(ans) + "kb";

                } else if (input >= 1000000 && input < 1000000000) {
                    double ans = input / 1000000;
                    size = df2.format(ans) + "Mb";
                } else if (input >= 1000000000) {
                    double ans = input / 1000000000;
                    size = df2.format(ans) + "Gb";
                }


                Toast.makeText(getContext(), "type :" + mimeType, Toast.LENGTH_SHORT).show();
                try {

                    InputStream iStream = null;
                    iStream = getContext().getContentResolver().openInputStream(sFile);
                    final byte[] inputData = getBytes(iStream);

                    encodedImage = android.util.Base64.encodeToString(inputData, android.util.Base64.DEFAULT);
                     callApi("mp4");

                  //  uploadDocument("document", videoPath, extension, size, name);

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

        callApi("jpg");
    }

    private void callApi(String ext) {
        apiInterface.createStatus(CUID, ext, encodedImage).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response != null) {
                        if (response.body().getStatus().equals("1")) {
                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("exp", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("failure", t.getLocalizedMessage());
            }
        });
    }

    private ArrayList<Users> getUsersList() {
        apiInterface.getUsers().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response != null) {
                    if (response.body().getStatus().equals("1")) {
                        userlist = response.body().getData();
                        list2.addAll(userlist);

                        Toast.makeText(getContext(), "size of users :" + userlist.size(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
        return list2;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.status_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.status_search:
                Toast.makeText(getContext(), "status_search", Toast.LENGTH_SHORT).show();
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        topStatusAdapter.getFilter().filter(newText);
                        return false;
                    }
                });


                break;

            case R.id.status_privacy:
                Toast.makeText(getContext(), "status_privacy", Toast.LENGTH_SHORT).show();
                break;

            case R.id.status_setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                Toast.makeText(getContext(), "status_setting", Toast.LENGTH_SHORT).show();
                break;

        }


        return super.onOptionsItemSelected(item);
    }


    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void saveData(){
        SharedPreferences sp = getContext().getSharedPreferences("aliApp" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(usersStatuses);
        editor.putString("userStatus" , json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sp = getContext().getSharedPreferences("aliApp" , Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("userStatus" , null);
        Type type = new TypeToken<ArrayList<UsersStatus>>() {}.getType();

        usersStatuses = gson.fromJson(json , type);
        //  adapter.notifyDataSetChanged();
        //  binding.chatRecyclerView.hideShimmerAdapter();

        if(usersStatuses == null){
            usersStatuses = new ArrayList<>();
        }
    }

    private void getContact() {
        //  Toast.makeText(this, "getContact()", Toast.LENGTH_SHORT).show();
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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
    private void changeFabicon() {
        //  binding.fab.hide();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //    binding.fab.show();
                binding.fab.setImageResource(R.drawable.ic_camera);

                binding.fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UploadStatus();
                    }
                });


            }
        }, 400);

    }

}