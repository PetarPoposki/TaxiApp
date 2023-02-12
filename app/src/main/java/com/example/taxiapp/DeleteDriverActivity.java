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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteDriverActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private DatabaseReference lastRef;
    private StorageReference storageRef;
    private NavigationMenuItemView AddNav;
    private NavigationMenuItemView OverviewNav;
    private NavigationMenuItemView LogoutNav;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private Button DeleteButton;
    private EditText driverNameEditText;
    public String driverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_driver);

        lastRef = FirebaseDatabase.getInstance("https://taxiapp-70702-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance("gs://taxiapp-70702.appspot.com").getReference();


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

        driverNameEditText = findViewById(R.id.driver_name);

        DeleteButton = findViewById(R.id.delete_button);
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverName = driverNameEditText.getText().toString();
                DatabaseReference childref = lastRef.child("drivers").child(driverName);
                childref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Check if the child exists
                        if (dataSnapshot.exists()) {

                            childref.removeValue();
                            Toast.makeText(DeleteDriverActivity.this, "Driver removed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DeleteDriverActivity.this, AdminOverviewActivity.class));


                        } else {
                            Toast.makeText(DeleteDriverActivity.this, "Driver does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (!email.equals("admin@project.com")) {
                // User is not an admin, redirect to login or another activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // User is an admin, continue loading the activity
            }
        } else {
            // User is not signed in, redirect to login or another activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AddNav = findViewById(R.id.nav_add);
        AddNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeleteDriverActivity.this, AddDriverActivity.class));
            }
        });
        OverviewNav = findViewById(R.id.nav_overview);
        OverviewNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeleteDriverActivity.this, AdminOverviewActivity.class));
            }
        });
        LogoutNav = findViewById(R.id.nav_logoutadmin);
        LogoutNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(DeleteDriverActivity.this, MainActivity.class));
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