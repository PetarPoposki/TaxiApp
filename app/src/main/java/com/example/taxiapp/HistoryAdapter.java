package com.example.taxiapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private List<History> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView driverName;
        public TextView timeName;

        // private  ImageButton Cross;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.hist_user);
            driverName = (TextView) itemView.findViewById(R.id.hist_driver);
            timeName = (TextView) itemView.findViewById(R.id.hist_time);

        }
    }

    // конструктор
    public HistoryAdapter(List<History> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }
    // Креирање нови views (повикано од layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new HistoryAdapter.ViewHolder(v);
    }
    // Замена на содржината во view (повикано од layout manager)
    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder viewHolder, int i) {
        History history = myList.get(i);
        Integer position = i;
        viewHolder.userName.setText("Client: " + history.getUser().substring(0, 1).toUpperCase() + history.getUser().substring(1));
        viewHolder.driverName.setText("Driver: " + history.getDriver().substring(0, 1).toUpperCase() + history.getDriver().substring(1));
        Date vreme = history.getTime();
        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

// Convert the Date object to a formatted string using the SimpleDateFormat object
        String formattedDate = dateFormat.format(vreme);
        viewHolder.timeName.setText("Time: " + formattedDate);
    }
    // Пресметка на големината на податочното множество (повикано од
    // layout manager)
    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

}
