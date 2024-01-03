package com.example.delvs;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.delvs.databinding.ActivityDisplayUsersBinding;
import com.example.delvs.databinding.ActivityHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayUsers extends AppCompatActivity {
    static int PERMISSION_CODE = 100;
    String mobile_Number;

    public class User {
        private String userName;
        private double latitude;
        private double longitude;
        public String mobileNumber;

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
    TextView MobileNumber;
    Location currentUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        readData();

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


        // Set the current user's location (you need to set this with the actual location)
        currentUserLocation = new Location("currentUser");
        currentUserLocation.setLatitude(currentUserLocation.getLatitude());
        currentUserLocation.setLongitude(currentUserLocation.getLongitude());

        if (ContextCompat.checkSelfPermission(DisplayUsers.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DisplayUsers.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }

        binding.phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + mobile_Number));
                startActivity(i);
            }
        });
    }
    private String getUsernameFromPrefs() {
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        return settings.getString("username", "");
    }
    public void readData() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                String user_Name = getUsernameFromPrefs();
                userList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String userName = snapshot.child("userName").getValue().toString();
                    if (userName.equals(user_Name)) {
                        continue;
                    }
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
        boolean foundNearbyUser = false;
        String currentUserName = getUsernameFromPrefs().toLowerCase();

        for (User user : userList) {
            float[] distance = new float[1];
            Location.distanceBetween(
                    currentUserLocation.getLatitude(), currentUserLocation.getLongitude(),
                    user.getLatitude(), user.getLongitude(),
                    distance
            );

            double distanceInKm = distance[0] / 10000000;
            Toast.makeText(DisplayUsers.this, "Distance: " + distanceInKm, Toast.LENGTH_SHORT).show();

            if (distanceInKm <= 3.0 && !user.getUserName().equalsIgnoreCase(currentUserName)) {
                // User is within the specified distance and not the current user
                // Show a toast for the nearby user
                String toastMessage = "Nearby User: " + user.getUserName() + ", Mobile Number: " + user.getMobileNumber();
                mobile_Number = user.getMobileNumber();
                binding.MobileNumber.setText(user.getMobileNumber());
                // Set the flag to true to indicate that a nearby user was found
                foundNearbyUser = true;
                break; // Break the loop since we only want to show a toast for one nearby user
            }
        }

        // If no nearby user was found, you can display a different toast or handle it as needed
        if (!foundNearbyUser) {
            Toast.makeText(this, "No nearby users found", Toast.LENGTH_SHORT).show();
        }
    }


    // Convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static class HomeActivity extends AppCompatActivity {
        private ActivityHomeBinding binding;
        private FirebaseDatabase db;
        private DatabaseReference reference;
        private ProgressDialog progressDialog;
        private static final String PREFS_NAME = "MyPrefsFile";
        private static final String USERNAME_KEY = "username";
        private static final String MOBILE_NUMBER_KEY = "mobile_number";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Registering...");
            progressDialog.setCancelable(false);

            binding.registerBtn.setOnClickListener(view -> {
                registerUser();
            });

            // Check if the user is already registered
            if (isUserRegistered()) {
                // If registered, open the GetLocation activity
                startActivity(new Intent(HomeActivity.this, GetLocation.class));
                finish(); // Finish the current activity to prevent going back to it
            }
        }

        private boolean isUserRegistered() {
            // Check if the username is already stored in preferences
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            return settings.contains(USERNAME_KEY);
        }

        private void redirectToGetLocation() {
            startActivity(new Intent(HomeActivity.this, GetLocation.class));
            finish(); // Finish the current activity to prevent going back to it
        }


        public void registerUser() {
            String firstName = binding.firstName.getEditText().getText().toString();
            String lastName = binding.lastName.getEditText().getText().toString();
            String age = binding.age.getEditText().getText().toString();
            String userName = binding.userName.getEditText().getText().toString();

            if (!firstName.isEmpty() && !lastName.isEmpty() && !age.isEmpty() && !userName.isEmpty()) {
                String mobileNumber = getMobileNumberFromPrefs();
                Toast.makeText(HomeActivity.this, "MobileNumber: " + mobileNumber, Toast.LENGTH_SHORT).show();
                Users users = new Users(firstName, lastName, age, userName, mobileNumber);
                db = FirebaseDatabase.getInstance();
                reference = db.getReference("Users");
                progressDialog.show();
                reference.child(userName).child("firstName").setValue(firstName);
                reference.child(userName).child("lastName").setValue(lastName);
                reference.child(userName).child("age").setValue(age);
                reference.child(userName).child("userName").setValue(userName);
                reference.child(userName).child("mobileNumber").setValue(mobileNumber).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        saveUsernameToPrefs(userName);

                        // Redirect to GetLocation activity
                        redirectToGetLocation();
                    } else {
                        Toast.makeText(HomeActivity.this, "Registration failed. Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(HomeActivity.this, "Unable to register. Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        }

        private void saveUsernameToPrefs(String username) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(USERNAME_KEY, username);
            editor.apply();
        }

        private String getMobileNumberFromPrefs() {
            SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
            return settings.getString("mobileNumber", "");
        }
    }
}
