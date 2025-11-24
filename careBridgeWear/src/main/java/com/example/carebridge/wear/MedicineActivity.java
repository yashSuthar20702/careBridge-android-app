package com.example.carebridge.wear;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.wear.adapters.MedicineAdapter;
import com.example.carebridge.wear.databinding.ActivityMedicineBinding;
import com.example.carebridge.wear.models.Medicine;

import java.util.ArrayList;
import java.util.List;

public class MedicineActivity extends AppCompatActivity {

    private ActivityMedicineBinding binding;
    private List<Medicine> medicineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeData();
        setupRecyclerView();
    }

    private void initializeData() {
        medicineList = new ArrayList<>();
        medicineList.add(new Medicine("Aspirin", "100mg", "8:00 AM", false));
        medicineList.add(new Medicine("Metformin", "500mg", "12:00 PM", true));
        medicineList.add(new Medicine("Lisinopril", "10mg", "6:00 PM", false));
        medicineList.add(new Medicine("Vitamin D", "1000IU", "8:00 PM", false));
    }

    private void setupRecyclerView() {
        MedicineAdapter adapter = new MedicineAdapter(medicineList);
        binding.medicineRecyclerView.setAdapter(adapter);
        binding.medicineRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.medicineBackButton.setOnClickListener(v -> finish());
    }
}