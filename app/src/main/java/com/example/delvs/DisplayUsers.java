package com.example.delvs;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.delvs.databinding.ActivityDisplayUsersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import com.google.firebase.database.ValueEventListener;

public class DisplayUsers extends AppCompatActivity {

    public class User {
        private String userName;
        private double latitude;
        private double longitude;
        private String mobileNumber;

        // Constructor
        public User(String userName, double latitude, double longitude, String mobileNumber) {
            this.userName = userName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.mobileNumber = mobileNumber;
        }

        // Getters and Setters (you can generate these using your IDE)

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }
    }

    ActivityDisplayUsersBinding binding;
    ArrayList<User> userList = new ArrayList<>();
    DatabaseReference reference;
    Location currentUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click on the BackButton
                navigateToPreviousActivity();
            }

            private void navigateToPreviousActivity() {
                // Create an Intent to navigate to the GetLocation activity
                Intent intent = new Intent(DisplayUsers.this, GetLocation.class);

                // You can add any additional data to the intent if needed
                // intent.putExtra("key", "value");

                // Start the activity
                startActivity(intent);

                // Finish the current activity
                finish();
            }
        });

        binding.phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DisplayUsers.this, "Entered Onclick", Toast.LENGTH_SHORT).show();
                readData();
            }
        });

        // Set the current user's location (you need to set this with the actual location)
        currentUserLocation = new Location("currentUser");
        currentUserLocation.setLatitude(currentUserLocation.getLatitude());
        currentUserLocation.setLongitude(currentUserLocation.getLongitude());
    }

    public void readData() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                Toast.makeText(DisplayUsers.this, "Entered datasnapshot", Toast.LENGTH_SHORT).show();
                userList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String userName = snapshot.child("userName").getValue().toString();
                    double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                    String mobileNumber = snapshot.child("mobileNumber").getValue().toString();
                    User user = new User(userName, latitude, longitude, mobileNumber);
                    userList.add(user);
                }
                // Now the userList contains all the users' data
                // You can access the userList and perform any operations you need
                // Compare with the current user's location
                findNearbyUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    // Inside findNearbyUsers method
    private void findNearbyUsers() {
        NestedScrollView scrollView = findViewById(R.id.scrollView);
        LinearLayout userInfoBlock = findViewById(R.id.UserInfoBlock);
        userInfoBlock.removeAllViews(); // Clear existing views before adding new ones

        for (User user : userList) {
            float[] distance = new float[1];
            Location.distanceBetween(
                    currentUserLocation.getLatitude(), currentUserLocation.getLongitude(),
                    user.getLatitude(), user.getLongitude(),
                    distance
            );

            double distanceInKm = distance[0] / 1000;

            if (distanceInKm <= 3.0) {
                // User is within the specified distance
                // Create a LinearLayout for each user
                LinearLayout userLayout = new LinearLayout(this);
                userLayout.setOrientation(LinearLayout.HORIZONTAL);
                userLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                // Create an ImageView for the profile picture
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        50, // Width
                        50 // Height
                ));
                imageView.setImageResource(R.drawable.searchaccounticon);
                userLayout.addView(imageView);

                // Create a TextView for the mobile number
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, // Width
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1 // Weight
                ));
                textView.setText(user.getMobileNumber());
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                textView.setBackgroundColor(Color.parseColor("#F4EDED"));
                textView.setTextColor(Color.parseColor("#546E7A"));
                userLayout.addView(textView);

                // Create an ImageButton for the phone call
                ImageButton phoneButton = new ImageButton(this);
                phoneButton.setLayoutParams(new LinearLayout.LayoutParams(
                        50, // Width
                        50 // Height
                ));
                phoneButton.setImageResource(R.drawable.circlecallbutton);
                phoneButton.setBackgroundColor(Color.BLACK);
                userLayout.addView(phoneButton);

                // Add the user's layout to the parent layout
                userInfoBlock.addView(userLayout);
            }
        }
    }
}
