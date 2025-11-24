package com.example.carebridge.wear;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.wear.adapters.HealthInfoAdapter;
import com.example.carebridge.wear.databinding.ActivityHealthInfoBinding;
import com.example.carebridge.wear.models.HealthInfo;

import java.util.ArrayList;
import java.util.List;

public class HealthInfoActivity extends AppCompatActivity {

    private ActivityHealthInfoBinding binding;
    private List<HealthInfo> healthInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeData();
        setupRecyclerView();
    }

    private void initializeData() {
        healthInfoList = new ArrayList<>();
        healthInfoList.add(new HealthInfo("Name", "Yash", R.drawable.ic_user));
        healthInfoList.add(new HealthInfo("Blood Group", "B+", R.drawable.ic_droplet));
        healthInfoList.add(new HealthInfo("Age", "29 years", R.drawable.ic_calendar));
        healthInfoList.add(new HealthInfo("Address", "123 Oak Street, Springfield", R.drawable.ic_location));
    }

    private void setupRecyclerView() {
        HealthInfoAdapter adapter = new HealthInfoAdapter(healthInfoList);
        binding.healthInfoRecyclerView.setAdapter(adapter);
        binding.healthInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.healthInfoBackButton.setOnClickListener(v -> finish());
    }
}