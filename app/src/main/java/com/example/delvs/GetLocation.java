package com.example.delvs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.delvs.databinding.ActivityGetLocationBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GetLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private Location currentLocation;
    private boolean isPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGetLocationBinding getLocationBinding = ActivityGetLocationBinding.inflate(getLayoutInflater());
        setContentView(getLocationBinding.getRoot());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Button getLocationButton = getLocationBinding.getLocation;
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        getLocationBinding.backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click on the BackButton
                navigateToPreviousActivity();
            }

            private void navigateToPreviousActivity() {
                // Create an Intent to navigate to the GetLocation activity
                Intent intent = new Intent(GetLocation.this, LocationAccess.class);

                // You can add any additional data to the intent if needed
                // intent.putExtra("key", "value");

                // Start the activity
                startActivity(intent);

                // Finish the current activity
                finish();
            }
        });

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int FINE_PERMISSION_CODE = 1;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        } else {
            isPermissionGranted = true;
            getLastLocation();
        }
    }

    // Inside getLastLocation method
    private void getLastLocation() {
        if (!isPermissionGranted) {
            checkPermission();
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(GetLocation.this, "Your location Latitude: " + location.getLatitude() +
                            "\nYour location Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    updateLocationInDatabase(location.getLatitude(), location.getLongitude());
                } else {
                    Toast.makeText(GetLocation.this, "Could not find location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLocationInDatabase(double latitude, double longitude) {
        // Obtain the currently signed-in Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userId = getUsernameFromPrefs();

        // Set the location values
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userReference.child("latitude").setValue(latitude);
        userReference.child("longitude").setValue(longitude);

        // Start the DisplayUsers activity
        Intent intent = new Intent(GetLocation.this, DisplayUsers.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    // Retrieve username from shared preferences
    private String getUsernameFromPrefs() {
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        return settings.getString("username", "");
    }


    public void onMapClick(@NonNull LatLng latLng) {
        // Handle map click
    }


    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Handle map ready
    }
}
