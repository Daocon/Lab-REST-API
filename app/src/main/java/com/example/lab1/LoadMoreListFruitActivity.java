package com.example.lab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lab1.adapter.Recycle_Item_Fruits;
import com.example.lab1.databinding.ActivityLoadMoreListFruitBinding;
import com.example.lab1.model.Fruit;
import com.example.lab1.model.Page;
import com.example.lab1.model.Response;
import com.example.lab1.services.HttpRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class LoadMoreListFruitActivity extends AppCompatActivity implements Recycle_Item_Fruits.FruitClick {

    private ActivityLoadMoreListFruitBinding binding;
    private ProgressBar loadmore;
    private int page = 1;
    private int totalPage = 0;
    private NestedScrollView nestedScrollView;
    private HttpRequest httpRequest;
    String token, refreshToken, id;
    private Recycle_Item_Fruits adapter;
    private RecyclerView recyclerView;
    private ArrayList<Fruit> dsFruits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadMoreListFruitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        httpRequest = new HttpRequest();
        loadmore = binding.pbLoadMore;

        recyclerView = binding.rvFruit;
        dsFruits = new ArrayList<>();
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);

        SharedPreferences sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        refreshToken = sharedPreferences.getString("refreshToken", "");
        id = sharedPreferences.getString("id", "");

        Log.d("token", token);

        nestedScrollView = binding.nestedScrollView;
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (totalPage == page) return;
                    if (loadmore.getVisibility() == View.GONE) {
                        loadmore.setVisibility(View.VISIBLE);
                        page++;
                        httpRequest.CallApi().getPageFruit("Bearer" + token, page).enqueue(getListFruitResponseCallback);
                    }
                }

            }
        });
    }


    private void getData(ArrayList<Fruit> _ds) {
        if (loadmore.getVisibility() == View.VISIBLE){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(dsFruits.size() - 1);
                    loadmore.setVisibility(View.GONE);
                    dsFruits.addAll(_ds);
                    adapter.notifyDataSetChanged();
                }
            }, 1000);
            return;
        }
        dsFruits.addAll(_ds);
        adapter = new Recycle_Item_Fruits(this, dsFruits, LoadMoreListFruitActivity.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.CallApi().getPageFruit("Bearer" + token,page).enqueue(getListFruitResponseCallback);
    }

    Callback<Response<Page<ArrayList<Fruit>>>>getListFruitResponseCallback = new Callback<Response<Page<ArrayList<Fruit>>>>() {
        @Override
        public void onResponse(Call<Response<Page<ArrayList<Fruit>>>> call, retrofit2.Response<Response<Page<ArrayList<Fruit>>>> response) {
            if (response.isSuccessful()){
                if (response.body().getStatus() == 200) {
                    //set total page
                    totalPage = response.body().getData().getTotalPage();
                    //lay data
                    ArrayList<Fruit> fruits = response.body().getData().getData();
                    // set du lieu
                    getData(fruits);
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Page<ArrayList<Fruit>>>> call, Throwable t) {
            // show error
            Toast.makeText(LoadMoreListFruitActivity.this, "Loi", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void delete(Fruit fruit) {

    }

    @Override
    public void edit(Fruit fruit) {

    }
}