package com.UniqueBulleteSolutions.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;



public class PhoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        Toast.makeText(this, "phone Number Activity", Toast.LENGTH_SHORT).show();

        binding.etPhoneNumber.requestFocus();
        auth= FirebaseAuth.getInstance();

        binding.ccp.registerCarrierNumberEditText(binding.etPhoneNumber);

        if(auth.getCurrentUser() != null)
        {
            Intent intent=new Intent(PhoneNumberActivity.this, MainActivity.class);
            startActivity(intent);
        }
        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhoneNumberActivity.this, OTPActivity.class);
                intent.putExtra("phoneNumber",binding.ccp.getFullNumberWithPlus().trim());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}