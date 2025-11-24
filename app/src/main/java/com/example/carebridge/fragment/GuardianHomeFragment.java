package com.example.carebridge.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carebridge.R;
import com.example.carebridge.adapters.MedicationAdapter;
import com.example.carebridge.adapters.PatientPagerAdapter;
import com.example.carebridge.shared.controller.AssignedPatientController;
import com.example.carebridge.shared.controller.PrescriptionController;
import com.example.carebridge.shared.model.AssignedPatientInfo;
import com.example.carebridge.shared.model.Medication;
import com.example.carebridge.shared.model.PatientInfo;
import com.example.carebridge.shared.model.Prescription;
import com.example.carebridge.utils.SharedPrefManager;
import com.example.carebridge.view.FullMapActivity;
import com.example.carebridge.view.FullMapActivityBlue;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GuardianHomeFragment extends Fragment {

    private static final String TAG = "GuardianHomeFragment";
    private static final int LOCATION_PERMISSION_REQUEST = 101;

    private TextView tvCurrentDate, tvCurrentTime, tvNoMedicines;
    private ViewPager2 vpPatients;
    private DotsIndicator dotsIndicatorPatients;
    private RecyclerView rvMedications;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnOpenFullMap;

    private final List<PatientInfo> patientList = new ArrayList<>();
    private final List<Medication> medicineList = new ArrayList<>();
    private MedicationAdapter medicationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardian_home, container, false);

        // Date & Time
        tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);

        // Medicines RecyclerView
        tvNoMedicines = view.findViewById(R.id.tvNoMedicines);
        rvMedications = view.findViewById(R.id.rvMedications);
        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));
        medicationAdapter = new MedicationAdapter(medicineList, true);
        rvMedications.setAdapter(medicationAdapter);

        // Patients ViewPager
        vpPatients = view.findViewById(R.id.vpPatients);
        dotsIndicatorPatients = view.findViewById(R.id.dotsIndicatorPatients);

        // SwipeRefresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        // MapView initialization
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(map -> {
            googleMap = map;
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            // Map style based on dark/light mode
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

            // Sample marker
            LatLng sampleLocation = new LatLng(43.6532, -79.3832);
            googleMap.addMarker(new MarkerOptions().position(sampleLocation).title("Nearby Care Center"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sampleLocation, 12f));
        });

        // Button to open full map
        btnOpenFullMap = view.findViewById(R.id.btnOpenFullMap);
        btnOpenFullMap.setOnClickListener(v -> openFullMap());

        startClock();
        refreshData();

        return view;
    }

    private void refreshData() { loadAssignedPatients(); }

    /** Fetch assigned patients */
    private void loadAssignedPatients() {
        String guardianId = new SharedPrefManager(requireContext()).getReferenceId();
        AssignedPatientController controller = new AssignedPatientController();
        controller.getAssignedPatients(guardianId, new AssignedPatientController.AssignedPatientsCallback() {
            @Override
            public void onSuccess(List<AssignedPatientInfo> patients) {
                if (!isAdded()) return;

                patientList.clear();
                for (AssignedPatientInfo p : patients) {
                    PatientInfo info = new PatientInfo();
                    info.setFullName(p.getFull_name());   // ✅ snake_case getter
                    info.setCaseId(p.getPatient_id());    // ✅ snake_case getter
                    patientList.add(info);
                }
                PatientPagerAdapter adapter = new PatientPagerAdapter(patientList);
                vpPatients.setAdapter(adapter);
                dotsIndicatorPatients.setViewPager2(vpPatients);

                // ✅ FIXED: Changed from getCase_id() to getCaseId()
                if (!patientList.isEmpty()) loadPatientMedicines(patientList.get(0).getCaseId());
                else showNoMedicines(true);
            }

            @Override
            public void onFailure(String message) { showNoMedicines(true); }
        });

        vpPatients.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // ✅ FIXED: Changed from getCase_id() to getCaseId()
                if (position < patientList.size()) loadPatientMedicines(patientList.get(position).getCaseId());
            }
        });
    }

    private void loadPatientMedicines(String caseId) {
        if (caseId == null || caseId.isEmpty()) return;

        PrescriptionController controller = new PrescriptionController(requireContext());
        controller.fetchPrescriptionsWithCaseId(caseId, new PrescriptionController.PrescriptionCallback() {
            @Override
            public void onSuccess(List<Prescription> prescriptions) {
                medicineList.clear();
                int total = 0, taken = 0;

                for (Prescription p : prescriptions) {
                    if (p.getMedicines() != null) {
                        medicineList.addAll(p.getMedicines());
                        total += p.getMedicines().size();
                        for (Medication med : p.getMedicines()) if (med.isTaken()) taken++;
                    }
                }

                medicationAdapter.notifyDataSetChanged();
                showNoMedicines(medicineList.isEmpty());

                // Update patient medicine stats
                for (PatientInfo patient : patientList) {
                    // ✅ FIXED: Changed from getCase_id() to getCaseId()
                    if (caseId.equals(patient.getCaseId())) {
                        patient.setTotalMedicines(total);
                        patient.setTakenMedicines(taken);
                        break;
                    }
                }

                if (vpPatients.getAdapter() != null) vpPatients.getAdapter().notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                medicineList.clear();
                medicationAdapter.notifyDataSetChanged();
                showNoMedicines(true);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showNoMedicines(boolean show) { tvNoMedicines.setVisibility(show ? View.VISIBLE : View.GONE); }

    private void startClock() {
        timeRunnable = () -> {
            Date now = new Date();
            tvCurrentDate.setText(new SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault()).format(now));
            tvCurrentTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(now));
            timeHandler.postDelayed(timeRunnable, 1000);
        };
        timeHandler.post(timeRunnable);
    }

    private void openFullMap() { startActivity(new Intent(requireContext(), FullMapActivityBlue.class)); }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
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

    // MapView lifecycle
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