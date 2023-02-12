package com.example.taxiapp;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder>{
    private List<Driver> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public ImageView Pic;
        private ImageButton Check;
        private  ImageButton Cross;
        public ViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.driver_name);
            Pic = (ImageView) itemView.findViewById(R.id.driver_image);
            Check = (ImageButton) itemView.findViewById(R.id.positive_icon);
            Cross = (ImageButton) itemView.findViewById(R.id.negative_icon);
        }
    }

    // конструктор
    public userAdapter(List<Driver> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    // Креирање нови views (повикано од layout manager)
    @Override
    public userAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new userAdapter.ViewHolder(v);
    }

    // Замена на содржината во view (повикано од layout manager)
    @Override
    public void onBindViewHolder(userAdapter.ViewHolder viewHolder, int i) {
        Driver driver = myList.get(i);
        Integer position = i;
        viewHolder.myName.setText(driver.getName());
        viewHolder.Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "CHECK " + myList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.Cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "CROSS " + myList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });



        Glide.with(mContext)
                //.load(driver.getImage().toString())
                .load(driver.getImage())
                //.override(90, 150)
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
