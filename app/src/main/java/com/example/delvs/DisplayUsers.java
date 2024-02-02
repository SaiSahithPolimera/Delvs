package com.example.delvs;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.delvs.databinding.ActivityDisplayUsersBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayUsers extends AppCompatActivity {
    ActivityDisplayUsersBinding binding;
    ArrayList<Users> userList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;

    DatabaseReference reference;
    static int PERMISSION_CODE = 100;

    Location currentUserLocation;
    String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(DisplayUsers.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DisplayUsers.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Use the location
                            currentUserLocation = location;
                            readData(); // Call readData() here to ensure it's called after the location is obtained
                        }
                    }
                });
        }

        if (ContextCompat.checkSelfPermission(DisplayUsers.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DisplayUsers.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DisplayUsers.this, GetLocation.class);
                startActivity(i);
            }
        });

        binding.phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_CALL);

                i.setData(Uri.parse("tel:" + mobileNumber));
                startActivity(i);
            }
        });
    }

    private String getUsernameFromPrefs() {
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        return settings.getString("username", "");
    }

       public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    public void readData() {
        int minDistance = 2; // Minimum distance in km
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if(datasnapshot.exists() ) {
                String user_Name = getUsernameFromPrefs();
                userList.clear(); // Clear the list before adding new data
                Users nearestUser = null;
                double minDistance = Double.MAX_VALUE;
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    Object userNameObj = snapshot.child("userName").getValue();
                    Object firstNameObj = snapshot.child("firstName").getValue();
                    Object latitudeObj = snapshot.child("latitude").getValue();
                    Object longitudeObj = snapshot.child("longitude").getValue();
                    Object mobileNumberObj = snapshot.child("mobileNumber").getValue();

                    if (userNameObj == null || firstNameObj == null || latitudeObj == null || longitudeObj == null || mobileNumberObj == null) {
                        continue; // Skip this record if any field is null
                    }

                    String userName = userNameObj.toString();
                    if(userName.equals(user_Name)) {
                        continue; // Skip this record if it's the current user
                    }
                    String firstName = firstNameObj.toString();
                    double latitude = Double.parseDouble(latitudeObj.toString());
                    double longitude = Double.parseDouble(longitudeObj.toString());
                    mobileNumber = mobileNumberObj.toString();

                    Users user = new Users(userName,firstName, latitude, longitude, mobileNumber);
                    userList.add(user);

                    if (currentUserLocation != null) {
                        double distance = calculateDistance(currentUserLocation.getLatitude(), currentUserLocation.getLongitude(), latitude, longitude);
                        if (distance <= minDistance) { // If the distance is less than or equal to 2km
                        minDistance = distance;
                        nearestUser = user;
                        }
                    }
                }
                if (nearestUser != null) {
                    binding.userName.setText(nearestUser.getFirstName());
                    mobileNumber = nearestUser.getMobileNumber();
                    binding.phoneButton.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(DisplayUsers.this, "No Users Found", Toast.LENGTH_SHORT).show();
                        binding.phoneButton.setVisibility(View.INVISIBLE);
                }
            }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
    });
    }


}