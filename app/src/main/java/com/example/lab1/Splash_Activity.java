package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash_Activity.this, LoginActivity.class));
            }
        }, 2000);
    }

//    private void nextActivity() {
//        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null){
//            Intent intent = new Intent(Splash_Activity.this, SignIn_Activity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Intent intent = new Intent(Splash_Activity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
}