package com.example.carebridge.wear;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.wear.adapters.GuardianAdapter;
import com.example.carebridge.wear.databinding.ActivityGuardianBinding;
import com.example.carebridge.wear.models.Guardian;

import java.util.ArrayList;
import java.util.List;

public class GuardianActivity extends AppCompatActivity {

    private ActivityGuardianBinding binding;
    private List<Guardian> guardianList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuardianBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeData();
        setupRecyclerView();
    }

    private void initializeData() {
        guardianList = new ArrayList<>();
        guardianList.add(new Guardian("Sarah Smith", "Family", "Daughter", "+1 234-5679"));
        guardianList.add(new Guardian("Mary Johnson", "Caretaker", "Primary Nurse", "+1 234-5680"));
    }

    private void setupRecyclerView() {
        GuardianAdapter adapter = new GuardianAdapter(guardianList);
        binding.guardianRecyclerView.setAdapter(adapter);
        binding.guardianRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.guardianBackButton.setOnClickListener(v -> finish());
    }
}