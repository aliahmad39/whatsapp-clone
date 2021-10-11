package com.UniqueBulleteSolutions.whatsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.UniqueBulleteSolutions.whatsapp.Api.ApiClient;
import com.UniqueBulleteSolutions.whatsapp.Api.ApiInterface;
import com.UniqueBulleteSolutions.whatsapp.ApiResponse.UserResponse;
import com.UniqueBulleteSolutions.whatsapp.databinding.ActivityOTPBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.OnOtpCompletionListener;


import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OTPActivity extends AppCompatActivity {
    ActivityOTPBinding binding;
    FirebaseAuth auth;
    String verificationId;
    ProgressDialog dialog;
    String phoneNumber;
    FirebaseDatabase database;
    String cuid;

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().hide();
        dialog =new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        dialog.show();

        Retrofit retrofit = ApiClient.getClient();
        apiInterface =retrofit.create(ApiInterface.class);

        auth= FirebaseAuth.getInstance();
        Intent intent=getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");

        binding.tvPhoneNumber.setText("Verify " + phoneNumber);

        PhoneAuthOptions options= PhoneAuthOptions.newBuilder(auth).
                setPhoneNumber(phoneNumber).
                setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPActivity.this,"otp :"+ e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);

                        dialog.dismiss();
                        verificationId = verifyId;

                        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        binding.otpView.requestFocus();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationId,otp);

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {

                           // Toast.makeText(OTPActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
//                             String id =  String.valueOf(task.getResult().getAdditionalUserInfo().getProviderId());
//                             String cred =String.valueOf(task.getResult().getCredential());
//

//                            Toast.makeText(OTPActivity.this, "prvd id :"+verificationId, Toast.LENGTH_SHORT).show();

                           // uuid = UUID.randomUUID();
                           // long calendar = Calendar.getInstance().getTimeInMillis();
                            cuid = String.valueOf(SystemClock.currentThreadTimeMillis());

                           callApi();
//                            Intent intent =new Intent(OTPActivity.this, MainActivity.class);
//                            startActivity(intent);
//
//                            finishAffinity();
                        }
                        else
                        {
                            Toast.makeText(OTPActivity.this,"otp :"+ task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void callApi( ) {
        Toast.makeText(OTPActivity.this, "random id :"+cuid, Toast.LENGTH_SHORT).show();
        apiInterface.createUser( cuid, phoneNumber ).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
               // progressDialog.dismiss();
                try {
                    if (response != null) {
                        if(response.body().getStatus().equals("1")) {
                            Toast.makeText(OTPActivity.this,"otp :"+ response.body().getMessage(), Toast.LENGTH_SHORT).show();

                          saveData(cuid , phoneNumber);
                        }
                        else{
                            Toast.makeText(OTPActivity.this,"otp :"+ response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(Exception e){
                    Log.e("exp", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(OTPActivity.this, "error otp:"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
               // progressDialog.dismiss();
                Log.e("failure", t.getLocalizedMessage());
            }
        });
    }

    private void saveData(String id , String phn){
        SharedPreferences sp = getSharedPreferences("UserCredentials" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("CUID" , id);
        editor.putString("CUP" , phn);
        editor.commit();
       //editor.apply();


        Intent intent = new Intent(OTPActivity.this, MainActivity.class);
        intent.putExtra("currentUID", sp.getString("CUID" , "0"));
        // intent.putExtra("currentUNM", response.body().getName());
        intent.putExtra("currentUPhn", sp.getString("CUP" , "0"));
        // intent.putExtra("currentUPic", response.body().getUserPic());
        startActivity(intent);
    }
}