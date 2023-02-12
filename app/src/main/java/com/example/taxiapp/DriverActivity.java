package com.example.taxiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DriverActivity extends AppCompatActivity {
    private TextView driverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        Intent intent = getIntent();
        String email = intent.getStringExtra("message");
        driverEmail = findViewById(R.id.driver_email);
        driverEmail.setText("The driver's email is: " + email);
    }
}