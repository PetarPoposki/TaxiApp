package com.example.taxiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.internal.NavigationMenuItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminOverviewActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference lastRef;
    private StorageReference storageReference;
    private NavigationMenuItemView AddNav;
    private NavigationMenuItemView DeleteNav;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public Button AddDriver;
    RecyclerView mRecyclerView;
    myAdapter mAdapter;
    List<Driver> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overview);

        values = new ArrayList<Driver>();
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://taxiapp-70702.appspot.com/drivers/Petar/image.jpg");
        storageReference = FirebaseStorage.getInstance("gs://taxiapp-70702.appspot.com").getReference();
        lastRef = FirebaseDatabase.getInstance("https://taxiapp-70702-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        //сетирање на RecyclerView контејнерот
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
// оваа карактеристика може да се користи ако се знае дека промените
// во содржината нема да ја сменат layout големината на RecyclerView
        mRecyclerView.setHasFixedSize(true);
// ќе користиме LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
// и default animator (без анимации)
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
// сетирање на кориснички дефиниран адаптер myAdapter (посебна класа)
        mAdapter = new myAdapter(values, R.layout.my_row, this);
//прикачување на адаптерот на RecyclerView
        mRecyclerView.setAdapter(mAdapter);


        lastRef.child("drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snappy: snapshot.getChildren())
                {
                    Driver driver = snappy.getValue(Driver.class);
                    //StorageReference stor = storageReference.child("drivers").child(driver.getName()).child("image.jpg");
                    String url = "https://firebasestorage.googleapis.com/v0/b/taxiapp-70702.appspot.com/o/drivers%2F" + driver.getName() +"%2Fimage?alt=media";
                    driver.setImage(url);
                    values.add(driver);
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminOverviewActivity.this, "HELLO" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });




        AddDriver = findViewById(R.id.add_driver_button);
        AddDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminOverviewActivity.this, AddDriverActivity.class));
            }
        });

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout_admin);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

    }


    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AddNav = findViewById(R.id.nav_add);
        AddNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminOverviewActivity.this, AddDriverActivity.class));
            }
        });
        DeleteNav = findViewById(R.id.nav_delete);
        DeleteNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminOverviewActivity.this, DeleteDriverActivity.class));
            }
        });
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}