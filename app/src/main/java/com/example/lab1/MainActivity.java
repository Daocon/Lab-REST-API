package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab1.adapter.Recycle_Item_Distributors;
import com.example.lab1.databinding.ActivityMainBinding;
import com.example.lab1.databinding.ActivitySignInBinding;
import com.example.lab1.model.Distributor;
import com.example.lab1.model.Response;
import com.example.lab1.services.HttpRequest;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private HttpRequest httpRequest;
    private Recycle_Item_Distributors adapter;
    private ArrayList<Distributor> mlistDis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SignIn_Activity.class));
                finish();
            }
        });
        recyclerView = binding.rvDis;
        mlistDis = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        httpRequest = new HttpRequest();
        httpRequest.CallApi().getListDistributor().enqueue(getListDistributor);
//        Toast.makeText(this, "Data", Toast.LENGTH_SHORT).show();

        binding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Thực hiện tìm kiếm
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        binding.btAdd.setOnClickListener(view1 -> openDialog());

    }

    private void openDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add);
        dialog.setTitle("Thêm mục mới");

        final EditText editText = dialog.findViewById(R.id.edtTitle);
        Button addButton = dialog.findViewById(R.id.buttonAdd);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString().trim();
                if (!content.isEmpty()) {
                    Distributor distributor = new Distributor();
                    distributor.setName(content);
                    httpRequest.CallApi().addDistributor(distributor).enqueue(responseDistributorAPI);
//                    Toast.makeText(MainActivity.this, "Nội dung: " + content, Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Đóng dialog sau khi thêm thành công
                } else {
                    Toast.makeText(MainActivity.this, "Nội dung không được để trống", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void performSearch() {
        String key = binding.edtSearch.getText().toString();
//        Toast.makeText(MainActivity.this, "Tìm: " + key, Toast.LENGTH_SHORT).show();
        // Gọi API để tìm kiếm nhà phân phối
        httpRequest.CallApi().searchDistributor(key).enqueue(getListDistributor);
    }

    Callback<Response<Distributor>> responseDistributorAPI = new Callback<Response<Distributor>>() {
        @Override
        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    // Sau khi thêm thành công, gọi lại API để cập nhật danh sách nhà phân phối
                    httpRequest.CallApi().getListDistributor().enqueue(getListDistributor);
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Distributor>> call, Throwable t) {
            Log.d(">>>> Distributor", "onFailure: " + t.getMessage());
        }
    };


    Callback<Response<ArrayList<Distributor>>> getListDistributor = new Callback<Response<ArrayList<Distributor>>>() {

        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()){
                if (response.body().getStatus() == 200){
                    mlistDis = response.body().getData();
                    adapter = new Recycle_Item_Distributors(MainActivity.this, mlistDis);
                    recyclerView.setAdapter(adapter);
//                    Toast.makeText(MainActivity.this,"Loi:"+ response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
            Log.d(">>>> Distributor", "onFailure: " + t.getMessage());
        }
    };
}