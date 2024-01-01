package com.example.delvs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.delvs.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
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
    } // <-- Added missing closing brace for onCreate method

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
                    startActivity(new Intent(HomeActivity.this, LocationAccess.class));
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