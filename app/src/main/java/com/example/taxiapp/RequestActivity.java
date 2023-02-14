package com.example.taxiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView selected;
    private NavigationMenuItemView LogoutNav;
    private NavigationMenuItemView RequestsNav;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    DatabaseReference lastRef;
    private GoogleMap mMap;
    private MapView mMapView;
    private Button Accept;
    private Button Decline;
    String capitalized;
    String username;
    private static final LatLng SYDNEY_OPERA_HOUSE = new LatLng(-33.8567844, 151.213108);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        Intent intent = getIntent();
        String email = intent.getStringExtra("message");
        selected = findViewById(R.id.selected_email);
        selected.setText(email);

        lastRef = FirebaseDatabase.getInstance("https://taxiapp-70702-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String name = user.getEmail().split("@")[0];
        capitalized = name.substring(0, 1).toUpperCase() + name.substring(1);
        username = email.split("@")[0];


        Accept = findViewById(R.id.accept_button);
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastRef.child("drivers").child(capitalized).child("busy").setValue(1);

            }
        });
        Decline = findViewById(R.id.decline_button);
        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastRef.child("requests").child(capitalized).child(username).removeValue();
                startActivity(new Intent(RequestActivity.this, DriverActivity.class));
            }
        });



        drawerLayout = findViewById(R.id.my_drawer_layout_admin);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        LogoutNav = findViewById(R.id.nav_logoutdriverreq);
        LogoutNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(RequestActivity.this, MainActivity.class));
            }
        });
        RequestsNav = findViewById(R.id.nav_requests);
        RequestsNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestActivity.this, DriverActivity.class));
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


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();

        } else {
            // User is not signed in, redirect to login or another activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        lastRef.child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snappy: snapshot.getChildren())
                {
                    if(snappy.getKey().equals(username))
                    {
                     LatLng nova = new LatLng((Double) snappy.child("latitude").getValue(),(Double) snappy.child("longitude").getValue());
                     mMap.addMarker(new MarkerOptions().position(nova).title("EVE SU!"));
                     mMap.moveCamera(CameraUpdateFactory.newLatLng(nova));
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nova, 15f)); // 15f is the zoom level
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestActivity.this, "HELLO" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }
}