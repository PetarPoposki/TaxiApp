package com.example.taxiapp;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder>{
    private List<Driver> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public ImageView Pic;
        public ViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.driver_name);
            Pic = (ImageView) itemView.findViewById(R.id.driver_image);
        }
    }

    // конструктор
    public myAdapter(List<Driver> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    // Креирање нови views (повикано од layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    // Замена на содржината во view (повикано од layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Driver driver = myList.get(i);
        viewHolder.myName.setText(driver.getName());



        Glide.with(mContext)
                //.load(driver.getImage().toString())
                .load(driver.getImage())
                .override(90, 150)
                .fitCenter()
                .into(viewHolder.Pic);

        //viewHolder.Pic.setImageURI(driver.getImage());
    }
    // Пресметка на големината на податочното множество (повикано од
   // layout manager)
    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}

