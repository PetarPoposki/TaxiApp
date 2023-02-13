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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDriverActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private DatabaseReference lastRef;
    private StorageReference storageRef;
    private NavigationMenuItemView AddNav;
    private NavigationMenuItemView DeleteNav;
    private NavigationMenuItemView LogoutNav;
    private EditText driverNameEditText;
    private EditText driverEmailEditText;
    private EditText driverPasswordEditText;
    private ImageView driverImageView;
    private static final int PICK_IMAGE = 1;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        mAuth = FirebaseAuth.getInstance();
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
        driverEmailEditText = findViewById(R.id.driver_email);
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
                String driverEmail = driverEmailEditText.getText().toString();

                // save the data to Firebase Realtime Database
                if(driverName.equals("") || imageUri == null)
                {
                    Toast.makeText(AddDriverActivity.this, "Image or Name is missing", Toast.LENGTH_SHORT).show();
                }
                else {
                    Driver driver = new Driver();
                     // driver.setImage(imageUri);
                    driver.setName(driverName);
                    driver.setBusy(0);
                    lastRef.child("drivers").child(driverName).setValue(driver);
                    lastRef.child("driver emails").child(driverName).setValue(driverEmail);
                    PerformAuth();
                    storageRef.child("drivers").child(driverName).child("image").putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    startActivity(new Intent(AddDriverActivity.this, AdminOverviewActivity.class));
                                }
                            });
                }
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
        LogoutNav = findViewById(R.id.nav_logoutadmin);
        LogoutNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(AddDriverActivity.this, MainActivity.class));
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

    public static boolean isEmailValid(String email) {

        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
            return true;
        else
            return false;
    }
    private void PerformAuth() {
        driverEmailEditText = findViewById(R.id.driver_email);
        driverPasswordEditText = findViewById(R.id.driver_password);
        //confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);

        String email = driverEmailEditText.getText().toString();
        String password = driverPasswordEditText.getText().toString();
        String confirmpassword = driverPasswordEditText.getText().toString();

        if (!isEmailValid(email))
        {
            driverEmailEditText.setError("Enter correct email.");
        }
        else if (password.isEmpty() || password.length()<6)
        {
            driverPasswordEditText.setError("Enter correct password.");
        }
        else if (!password.equals(confirmpassword))
        {
            driverPasswordEditText.setError("Passwords do not match.");
        }
        else
        {


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {

                        Toast.makeText(AddDriverActivity.this, "Registration is successful.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(AddDriverActivity.this, "" + task.getException() , Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }


}
