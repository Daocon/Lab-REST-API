package com.example.lab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.lab1.databinding.ActivitySignInWithPhoneBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignInWithPhoneActivity extends AppCompatActivity {

    private ActivitySignInWithPhoneBinding binding;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private String mVerificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInWithPhoneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(SignInWithPhoneActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationID = s;
                showCodeInputLayout();
            }
        };

        binding.btSend.setOnClickListener(view1 -> {
            String phoneNumber = binding.etPhone.getText().toString().trim();
            if (!TextUtils.isEmpty(phoneNumber)){
                sendVerificationCode(phoneNumber);
            } else {
                Toast.makeText(this, "Please fill in field", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = binding.etCodePhone.getText().toString().trim();
                if (!TextUtils.isEmpty(otp)) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, otp);
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(SignInWithPhoneActivity.this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInWithPhoneActivity.this, "Sign-in successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInWithPhoneActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(SignInWithPhoneActivity.this, "Sign-in failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationCode(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+84"+phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() != 9) {
            return false;
        }
        return true;
    }

    private void showCodeInputLayout() {
        binding.enterPhone.setVisibility(View.GONE);
        binding.enterCode.setVisibility(View.VISIBLE);
    }

}