package com.example.delvs;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.delvs.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    String firstName, lastName, age, userName;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstName = binding.firstName.getText().toString();
                lastName = binding.lastName.getText().toString();
                age = binding.age.getText().toString();
                userName = binding.userName.getText().toString();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !age.isEmpty() && !userName.isEmpty()){

                    Users users = new Users(firstName,lastName,age,userName);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Users");
                    reference.child(userName).setValue(users).addOnCompleteListener(task -> {

                        binding.firstName.setText("");
                        binding.lastName.setText("");
                        binding.age.setText("");
                        binding.userName.setText("");
                        Toast.makeText(HomeActivity.this,"Successfully Registered" ,Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}