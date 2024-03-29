package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lab1.adapter.Recycle_Item_Fruits;
import com.example.lab1.databinding.ActivityHomeBinding;
import com.example.lab1.model.Fruit;
import com.example.lab1.model.Response;
import com.example.lab1.services.HttpRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private HttpRequest httpRequest;
    private RecyclerView recyclerView;
    private ArrayList<Fruit> dsFruits;
    private Recycle_Item_Fruits recycle_item_fruits;
    private String token;
    private SharedPreferences sharedPreferences;

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

        binding.btnAddFruit.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, AddFruitActivity.class));
        });
    }

    Callback<Response<ArrayList<Fruit>>> getListFruitResponse = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if(response.isSuccessful()){
                if (response.body().getStatus() == 200) {
                    dsFruits = response.body().getData();
                    recycle_item_fruits = new Recycle_Item_Fruits(HomeActivity.this, dsFruits);
                    recyclerView.setAdapter(recycle_item_fruits);
//                    Toast.makeText(HomeActivity.this, "hihi"+dsFruits, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
            t.printStackTrace();
        }
    };
}