package com.example.lab1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lab1.R;
import com.example.lab1.model.Distributor;

import java.util.ArrayList;

public class Spinner_adapter extends BaseAdapter {
    ArrayList<Distributor> list;
    Context context;

    public Spinner_adapter(ArrayList<Distributor> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (list != null) {
            return list.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (list != null) {
            return i;
        }
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_spinner, viewGroup, false);
        TextView tv = v.findViewById(R.id.textView);
        tv.setText((i + 1) + ".");
        TextView tv2 = v.findViewById(R.id.textView2);
        tv2.setText(list.get(i).getName());
        return null;
    }
}
