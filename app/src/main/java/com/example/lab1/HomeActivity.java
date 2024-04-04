package com.example.lab1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.lab1.adapter.Recycle_Item_Fruits;
import com.example.lab1.databinding.ActivityHomeBinding;
import com.example.lab1.model.Fruit;
import com.example.lab1.model.Response;
import com.example.lab1.services.HttpRequest;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity implements Recycle_Item_Fruits.FruitClick{

    private ActivityHomeBinding binding;
    private HttpRequest httpRequest;
    private RecyclerView recyclerView;
    private ArrayList<Fruit> dsFruits;
    private Recycle_Item_Fruits adapter;
    private String token;
    private SharedPreferences sharedPreferences;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.rvFruit;
        dsFruits = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        httpRequest = new HttpRequest();
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);

        token = sharedPreferences.getString("token", "");
        httpRequest.CallApi().getListFruit("Bearer " + token).enqueue(getListFruitResponse);

        userListener();
    }
    private void userListener () {
        binding.btnAddFruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this , AddFruitActivity.class));
            }
        });
    }

    Callback<Response<ArrayList<Fruit>>> getListFruitResponse = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if(response.isSuccessful()){
                if (response.body().getStatus() == 200) {
                    ArrayList<Fruit> ds = response.body().getData();
                    getData(ds);
//                    Toast.makeText(HomeActivity.this, "hihi"+dsFruits, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
            t.printStackTrace();
        }
    };

    private void getData (ArrayList<Fruit> ds) {
        adapter = new Recycle_Item_Fruits(this, ds,this );
        binding.rvFruit.setAdapter(adapter);
    }

    @Override
    public void delete(Fruit fruit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setMessage("Bạn có chắc chắn muốn xóa không ?");
        builder.setIcon(R.drawable.baseline_warning_24).setTitle("Cảnh báo");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFruit(fruit.get_id());
            }
        }).setNegativeButton("Cancel",null).show();
    }

    private void deleteFruit(String id) {
        httpRequest.CallApi().deleteFruits(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    httpRequest.CallApi().getListFruit("Bearer " + token).enqueue(new Callback<Response<ArrayList<Fruit>>>() {
                        @Override
                        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
                            if (response.isSuccessful()) {
                                ArrayList<Fruit> newData = response.body().getData();
                                callData(newData);
                                Toast.makeText(HomeActivity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
                            // Xử lý khi gặp lỗi
                            Toast.makeText(HomeActivity.this, "Lỗi khi lấy danh sách quả trái sau khi xoá", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Xử lý khi xóa không thành công
                    Toast.makeText(HomeActivity.this, "Xoá không thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Xử lý khi gặp lỗi
                Toast.makeText(HomeActivity.this, "Lỗi khi xoá quả trái", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callData(ArrayList<Fruit> newData) {
        dsFruits.clear();
        dsFruits.addAll(newData);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void edit(Fruit fruit) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.CallApi().getListFruit("Bearer "+token).enqueue(getListFruitResponse);
    }
}