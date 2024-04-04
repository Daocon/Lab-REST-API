package com.example.lab1.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab1.R;
import com.example.lab1.model.Distributor;
import com.example.lab1.model.Fruit;
import com.example.lab1.model.Response;
import com.example.lab1.services.HttpRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class Recycle_Item_Fruits extends RecyclerView.Adapter<Recycle_Item_Fruits.UserViewHolder>
{
    private final ArrayList<Fruit> list;
    private Context context;
    private HttpRequest httpRequest;
    private Fruit fruit;

    private FruitClick fruitClick;
    public Recycle_Item_Fruits(Context context, ArrayList<Fruit> list,FruitClick fruitClick)
    {
        this.context = context;
        this.list = list;
        this.fruitClick = fruitClick;
    }

    public interface FruitClick {
        void delete(Fruit fruit);
        void edit(Fruit fruit);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fruit, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        fruit = list.get(position);
        if (fruit == null)
        {
            return;
        }
        holder.tv_name.setText(fruit.getName());
        holder.tv_quantity.setText(fruit.getQuantity());
        holder.tv_price.setText(fruit.getPrice());
        holder.tv_description.setText(fruit.getDescription());
        if (!fruit.getImage().isEmpty()) { // Kiểm tra danh sách hình ảnh không rỗng
            String url = fruit.getImage().get(0);
            String newUrl = url.replace("localhost", "10.0.2.2");
            Glide.with(context)
                    .load(newUrl)
                    .thumbnail(Glide.with(context).load(R.drawable.loading))
                    .into(holder.iv_fruit);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fruitClick.delete(fruit);
                list.remove(fruit);
//                Toast.makeText(context, "id"+dsdistributors.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Name:"+distributor, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        if (list != null)
        {
            return list.size();
        }
        return 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tv_Id;
        private final TextView tv_name;
        private final TextView tv_quantity;
        private final TextView tv_price;
        private final TextView tv_status;
        private final TextView tv_description;
        private final TextView tv_distributor;
        private final ImageView iv_fruit;
        private final Button btnDelete;
        private final Button btnUpdate;
        public UserViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tv_Id = itemView.findViewById(R.id.tv_Id);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_distributor = itemView.findViewById(R.id.tv_distributor);
            btnDelete = itemView.findViewById(R.id.btDelete);
            btnUpdate = itemView.findViewById(R.id.btUpdate);
            iv_fruit = itemView.findViewById(R.id.iv_fruit);
        }
    }

}
