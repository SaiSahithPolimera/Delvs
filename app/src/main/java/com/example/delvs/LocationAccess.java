package com.example.delvs;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LocationAccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_access); // Set the content view to your XML layout file

        Button setLocationButton = findViewById(R.id.setLocation);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the button click and start LocationActivity
                Intent intent = new Intent(LocationAccess.this, LocationActivity.class);
                startActivity(intent);
            }
        });
    }
}
