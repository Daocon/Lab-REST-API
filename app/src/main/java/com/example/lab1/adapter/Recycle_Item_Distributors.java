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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1.MainActivity;
import com.example.lab1.R;
import com.example.lab1.handle.Item_Distributor_Handle;
import com.example.lab1.model.Distributor;
import com.example.lab1.model.Response;
import com.example.lab1.services.HttpRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class Recycle_Item_Distributors extends RecyclerView.Adapter<Recycle_Item_Distributors.UserViewHolder>
{
    private final ArrayList<Distributor> dsdistributors;
    private Context context;
    private HttpRequest httpRequest;
    private Item_Distributor_Handle handle;
    private Distributor distributor;
    public Recycle_Item_Distributors(Context context,ArrayList<Distributor> dsdistributors, Item_Distributor_Handle handle)
    {
        this.context = context;
        this.dsdistributors = dsdistributors;
        this.httpRequest = new HttpRequest();
        this.handle = handle;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dis, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        distributor = dsdistributors.get(position);
        if (distributor == null)
        {
            return;
        }
        holder.tvId.setText(distributor.getId());
        holder.tvTitle.setText(distributor.getName());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete this item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handle.Delete(dsdistributors.get(position).getId());
                        Toast.makeText(context, "Id"+ distributor.getId(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
//                Toast.makeText(context, "id"+dsdistributors.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle.Update(dsdistributors.get(position).getId(),dsdistributors.get(position));
//                Toast.makeText(context, "Name:"+distributor, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        if (dsdistributors != null)
        {
            return dsdistributors.size();
        }
        return 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tvId;
        private final TextView tvTitle;
        private final Button btnDelete;
        private final Button btnUpdate;
        public UserViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            btnDelete = itemView.findViewById(R.id.btDelete);
            btnUpdate = itemView.findViewById(R.id.btUpdate);
        }
    }
}
