package com.example.carebridge;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * MainActivity: Primary entry point of the CareBridge application
 * Handles initial setup and edge-to-edge display configuration
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Initializes the activity and sets up the main layout with edge-to-edge display
     * @param savedInstanceState Persistent state from previous activity instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for modern Android UI
        EdgeToEdge.enable(this);

        // Inflate the main activity layout
        setContentView(R.layout.activity_main);

        // Configure window insets for proper system bar handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}