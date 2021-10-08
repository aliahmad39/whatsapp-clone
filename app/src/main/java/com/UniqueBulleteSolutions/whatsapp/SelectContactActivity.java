package com.UniqueBulleteSolutions.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class SelectContactActivity extends AppCompatActivity {

    ListView lv;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        lv = findViewById(R.id.listConatct);
        btn = findViewById(R.id.btngetContact);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(SelectContactActivity.this)
                        .withPermission(Manifest.permission.READ_CONTACTS)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                getContact();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                  permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });







    }

    public void getContact(){
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,null , null , null , null);
        startManagingCursor(cursor);
        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME ,
       ContactsContract.CommonDataKinds.Phone.NUMBER , ContactsContract.CommonDataKinds.Phone._ID };

        int[] to = {android.R.id.text1 ,android.R.id.text2 };

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this , android.R.layout.simple_list_item_2,cursor , from ,to);
        lv.setAdapter(simpleCursorAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SelectContactActivity.this, position+"", Toast.LENGTH_SHORT).show();
            }
        });

    }
}