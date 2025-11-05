package com.example.carebridge.fragment;

import android.Manifest;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carebridge.R;
import com.example.carebridge.adapters.MedicationAdapter;
import com.example.carebridge.controller.PrescriptionController;
import com.example.carebridge.model.Medication;
import com.example.carebridge.model.Prescription;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView rvMedications;
    private TextView tvNoMedicines, tvTotalMedicines, tvTakenMedicines, tvRemainingMedicines;
    private TextView tvCurrentDate, tvCurrentTime;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MedicationAdapter adapter;
    private final List<Medication> medicationList = new ArrayList<>();

    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST = 101;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔹 Initialize UI components
        rvMedications = view.findViewById(R.id.rvMedications);
        tvNoMedicines = view.findViewById(R.id.tvNoMedicines);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvTotalMedicines = view.findViewById(R.id.tvTotalMedicines);
        tvTakenMedicines = view.findViewById(R.id.tvTakenMedicines);
        tvRemainingMedicines = view.findViewById(R.id.tvRemainingMedicines);
        tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);

        // Check Internet connection
        if (!isInternetAvailable()) {
            Log.w(TAG, "Internet not available");
            new AlertDialog.Builder(requireContext())
                    .setTitle("No Internet Connection")
                    .setMessage("Please enable internet to load maps and data.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            Log.i(TAG, "Internet is available");
        }

        // RecyclerView setup
        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicationAdapter(medicationList);
        rvMedications.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        startClock();
        loadPrescriptionData();

        // Initialize Google Map
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(requireContext());
        } catch (Exception e) {
            Log.e(TAG, "MapsInitializer failed", e);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                enableUserLocation();
            }
        });

        return view;
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                boolean isConnected = capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                Log.d(TAG, "Network check: " + isConnected);
                return isConnected;
            } else {
                Log.d(TAG, "No active network");
            }
        } else {
            Log.d(TAG, "ConnectivityManager is null");
        }
        return false;
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));
                    googleMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here"));
                    Log.i(TAG, "User location: " + location.getLatitude() + ", " + location.getLongitude());
                } else {
                    Log.w(TAG, "Location is null");
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to get location", e);
            });
        }
    }

    private void refreshData() { loadPrescriptionData(); }

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
                updateSummaryCounts();
                showNoMedicineView(medicationList.isEmpty());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                Log.i(TAG, "Prescriptions loaded successfully");
            }

            @Override
            public void onFailure(String errorMessage) {
                showNoMedicineView(true);
                tvNoMedicines.setText("No medicines assigned by doctor yet.");
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Failed to load prescriptions: " + errorMessage);
            }
        });
    }

    private void updateSummaryCounts() {
        int total = medicationList.size();
        int taken = 0;
        int remaining = 0;
        for (Medication med : medicationList) {
            if (med.isTaken()) taken++;
            else remaining++;
        }
        tvTotalMedicines.setText(String.valueOf(total));
        tvTakenMedicines.setText(String.valueOf(taken));
        tvRemainingMedicines.setText(String.valueOf(remaining));
        Log.d(TAG, "Summary updated: total=" + total + ", taken=" + taken + ", remaining=" + remaining);
    }

    private void showNoMedicineView(boolean show) {
        tvNoMedicines.setVisibility(show ? View.VISIBLE : View.GONE);
        rvMedications.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                tvCurrentDate.setText(dateFormat.format(new Date()));
                tvCurrentTime.setText(timeFormat.format(new Date()));
                timeHandler.postDelayed(this, 60 * 1000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timeHandler.removeCallbacks(timeRunnable);
        mapView.onDestroy();
    }

    @Override public void onStart() { super.onStart(); mapView.onStart(); }
    @Override public void onResume() { super.onResume(); mapView.onResume(); }
    @Override public void onPause() { super.onPause(); mapView.onPause(); }
    @Override public void onStop() { super.onStop(); mapView.onStop(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
