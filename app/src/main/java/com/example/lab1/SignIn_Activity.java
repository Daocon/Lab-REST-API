package com.example.lab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.lab1.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        binding.tvSignIn.setOnClickListener(v -> startActivity(new
                Intent(SignIn_Activity.this, SignUp_Activity.class)));

        binding.btSignIn.setOnClickListener(view1 -> signIn());

        binding.tvForgotpass.setOnClickListener(view12 -> startActivity(new Intent(SignIn_Activity.this, ForgotPasswordActivity.class)));

        binding.btPhone.setOnClickListener(view13 -> startActivity(new Intent(SignIn_Activity.this, SignInWithPhoneActivity.class)));
    }

    private void signIn() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignIn_Activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidEmail(email)) {
            Toast.makeText(SignIn_Activity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(SignIn_Activity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(SignIn_Activity.this, "Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignIn_Activity.this, MainActivity.class));
            } else {
                Toast.makeText(SignIn_Activity.this, "Fail to sign in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}