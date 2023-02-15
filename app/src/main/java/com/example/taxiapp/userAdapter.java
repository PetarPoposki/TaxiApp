package com.example.taxiapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Locale;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder>{
    private List<Driver> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public ImageView Pic;
        private Button Choose;

       // private  ImageButton Cross;
        public ViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.driver_name);
            Pic = (ImageView) itemView.findViewById(R.id.driver_image);
            Choose = (Button) itemView.findViewById(R.id.choose_button);


            //Cross = (ImageButton) itemView.findViewById(R.id.negative_icon);
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
        viewHolder.Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference lastRef;
                lastRef = FirebaseDatabase.getInstance("https://taxiapp-70702-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                String topic = email.split("@")[0];
                String drivermail = myList.get(position).getName().toLowerCase() + "@project.com";
                Request baranje = new Request(email, drivermail);
                lastRef.child("requests").child(myList.get(position).getName()).child(email.split("@")[0]).setValue(baranje);
              //  lastRef.child("drivers").child(myList.get(position).getName()).child("busy").setValue(1);
                Toast.makeText(mContext, "Chosen " + myList.get(position).getName() + ". Wait for reply.", Toast.LENGTH_LONG).show();
                FirebaseMessaging.getInstance().subscribeToTopic(topic)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Subscribed";
                                if (!task.isSuccessful()) {
                                    msg = "Subscribe failed";
                                }
                                Log.d(TAG, msg);
                                //Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
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
