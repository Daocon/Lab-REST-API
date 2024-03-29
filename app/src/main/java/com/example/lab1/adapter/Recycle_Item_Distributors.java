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
    private Distributor distributor;
    public Recycle_Item_Distributors(Context context,ArrayList<Distributor> dsdistributors)
    {
        this.context = context;
        this.dsdistributors = dsdistributors;
        this.httpRequest = new HttpRequest();
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
//                Toast.makeText(context, "id"+dsdistributors.get(position), Toast.LENGTH_SHORT).show();
                showDeleteConfirmationDialog(dsdistributors.get(position));
            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(dsdistributors.get(position));
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

    private void showUpdateDialog(Distributor distributor) {
//        Toast.makeText(context, "Id:"+ id + "name:"+name, Toast.LENGTH_SHORT).show();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_update);
        dialog.setTitle("Sửa mục mới");

        final EditText editText = dialog.findViewById(R.id.edtTitle);
        Button updateButton = dialog.findViewById(R.id.buttonUpdate);
        String id = distributor.getId();
        editText.setText(distributor.getName());
//        distributor.setName(editText.getText().toString().trim());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString().trim();
                if (!content.isEmpty()) {
                    distributor.setName(content);
                    httpRequest.CallApi().updateDistributorById(id, distributor).enqueue(new Callback<Response<Distributor>>() {
                        @Override
                        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus() == 200) {
                                    Toast.makeText(context, "Sửa thành công", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                    reloadDistributorData();
                                    dialog.dismiss(); // Đóng dialog sau khi thêm thành công
                                }
                                else {
                                    Toast.makeText(context, "Sửa thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response<Distributor>> call, Throwable t) {
                            Log.d(">>>> Distributor", "onFailure: " + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(context, "Nội dung không được để trống", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void showDeleteConfirmationDialog(Distributor distributor) {
//        Toast.makeText(context, "id: "+ distributor.getId(), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa không?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = distributor.getId();
                deleteItem(id);
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void deleteItem(String id) {
//        Toast.makeText(context, "id" + id, Toast.LENGTH_SHORT).show();
        httpRequest = new HttpRequest();
        httpRequest.CallApi().deleteDistributorById(id).enqueue(new Callback<Response<Distributor>>() {
            @Override
            public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        // Xóa phần tử khỏi danh sách
                        int position = findPositionById(id);
                        Toast.makeText(context, "id: "+ id, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(context, "po:"+ position, Toast.LENGTH_SHORT).show();
                        if (position != -1) {
                            dsdistributors.remove(position);
                            notifyDataSetChanged(); // Cập nhật giao diện
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            reloadDistributorData();
//                            Toast.makeText(context, "ds: "+dsdistributors, Toast.LENGTH_SHORT).show();
                        }
                        // Sau khi xóa thành công, gọi lại API để tải lại dữ liệu mới


                    }
                    else {
                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<Distributor>> call, Throwable t) {
                Log.d(">>>> Distributor", "onFailure: " + t.getMessage());
            }
        });
    }

    // Phương thức để gọi lại API và tải lại dữ liệu mới
    private void reloadDistributorData() {
        httpRequest = new HttpRequest();
        httpRequest.CallApi().getListDistributor().enqueue(new Callback<Response<ArrayList<Distributor>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Distributor> newDistributors = response.body().getData();
                    if (newDistributors != null) {
                        // Cập nhật danh sách mới
                        dsdistributors.clear();
                        dsdistributors.addAll(newDistributors);
                        notifyDataSetChanged(); // Cập nhật giao diện

                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
                Log.d(">>>> Distributor", "onFailure: " + t.getMessage());
            }
        });
    }
    private int findPositionById(String id) {
        for (int i = 0; i < dsdistributors.size(); i++) {
            if (dsdistributors.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1; // Không tìm thấy
    }

}
