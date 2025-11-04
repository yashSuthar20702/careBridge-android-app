package com.example.carebridge.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carebridge.R;
import com.example.carebridge.adapters.MedicationAdapter;
import com.example.carebridge.controller.PrescriptionController;
import com.example.carebridge.model.Medication;
import com.example.carebridge.model.Prescription;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvMedications;
    private TextView tvNoMedicines;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MedicationAdapter adapter;
    private final List<Medication> medicationList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvMedications = view.findViewById(R.id.rvMedications);
        tvNoMedicines = view.findViewById(R.id.tvNoMedicines);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicationAdapter(medicationList);
        rvMedications.setAdapter(adapter);

        // Pull-to-refresh action
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        // Initial load
        loadPrescriptionData();

        return view;
    }

    private void refreshData() {
        Log.d("HomeFragment", "Refreshing data...");
        loadPrescriptionData();
    }

    private void loadPrescriptionData() {
        swipeRefreshLayout.setRefreshing(true);

        PrescriptionController controller = new PrescriptionController(requireContext());
        controller.fetchPrescriptions(new PrescriptionController.PrescriptionCallback() {
            @Override
            public void onSuccess(List<Prescription> prescriptions) {
                medicationList.clear();

                for (Prescription p : prescriptions) {
                    if (p.getMedicines() != null) {
                        medicationList.addAll(p.getMedicines());
                    }
                }

                if (medicationList.isEmpty()) {
                    showNoMedicineView(true);
                } else {
                    showNoMedicineView(false);
                }

                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                Log.d("HomeFragment", "Loaded " + medicationList.size() + " medicines");
            }

            @Override
            public void onFailure(String errorMessage) {
                showNoMedicineView(true);
                tvNoMedicines.setText("No medicines assigned by doctor yet.");
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                Log.e("HomeFragment", "Failed: " + errorMessage);
            }
        });
    }

    private void showNoMedicineView(boolean show) {
        if (show) {
            tvNoMedicines.setVisibility(View.VISIBLE);
            rvMedications.setVisibility(View.GONE);
        } else {
            tvNoMedicines.setVisibility(View.GONE);
            rvMedications.setVisibility(View.VISIBLE);
        }
    }
}
