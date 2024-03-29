package com.example.lab1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lab1.databinding.ActivityRegisterBinding;
import com.example.lab1.model.Distributor;
import com.example.lab1.model.User;
import com.example.lab1.services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import com.example.lab1.model.Response;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private HttpRequest httpRequest;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        httpRequest = new HttpRequest();

        binding.ivAvartar.setOnClickListener(view -> {
            chooseImage();
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validate
                if (binding.etUsername.getText().toString().isEmpty() || binding.etPassword.getText().toString().isEmpty() || binding.etEmail.getText().toString().isEmpty() || binding.etName.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                //validate email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
                    Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                //validate password
                if (binding.etPassword.getText().toString().length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                //validate image
                if (file == null) {
                    Toast.makeText(RegisterActivity.this, "Please choose an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), binding.etUsername.getText().toString());
                RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), binding.etPassword.getText().toString());
                RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), binding.etEmail.getText().toString());
                RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), binding.etName.getText().toString());

                MultipartBody.Part multipartBody;
                if (file != null) {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                    multipartBody = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
                } else {
                    multipartBody = null;
                }
                httpRequest.CallApi().register(_username, _password, _email, _name, multipartBody).enqueue(responseUser);
            }
        });

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    Callback<Response<User>> responseUser = new Callback<Response<User>>() {
        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if(response.isSuccessful()){
                if (response.body().getStatus() == 200) {
                    Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Register fail", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable t) {
            Log.e("Errorrrrrrrrrrrrrr", t.getMessage());
        }
    };

    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getImage.launch(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == Activity.RESULT_OK) {
                Intent data = o.getData();
                Uri uri = data.getData();
                file = createFileFromURI(uri, "avatar");
                Glide.with(RegisterActivity.this)
                        .load(file)
                        .thumbnail(Glide.with(RegisterActivity.this).load(R.drawable.loading))
                        .centerCrop()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.ivAvartar);
            }
        }
    });

    private File createFileFromURI(Uri uri, String name) {
        File file = new File(RegisterActivity.this.getCacheDir(), name+".png");
        try {
            InputStream inputStream = RegisterActivity.this.getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}