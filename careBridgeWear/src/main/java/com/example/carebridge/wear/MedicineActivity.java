package com.example.carebridge.wear;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.shared.controller.PrescriptionController;
import com.example.carebridge.shared.model.Medication;
import com.example.carebridge.shared.model.Prescription;
import com.example.carebridge.wear.adapters.MedicineAdapter;
import com.example.carebridge.wear.databinding.ActivityMedicineBinding;

import java.util.ArrayList;
import java.util.List;

public class MedicineActivity extends AppCompatActivity {

    private ActivityMedicineBinding binding;
    private List<Medication> medicineList = new ArrayList<>();
    private MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        loadMedicineData();

        binding.medicineBackButton.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new MedicineAdapter(medicineList);
        binding.medicineRecyclerView.setAdapter(adapter);
        binding.medicineRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Remove click listener from adapter level - users can no longer toggle status
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        Network network = cm.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities nc = cm.getNetworkCapabilities(network);
        return nc != null &&
                (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    private void loadMedicineData() {
        // If no internet, show message
        if (!isInternetAvailable()) {
            showNoMedicine(getString(R.string.no_internet_connection));
            return;
        }

        PrescriptionController controller = new PrescriptionController(this);
        controller.fetchPrescriptions(new PrescriptionController.PrescriptionCallback() {
            @Override
            public void onSuccess(List<Prescription> prescriptions) {
                medicineList.clear();

                for (Prescription p : prescriptions) {
                    if (p.getMedicines() != null) {
                        medicineList.addAll(p.getMedicines());
                    }
                }

                if (medicineList.isEmpty()) {
                    showNoMedicine(getString(R.string.no_medicine_found));
                } else {
                    showList();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                showNoMedicine(getString(R.string.failed_load_data));
            }
        });
    }

    private void showNoMedicine(String message) {
        binding.medicineRecyclerView.setVisibility(View.GONE);
        binding.noMedicineText.setVisibility(View.VISIBLE);
        binding.noMedicineText.setText(message);
    }

    private void showList() {
        binding.noMedicineText.setVisibility(View.GONE);
        binding.medicineRecyclerView.setVisibility(View.VISIBLE);
    }
}