package com.example.taxiapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    long MIN_TIME_INTERVAL = 1000; // 1 second
    float MIN_DISTANCE = 100; // 100 meters
    LocationManager locationManager;
    Location location = null;
    String notifemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle the updated location
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
            }
        };
        locationManager = (LocationManager)
                RequestActivity.this.getSystemService(Context.LOCATION_SERVICE);
        // Check if the app has permission to access the device's location
        if (ActivityCompat.checkSelfPermission((Activity) RequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission((Activity) RequestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the app doesn't have permission, request permission
            ActivityCompat.requestPermissions((Activity) RequestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // If the location provider is not enabled, prompt the user to enable it
                Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(enableLocationIntent);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

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
        notifemail = email;


        Accept = findViewById(R.id.accept_button);
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestActivity.this, FinishedActivity.class);
                intent.putExtra("message", username);
                lastRef.child("drivers").child(capitalized).child("busy").setValue(1);

                lastRef.child("requests").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snappy: snapshot.getChildren())
                        {
                            for(DataSnapshot snoopy: snappy.getChildren())
                            {
                                if(snoopy.getKey().equals(username) && !snappy.getKey().equals(capitalized))
                                {
                                    lastRef.child("requests").child(snappy.getKey()).child(snoopy.getKey()).removeValue();
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RequestActivity.this, "HELLO" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

                sendNotification(notifemail);
                startActivity(intent);
            }
        });
        Decline = findViewById(R.id.decline_button);
        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastRef.child("requests").child(capitalized).child(username).removeValue();
                startActivity(new Intent(RequestActivity.this, DriverActivity.class));
               // sendNotification(notifemail);
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
                        LatLng driverpos = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng nova = new LatLng((Double) snappy.child("latitude").getValue(),(Double) snappy.child("longitude").getValue());
                        mMap.addMarker(new MarkerOptions().position(nova).title("EVE SU!"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(nova));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nova, 15f)); // 15f is the zoom level
                            Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(driverpos, nova)
                                .width(5)
                                .color(Color.BLUE)
                                .geodesic(true));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestActivity.this, "HELLO" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }
    private void sendNotification(String email) {
        // Create a new message
        JSONObject message = new JSONObject();
        try {
            String topic = "/topics/" + email.split("@")[0];
            message.put("to", topic);
            message.put("data", new JSONObject().put("message","Hello " + email + ". Your request has been accepted."));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send the message using the FCM API
        new SendMessageTask().execute(message);
    }





    private class SendMessageTask extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            try {
                // Send the message to the server
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "key=AAAAg8jkbJw:APA91bFsfE2qkEuRMvXC8CshRKs2gelohOSLSHXBvWrZCGowsCLFOGUM1yvvevf3vT4fPmPGi2-KZEIHe0CLnx8tCAzmiRNFDeOvgmHvc4yAeV-9SGo4VqYuay_Yh77l61mhwmELn7x3");
                connection.setDoOutput(true);

                // Write the message to the request body
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params[0].toString().getBytes());
                outputStream.flush();
                outputStream.close();

                // Read the response from the server
                InputStream inputStream = connection.getInputStream();
                String response = convertStreamToString(inputStream);
                inputStream.close();

                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            // Handle the response from the server
            Log.d(TAG, "Response: " + response);
        }
    }

    private String convertStreamToString(InputStream inputStream) {
        // This method reads the response from the server and converts it to a string
        // You can customize this method to handle the response in any way you want
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

}