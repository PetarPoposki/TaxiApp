package com.example.taxiapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddDriverActivity extends AppCompatActivity {

    private DatabaseReference lastRef;
    private StorageReference storageRef;
    private NavigationMenuItemView AddNav;
    private NavigationMenuItemView DeleteNav;
    private EditText driverNameEditText;
    private ImageView driverImageView;
    private static final int PICK_IMAGE = 1;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        lastRef = FirebaseDatabase.getInstance("https://taxiapp-70702-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

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
        driverImageView = findViewById(R.id.driver_image);

        Button uploadPictureButton = findViewById(R.id.upload_picture_button);
        uploadPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        Button saveData = findViewById(R.id.save_button);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String driverName = driverNameEditText.getText().toString();

                // save the data to Firebase Realtime Database
                if(driverName.equals("") || imageUri == null)
                {
                    Toast.makeText(AddDriverActivity.this, "Image or Name is missing", Toast.LENGTH_SHORT).show();
                }
                else {
                    Driver driver = new Driver();
                  //  driver.setImage(imageUri);
                    driver.setName(driverName);
                    lastRef.child("drivers").child(driverName).setValue(driver);
                    storageRef.child("drivers").child(driverName).child("image").putFile(imageUri);

                    startActivity(new Intent(AddDriverActivity.this, AdminOverviewActivity.class));
                }
            }
        });
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AddNav = findViewById(R.id.nav_overview);
        AddNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddDriverActivity.this, AdminOverviewActivity.class));
            }
        });
        DeleteNav = findViewById(R.id.nav_delete);
        DeleteNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddDriverActivity.this, DeleteDriverActivity.class));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            driverImageView.setImageURI(imageUri);
        }
    }


}
