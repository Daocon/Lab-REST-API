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
import com.example.lab1.handle.Item_Distributor_Handle;
import com.example.lab1.model.Distributor;
import com.example.lab1.model.Response;
import com.example.lab1.services.HttpRequest;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


public class MainActivity extends AppCompatActivity implements Item_Distributor_Handle {

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

//        binding.btLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this, SignIn_Activity.class));
//                finish();
//            }
//        });

        mlistDis = new ArrayList<>();

        httpRequest = new HttpRequest();
        httpRequest.CallApi().getListDistributor().enqueue(getListDistributor);
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
    //get data
    public void getData(ArrayList<Distributor> mlistDis){
        adapter = new Recycle_Item_Distributors(this, mlistDis, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvDis.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rvDis.addItemDecoration(dividerItemDecoration);
        binding.rvDis.setAdapter(adapter);
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
        httpRequest.CallApi().searchDistributor(key).enqueue(getListDistributor);
    }

    Callback<Response<Distributor>> responseDistributorAPI = new Callback<Response<Distributor>>() {
        @Override
        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
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
                    getData(mlistDis);
//                    Toast.makeText(MainActivity.this,"Loi:"+ response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
            Log.d(">>>> Distributor", "onFailure: " + t.getMessage());
        }
    };

    @Override
    public void Delete(String id) {
        httpRequest.CallApi().deleteDistributorById(id).enqueue(responseDistributorAPI);
    }

    @Override
    public void Update(String id, Distributor distributor) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_update);
        dialog.setTitle("Sửa mục mới");

        final EditText editText = dialog.findViewById(R.id.edtTitle);
        Button updateButton = dialog.findViewById(R.id.buttonUpdate);
        editText.setText(distributor.getName());
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString().trim();
                if (!content.isEmpty()) {
                    distributor.setName(content);
                    httpRequest.CallApi().updateDistributorById(id, distributor).enqueue(responseDistributorAPI);
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Nội dung không được để trống", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}