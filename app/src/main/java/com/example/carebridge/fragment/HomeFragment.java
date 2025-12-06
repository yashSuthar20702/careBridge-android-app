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
import com.example.carebridge.shared.controller.PrescriptionController;
import com.example.carebridge.shared.model.Medication;
import com.example.carebridge.shared.model.Prescription;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView rvMedications;
    private TextView tvNoMedicines, tvTotalMedicines, tvTakenMedicines, tvRemainingMedicines;
    private TextView tvCurrentDate, tvCurrentTime, tvWarningMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MedicationAdapter adapter;
    private final List<Medication> medicationList = new ArrayList<>();
    private MaterialCardView cardWarning;

    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST = 101;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Init UI components
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

        // RecyclerView setup
        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicationAdapter(medicationList, false);
        rvMedications.setAdapter(adapter);

        // Map setup
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

        // Full map button
        Button btnOpenFullMap = view.findViewById(R.id.btnOpenFullMap);
        btnOpenFullMap.setOnClickListener(v -> openFullMap());

        // SwipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        startClock();
        loadPrescriptionData();

        try { MapsInitializer.initialize(requireContext()); }
        catch (Exception e) { Log.e(TAG, "MapsInitializer failed", e); }

        return view;
    }

    private boolean isInternetAvailable() {
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
        startActivity(new Intent(requireContext(), FullMapActivity.class));
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        if (googleMap != null) googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && googleMap != null) {
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));
                googleMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here"));
            }
        });
    }

    private void refreshData() { loadPrescriptionData(); }

    private void loadPrescriptionData() {
        swipeRefreshLayout.setRefreshing(true);
        cardWarning.setVisibility(View.GONE);

        if (!isInternetAvailable()) {
            showWarning("No internet connection. Data cannot be loaded.");
            showNoMedicineView(true);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

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

                updateSummaryCounts();

                if (medicationList.isEmpty()) {
                    showNoMedicineView(true);
                    showWarning("No medicine assigned right now.");
                } else {
                    showNoMedicineView(false);
                    cardWarning.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                medicationList.clear();
                updateSummaryCounts();

                if ("No active prescriptions found".equalsIgnoreCase(errorMessage)) {
                    showNoMedicineView(true);  // Show "No medicines" text
                    showWarning("No medicine assigned right now.");
                } else {
                    showNoMedicineView(false); // Keep map visible
                    showWarning(getString(R.string.network_error_retry_message));
                }

                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    private void updateSummaryCounts() {
        int total = medicationList.size();
        int taken = 0;
        for (Medication med : medicationList) if (med.isTaken()) taken++;
        int remaining = total - taken;

        tvTotalMedicines.setText(String.valueOf(total));
        tvTakenMedicines.setText(String.valueOf(taken));
        tvRemainingMedicines.setText(String.valueOf(remaining));
    }

    private void showNoMedicineView(boolean show) {
        tvNoMedicines.setVisibility(show ? View.VISIBLE : View.GONE);
        rvMedications.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showWarning(String message) {
        tvWarningMessage.setText(message);
        cardWarning.setVisibility(View.VISIBLE);
    }

    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                tvCurrentDate.setText(dateFormat.format(now));
                tvCurrentTime.setText(timeFormat.format(now));
                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    // MapView Lifecycle
    @Override public void onStart() { super.onStart(); mapView.onStart(); }
    @Override public void onResume() { super.onResume(); mapView.onResume(); }
    @Override public void onPause() { super.onPause(); mapView.onPause(); }
    @Override public void onStop() { super.onStop(); mapView.onStop(); }
    @Override public void onDestroyView() {
        super.onDestroyView();
        timeHandler.removeCallbacks(timeRunnable);
        mapView.onDestroy();
    }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
