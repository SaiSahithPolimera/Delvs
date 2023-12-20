package com.example.delvs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean isPermissionGranted;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
private FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mAuth = FirebaseAuth.getInstance();
        checkPermission();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        getLastLocation();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            // Save user location to the "Users" node
            usersRef.child("latitude").setValue(currentLocation.getLatitude());
            usersRef.child("longitude").setValue(currentLocation.getLongitude()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LocationActivity.this, "Location detected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocationActivity.this, "Failed to save location", Toast.LENGTH_SHORT).show();
                }
            });

            // Find the useLocation button and set its click listener
            Button useLocationButton = findViewById(R.id.useLocation);
            useLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentLocation != null) {
                        // Display latitude and longitude in a toast
                        Toast.makeText(LocationActivity.this, "Latitude: " + currentLocation.getLatitude() +
                                "\nLongitude: " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LocationActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Find the backButton image button and set its click listener
            ImageButton backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Go back to the previous activity (LocationAccess)
                    finish();
                }
            });
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        } else {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                }
            });
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            isPermissionGranted = false;
        } else {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setOnMapClickListener(this);
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(this, "Your location latitude: " + currentLocation.getLatitude() +
                    " longitude: " + currentLocation.getLongitude() + ".", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Handle map click event if needed
    }
}
