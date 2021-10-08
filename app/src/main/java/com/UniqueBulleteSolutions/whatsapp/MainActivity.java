package com.UniqueBulleteSolutions.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.widget.Toast;

import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityMainBinding;
import com.UniqueBulleteSolutions.whatsapp.Adapter.FragmentAdapter;
import com.UniqueBulleteSolutions.whatsapp.dialog.DialogPreviewSendImage;
import com.UniqueBulleteSolutions.whatsapp.fragment.ChatFragment;
import com.UniqueBulleteSolutions.whatsapp.fragment.StatusFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private Toolbar mtoolbar;
    ViewPager viewPager;
    TabLayout tablayout;
    TabItem tabItem1, tabItem2, tabItem3;
    FloatingActionButton fab;

    private AlertDialog.Builder dialogueBuilder;
    private AlertDialog dialog;
    public static int pos;
    ImageView search, option;
    FragmentAdapter pageAdapter;



    private static String CUID;
    private static String CUN = "";
    private static String CUP = "";
    private static String CUI = "";

    public static String getCUI() {
        return CUI;
    }

    public static String getCUN() {
        return CUN;
    }

    public static void setCUN(String CUN) {
        MainActivity.CUN = CUN;
    }

    public static String getCUP() {
        return CUP;
    }

    public static void setCUP(String CUP) {
        MainActivity.CUP = CUP;
    }

    public static String getCUID() {
        return CUID;

    }

    public static void setCUID(String CUID) {
        MainActivity.CUID = CUID;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("WhatsApp");

        fab = findViewById(R.id.fab);


      checkexitenence();


        viewPager = findViewById(R.id.viewpager);
        tablayout = findViewById(R.id.tablayout);
        tabItem1 = findViewById(R.id.tab1);
        tabItem2 = findViewById(R.id.tab2);
        tabItem3 = findViewById(R.id.tab3);

        pageAdapter = new FragmentAdapter(getSupportFragmentManager(), tablayout.getTabCount(), CUID);

        viewPager.setAdapter(pageAdapter);

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());


                if (tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 2)
                    pageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));


    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }





    private void checkexitenence(){
        SharedPreferences sp = getSharedPreferences("UserCredentials" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(!sp.contains("CUP")){
            Intent intent = new Intent(this , PhoneNumberActivity.class);
            startActivity(intent);
        }else{
            CUID = sp.getString("CUID" , "0");
            CUP = sp.getString("CUP" , "0");


        }
    }


}