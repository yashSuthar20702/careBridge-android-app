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

/**
 * MedicineActivity

 * Displays the list of prescribed medicines for the logged-in user on Wear OS.
 * Data is fetched from the backend using PrescriptionController.
 * UI is optimized for small Wear OS screens.
 */
public class MedicineActivity extends AppCompatActivity {

    private ActivityMedicineBinding binding;

    // List holding all medicines fetched from prescriptions
    private final List<Medication> medicineList = new ArrayList<>();

    // RecyclerView adapter
    private MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup RecyclerView
        setupRecyclerView();

        // Load medicine data from backend
        loadMedicineData();
    }

    /**
     * Configures RecyclerView and its adapter
     */
    private void setupRecyclerView() {
        adapter = new MedicineAdapter(medicineList);
        binding.medicineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.medicineRecyclerView.setAdapter(adapter);
    }

    /**
     * Checks whether the device has an active internet connection
     */
    private boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        Network network = cm.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    /**
     * Fetches prescription data and extracts medicines
     */
    private void loadMedicineData() {

        // Validate internet availability before API call
        if (!isInternetAvailable()) {
            showNoMedicine(getString(R.string.no_internet_connection));
            return;
        }

        PrescriptionController controller = new PrescriptionController(this);

        controller.fetchPrescriptions(new PrescriptionController.PrescriptionCallback() {

            @Override
            public void onSuccess(List<Prescription> prescriptions) {
                medicineList.clear();

                // Extract medicines from each prescription
                for (Prescription prescription : prescriptions) {
                    if (prescription.getMedicines() != null) {
                        medicineList.addAll(prescription.getMedicines());
                    }
                }

                // Update UI based on data availability
                if (medicineList.isEmpty()) {
                    showNoMedicine(getString(R.string.no_medicine_found));
                } else {
                    showMedicineList();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                showNoMedicine(getString(R.string.failed_load_data));
            }
        });
    }

    /**
     * Displays message when no medicines are available
     */
    private void showNoMedicine(String message) {
        binding.medicineRecyclerView.setVisibility(View.GONE);
        binding.noMedicineText.setVisibility(View.VISIBLE);
        binding.noMedicineText.setText(message);
    }

    /**
     * Displays the medicine list
     */
    private void showMedicineList() {
        binding.noMedicineText.setVisibility(View.GONE);
        binding.medicineRecyclerView.setVisibility(View.VISIBLE);
    }
}