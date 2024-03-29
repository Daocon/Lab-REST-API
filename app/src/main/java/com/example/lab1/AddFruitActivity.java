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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.lab1.adapter.Recycle_Item_Distributors;
import com.example.lab1.adapter.Recycle_Item_Fruits;
import com.example.lab1.adapter.Recycle_Item_Image;
import com.example.lab1.databinding.ActivityAddFruitBinding;
import com.example.lab1.model.Distributor;
import com.example.lab1.model.Fruit;
import com.example.lab1.model.Response;
import com.example.lab1.services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AddFruitActivity extends AppCompatActivity {

    private ActivityAddFruitBinding binding;

    private HttpRequest httpRequest;

    private Recycle_Item_Image adapter;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFruitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String refreshToken = sharedPreferences.getString("refreshToken", "");
        id = sharedPreferences.getString("id", "");
        Log.d(">>>> Token", "onCreate: " + token);
        Log.d(">>>> RefreshToken", "onCreate: " + refreshToken);
        Log.d(">>>> ID", "onCreate: " + id);

        httpRequest = new HttpRequest();
//        httpRequest.CallApi().getListDistributor().enqueue();


        binding.btnAdd.setOnClickListener(view -> {
            //validate
            if (binding.etName.getText().toString().trim().isEmpty() || binding.etPrice.getText().toString().trim().isEmpty()) {
                Toast.makeText(AddFruitActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, RequestBody> mapRequestBody = new HashMap<>();
            String _name = binding.etName.getText().toString().trim();
            String _price = binding.etPrice.getText().toString().trim();
            String _quantity = binding.etQuantity.getText().toString().trim();
            String _description = binding.etDescription.getText().toString().trim();
            String _status = binding.etStatus.getText().toString().trim();

            //put request body
            mapRequestBody.put("name", getRequestBody(_name));
            mapRequestBody.put("price", getRequestBody(_price));
            mapRequestBody.put("quantity", getRequestBody(_quantity));
            mapRequestBody.put("description", getRequestBody(_description));
            mapRequestBody.put("status", getRequestBody(_status));
            mapRequestBody.put("distributor", getRequestBody(id));

            ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
            _ds_image.forEach(file -> {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file.toString());
//                MultipartBody.Part body = MultipartBody.Part.createFormData("image", , requestFile);
//                _ds_image.add(body);
            });

        });
    }

    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }



    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE, true);
            getImage.launch(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode()== Activity.RESULT_OK){
                Intent intent = o.getData();
                if (intent.getClipData() != null){
                    int count = intent.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count){
                        Uri imageUri = intent.getClipData().getItemAt(currentItem).getUri();
                        currentItem = currentItem + 1;
                        File file = createFileFromURI(imageUri, "avatar");

                    }
//                    setViewListHinh();
                }
            }
        }
    });

    private void setViewListHinh(ArrayList<File> files) {
//        adapter = new Recycle_Item_Fruits(AddFruitActivity.this, files);

    }

    private File createFileFromURI(Uri uri, String name) {
        File file = new File(AddFruitActivity.this.getCacheDir(), name+".png");
        try {
            InputStream inputStream = AddFruitActivity.this.getContentResolver().openInputStream(uri);
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