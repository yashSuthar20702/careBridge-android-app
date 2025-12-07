package com.example.carebridge.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carebridge.R;
import com.example.carebridge.adapters.MedicationAdapter;
import com.example.carebridge.shared.controller.MedicineLogController;
import com.example.carebridge.shared.controller.PrescriptionController;
import com.example.carebridge.shared.model.Medication;
import com.example.carebridge.shared.model.MedicineLog;
import com.example.carebridge.shared.model.Prescription;
import com.example.carebridge.utils.SharedPrefManager;
import com.example.carebridge.view.FullMapActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView rvMedications;
    private TextView tvNoMedicines, tvTotalMedicines, tvTakenMedicines, tvRemainingMedicines;
    private TextView tvCurrentDate, tvCurrentTime, tvWarningMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MedicationAdapter adapter;
    private final List<Medication> medicationList = new ArrayList<>();
    private final List<MedicineLog> medicineLogs = new ArrayList<>();
    private MaterialCardView cardWarning;

    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private SharedPrefManager sharedPrefManager;

    private static final int LOCATION_PERMISSION_REQUEST = 101;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefManager = new SharedPrefManager(requireContext());

        // Init UI
        rvMedications = view.findViewById(R.id.rvMedications);
        tvNoMedicines = view.findViewById(R.id.tvNoMedicines);
        tvTotalMedicines = view.findViewById(R.id.tvTotalMedicines);
        tvTakenMedicines = view.findViewById(R.id.tvTakenMedicines);
        tvRemainingMedicines = view.findViewById(R.id.tvRemainingMedicines);
        tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        cardWarning = view.findViewById(R.id.cardWarning);
        tvWarningMessage = view.findViewById(R.id.tvWarningMessage);

        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicationAdapter(medicationList, false);
        rvMedications.setAdapter(adapter);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mapView.getMapAsync(map -> {
            googleMap = map;
            int currentNightMode = getResources().getConfiguration().uiMode
                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            int styleRes = (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES)
                    ? R.raw.map_style_dark : R.raw.map_style_light;

            try {
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(requireContext(), styleRes)
                );
                if (!success) Log.e(TAG, "Failed to parse map style.");
            } catch (Exception e) {
                Log.e(TAG, "Cannot find map style. Error: ", e);
            }

            enableUserLocation();
        });

        Button btnOpenFullMap = view.findViewById(R.id.btnOpenFullMap);
        btnOpenFullMap.setOnClickListener(v -> openFullMap());

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        startClock();
        loadPrescriptionData();

        try { MapsInitializer.initialize(requireContext()); }
        catch (Exception e) { Log.e(TAG, "MapsInitializer failed", e); }

        return view;
    }

    private boolean isInternetAvailable() {
        if (!isAdded()) return false;
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    private void openFullMap() {
        if (isAdded()) startActivity(new Intent(requireContext(), FullMapActivity.class));
    }

    private void enableUserLocation() {
        if (!isAdded()) return;
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        if (googleMap != null) googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (!isAdded() || location == null || googleMap == null) return;
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));
            googleMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here"));
        });
    }

    private void refreshData() { loadPrescriptionData(); }

    private void loadPrescriptionData() {
        if (!isAdded()) return;

        swipeRefreshLayout.setRefreshing(true);
        hideWarning();

        if (!isInternetAvailable()) {
            showWarning("No internet connection. Data cannot be loaded.");
            showNoMedicineView(true);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        // Fetch prescriptions
        PrescriptionController controller = new PrescriptionController(requireContext());
        controller.fetchPrescriptions(new PrescriptionController.PrescriptionCallback() {
            @Override
            public void onSuccess(List<Prescription> prescriptions) {
                if (!isAdded()) return;

                medicationList.clear();
                for (Prescription p : prescriptions) {
                    if (p.getMedicines() != null) {
                        for (Medication med : p.getMedicines()) {
                            med.calculateDuration(); // automatically calculates durationDays
                        }
                        medicationList.addAll(p.getMedicines());
                    }
                }

                adapter.notifyDataSetChanged();

                // ✅ Load medicine logs using caseId from SharedPreferences
                String caseId = sharedPrefManager.getCaseId();
                loadMedicineLogs(caseId);

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;

                medicationList.clear();
                adapter.notifyDataSetChanged();
                showNoMedicineView(true);
                showWarning("Failed to load prescriptions.");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadMedicineLogs(String caseId) {
        MedicineLogController logController = new MedicineLogController(requireContext());
        logController.fetchLogs(caseId, new MedicineLogController.MedicineLogCallback() {
            @Override
            public void onSuccess(List<MedicineLog> logs) {
                if (!isAdded()) return;

                medicineLogs.clear();
                medicineLogs.addAll(logs);

                updateSummaryCounts();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                showWarning("Failed to load medicine logs: " + errorMessage);
                medicineLogs.clear();
                updateSummaryCounts();
            }
        });
    }

    private void updateSummaryCounts() {
        if (!isAdded()) return;

        int total = medicineLogs.size();
        int taken = 0;
        for (MedicineLog log : medicineLogs) if (log.isTaken()) taken++;
        int remaining = total - taken;

        if (tvTotalMedicines != null) tvTotalMedicines.setText(String.valueOf(total));
        if (tvTakenMedicines != null) tvTakenMedicines.setText(String.valueOf(taken));
        if (tvRemainingMedicines != null) tvRemainingMedicines.setText(String.valueOf(remaining));

        showNoMedicineView(total == 0);
        if (total == 0) showWarning("No medicine assigned right now.");
        else hideWarning();
    }

    private void showNoMedicineView(boolean show) {
        if (!isAdded()) return;
        if (tvNoMedicines != null) tvNoMedicines.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rvMedications != null) rvMedications.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showWarning(String message) {
        if (!isAdded()) return;
        if (tvWarningMessage != null) tvWarningMessage.setText(message);
        if (cardWarning != null) cardWarning.setVisibility(View.VISIBLE);
    }

    private void hideWarning() {
        if (!isAdded()) return;
        if (cardWarning != null) cardWarning.setVisibility(View.GONE);
    }

    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) return;
                Date now = new Date();
                tvCurrentDate.setText(android.text.format.DateFormat.format("EEEE, MMM dd yyyy", now));
                tvCurrentTime.setText(android.text.format.DateFormat.format("hh:mm a", now));
                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    @Override public void onStart() { super.onStart(); mapView.onStart(); }
    @Override public void onResume() { super.onResume(); mapView.onResume(); }
    @Override public void onPause() { super.onPause(); mapView.onPause(); }
    @Override public void onStop() { super.onStop(); mapView.onStop(); }
    @Override public void onDestroyView() {
        super.onDestroyView();
        timeHandler.removeCallbacks(timeRunnable);
        if (mapView != null) mapView.onDestroy();
    }
    @Override public void onLowMemory() { super.onLowMemory(); if (mapView != null) mapView.onLowMemory(); }
    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}
