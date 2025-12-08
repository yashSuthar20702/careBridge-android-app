package com.example.carebridge.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carebridge.R;
import com.example.carebridge.adapters.MedicationAdapter;
import com.example.carebridge.shared.controller.DailyTipsController;
import com.example.carebridge.shared.controller.MedicineLogController;
import com.example.carebridge.shared.controller.PrescriptionController;
import com.example.carebridge.shared.model.Medication;
import com.example.carebridge.shared.model.MedicineLog;
import com.example.carebridge.shared.model.Prescription;
import com.example.carebridge.shared.model.Tip;
import com.example.carebridge.shared.model.Video;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int LOCATION_PERMISSION_REQUEST = 101;

    private RecyclerView rvMedications;
    private TextView tvNoMedicines, tvTotalMedicines, tvTakenMedicines, tvRemainingMedicines;
    private TextView tvCurrentDate, tvCurrentTime, tvDailyMessage, tvDailyHeader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MedicationAdapter adapter;
    private final List<Medication> medicationList = new ArrayList<>();
    private final List<MedicineLog> medicineLogs = new ArrayList<>();

    private MaterialCardView cardDailyTip;
    private LinearLayout btnActionTip;
    private ImageView imgDailyType;
    private YouTubePlayerView youtubePlayerView;

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    private SharedPrefManager sharedPrefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefManager = new SharedPrefManager(requireContext());
        initViews(view);

        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicationAdapter(medicationList, false);
        rvMedications.setAdapter(adapter);

        mapView.onCreate(savedInstanceState);
        loadMap();

        view.findViewById(R.id.btnOpenFullMap).setOnClickListener(v -> openFullMap());
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        startClock();
        fetchDailyTips();
        loadPrescriptionData();

        try { MapsInitializer.initialize(requireContext()); }
        catch (Exception e) { Log.e(TAG, "MapsInitializer failed", e); }

        return view;
    }

    private void initViews(View view) {
        rvMedications = view.findViewById(R.id.rvMedications);
        tvNoMedicines = view.findViewById(R.id.tvNoMedicines);
        tvTotalMedicines = view.findViewById(R.id.tvTotalMedicines);
        tvTakenMedicines = view.findViewById(R.id.tvTakenMedicines);
        tvRemainingMedicines = view.findViewById(R.id.tvRemainingMedicines);
        tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);

        cardDailyTip = view.findViewById(R.id.cardDailyTip);
        tvDailyMessage = view.findViewById(R.id.tvDailyMessage);
        tvDailyHeader = view.findViewById(R.id.tvDailyHeader);
        imgDailyType = view.findViewById(R.id.imgDailyType);
        btnActionTip = view.findViewById(R.id.btnActionTip);
        youtubePlayerView = view.findViewById(R.id.youtubePlayerView);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mapView = view.findViewById(R.id.mapView);

        cardDailyTip.setVisibility(View.GONE);
        btnActionTip.setVisibility(View.GONE);
        youtubePlayerView.setVisibility(View.GONE);
    }

    private void loadMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mapView.getMapAsync(map -> {
            googleMap = map;
            int nightMode = getResources().getConfiguration().uiMode &
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK;

            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES ?
                            R.raw.map_style_dark : R.raw.map_style_light
            ));
            enableUserLocation();
        });
    }

    private void fetchDailyTips() {
        new DailyTipsController().fetchDailyTips(new DailyTipsController.DailyTipCallback() {
            @Override
            public void onTips(List<Tip> tips) {
                if (!isAdded() || tips == null || tips.isEmpty()) return;
                Tip today = tips.get(0);
                showTip(today.getTitle());
            }

            @Override
            public void onVideo(Video video) {
                if (!isAdded()) return;
                showVideo(video.getTitle(), video.getUrl());
            }

            @Override
            public void onFailure(String error) { Log.e(TAG, error); }
        });
    }

    private void showTip(String message) {
        cardDailyTip.setVisibility(View.VISIBLE);
        tvDailyHeader.setText("Daily Tip");
        tvDailyMessage.setText(message);
        imgDailyType.setImageResource(R.drawable.ic_lightbulb);

        youtubePlayerView.setVisibility(View.GONE);
        btnActionTip.setVisibility(View.GONE);
    }

    private void showVideo(String title, String url) {
        cardDailyTip.setVisibility(View.VISIBLE);
        tvDailyHeader.setText("Health Video");
        tvDailyMessage.setText(title);
        imgDailyType.setImageResource(R.drawable.ic_lightbulb);

        youtubePlayerView.setVisibility(View.VISIBLE);
        btnActionTip.setVisibility(View.GONE);

        String videoId = extractYoutubeId(url);
        if (videoId == null) return;

        getLifecycle().addObserver(youtubePlayerView);
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f);
            }
        });
    }

    private String extractYoutubeId(String url) {
        if (url == null) return null;
        String pattern = "(?<=youtu.be/|v=)[^&\\n]+";
        java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) return matcher.group();
        return null;
    }

    private void refreshData() { loadPrescriptionData(); fetchDailyTips(); }

    private void loadPrescriptionData() {
        if (!isAdded()) return;

        swipeRefreshLayout.setRefreshing(true);

        if (!isInternetAvailable()) {
            showTip("No internet connection.");
            swipeRefreshLayout.setRefreshing(false);
            showNoMedicineView(true);
            return;
        }

        new PrescriptionController(requireContext()).fetchPrescriptions(new PrescriptionController.PrescriptionCallback() {
            @Override
            public void onSuccess(List<Prescription> prescriptions) {
                medicationList.clear();
                for (Prescription p : prescriptions) {
                    if (p.getMedicines() != null) {
                        for (Medication med : p.getMedicines()) med.calculateDuration();
                        medicationList.addAll(p.getMedicines());
                    }
                }
                adapter.notifyDataSetChanged();
                loadMedicineLogs(sharedPrefManager.getCaseId());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                medicationList.clear();
                adapter.notifyDataSetChanged();
                showNoMedicineView(true);
                showTip("Failed to load prescriptions.");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadMedicineLogs(String caseId) {
        new MedicineLogController(requireContext()).fetchLogs(caseId, new MedicineLogController.MedicineLogCallback() {
            @Override
            public void onSuccess(List<MedicineLog> logs) {
                medicineLogs.clear();
                medicineLogs.addAll(logs);
                updateSummaryCounts();
            }

            @Override
            public void onFailure(String errorMessage) {
                showTip("Failed to load logs.");
                medicineLogs.clear();
                updateSummaryCounts();
            }
        });
    }

    private void updateSummaryCounts() {
        int total = medicineLogs.size();
        int taken = (int) medicineLogs.stream().filter(MedicineLog::isTaken).count();
        int remaining = total - taken;

        tvTotalMedicines.setText(String.valueOf(total));
        tvTakenMedicines.setText(String.valueOf(taken));
        tvRemainingMedicines.setText(String.valueOf(remaining));

        showNoMedicineView(total == 0);
        if (total == 0) showTip("No medicine assigned.");
    }

    private void showNoMedicineView(boolean show) {
        tvNoMedicines.setVisibility(show ? View.VISIBLE : View.GONE);
        rvMedications.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) return;
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14));
            googleMap.addMarker(new MarkerOptions().position(pos).title("You are here"));
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null || cm.getActiveNetwork() == null) return false;
        NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return cap != null && (cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                tvCurrentDate.setText(android.text.format.DateFormat.format("EEEE, MMM dd yyyy", now));
                tvCurrentTime.setText(android.text.format.DateFormat.format("hh:mm a", now));
                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    private void openFullMap() {
        startActivity(new Intent(requireContext(), FullMapActivity.class));
    }

    @Override public void onStart() { super.onStart(); mapView.onStart(); }
    @Override public void onResume() { super.onResume(); mapView.onResume(); }
    @Override public void onPause() { super.onPause(); mapView.onPause(); }
    @Override public void onStop() { super.onStop(); mapView.onStop(); }
    @Override public void onDestroyView() { timeHandler.removeCallbacks(timeRunnable); mapView.onDestroy(); super.onDestroyView(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override public void onSaveInstanceState(@NonNull Bundle out) { super.onSaveInstanceState(out); mapView.onSaveInstanceState(out); }
}
