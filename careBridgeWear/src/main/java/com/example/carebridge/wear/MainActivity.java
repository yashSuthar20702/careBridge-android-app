package com.example.carebridge.wear;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.databinding.ActivityMainBinding;
import com.example.carebridge.wear.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load the HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }
}