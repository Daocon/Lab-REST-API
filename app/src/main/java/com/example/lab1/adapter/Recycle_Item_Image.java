package com.example.lab1.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Recycle_Item_Image extends RecyclerView.Adapter<Recycle_Item_Image.ImageViewHolder> {

    List<File> files;
     Context context;

    public Recycle_Item_Image(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_fruit, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        File file = files.get(position);
        if (file != null) {
            holder.iv_fruit.setImageURI(Uri.fromFile(file));
            if (position == files.size() + 1) {
                holder.iv_fruit.setImageResource(R.drawable.baseline_person_24);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (files != null) {
            return files.size();
        }
        return 0;
    }

    public void setListFiles(List<File> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_fruit;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_fruit = itemView.findViewById(R.id.iv_fruit);
        }
    }
}