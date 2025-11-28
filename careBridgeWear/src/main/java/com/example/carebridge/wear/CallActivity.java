package com.example.carebridge.wear;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.carebridge.wear.adapters.GuardianCallAdapter;
import com.example.carebridge.wear.databinding.ActivityCallBinding;
import com.example.carebridge.wear.models.Guardian;
import java.util.ArrayList;
import java.util.List;

public class CallActivity extends AppCompatActivity implements GuardianCallAdapter.OnGuardianCallListener {

    private ActivityCallBinding binding;
    private List<Guardian> guardianList;
    private GuardianCallAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
        initializeGuardians();
        setupRecyclerView();
    }

    private void setupClickListeners() {
        binding.callBackButton.setOnClickListener(v -> finish());
        binding.callEmergencyContact.setOnClickListener(v -> makeCall("911"));
    }

    private void initializeGuardians() {
        guardianList = new ArrayList<>();
        // Add sample guardians - you can replace this with data from your API/database
        guardianList.add(new Guardian("Yash", "Family", "Friend", "+1 519-569-2560"));
        guardianList.add(new Guardian("Dhwani", "Caretaker", "Primary Nurse", "+1 519-568-2540"));
        guardianList.add(new Guardian("Jasjit S", "Family", "Primary Guardian", "+1 519-573-0317"));
        guardianList.add(new Guardian("Dr. Smith", "Medical", "Primary Doctor", "+1 519-555-1234"));
    }

    private void setupRecyclerView() {
        adapter = new GuardianCallAdapter(guardianList, this);
        binding.guardiansRecyclerView.setAdapter(adapter);
        binding.guardiansRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onGuardianCall(Guardian guardian) {
        makeCall(guardian.getPhone());
    }

    private void makeCall(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}