package com.example.carebridge.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.R;
import com.example.carebridge.adapters.NearbyPlacesAdapter;
import com.example.carebridge.model.NearbyPlace;
import com.example.carebridge.model.NearbySearchResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FullMapActivity extends AppCompatActivity {

    private static final String TAG = "FullMap_Debug";
    private static final int LOCATION_PERMISSION = 200;

    private MapView mapView;
    private GoogleMap googleMap;

    private FusedLocationProviderClient fusedLocationClient;

    private RecyclerView rvNearbyPlaces;
    private List<NearbyPlace> nearbyPlacesList = new ArrayList<>();
    private NearbyPlacesAdapter adapter;

    private NearbyPlacesApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);

        mapView = findViewById(R.id.fullMapView);
        mapView.onCreate(savedInstanceState);

        rvNearbyPlaces = findViewById(R.id.rvNearbyPlaces);
        rvNearbyPlaces.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NearbyPlacesAdapter(this, nearbyPlacesList, null);
        rvNearbyPlaces.setAdapter(adapter);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupRetrofit();

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            Log.e(TAG, "MapsInitializer error", e);
        }

        mapView.getMapAsync(map -> {
            googleMap = map;

            // Apply device theme map style
            int currentNightMode = getResources().getConfiguration().uiMode
                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

            int styleRes = currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
                    ? R.raw.map_style_dark
                    : R.raw.map_style_light;

            try {
                boolean success = googleMap.setMapStyle(
                        com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle(this, styleRes)
                );
                if (!success) Log.e(TAG, "Failed to parse map style.");
            } catch (Exception e) {
                Log.e(TAG, "Cannot find map style. Error: ", e);
            }

            adapter.setMapReference(googleMap);
            enableUserLocation();
        });
    }

    private void setupRetrofit() {
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(NearbyPlacesApi.class);
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION
            );
            return;
        }

        googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) return;

            LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user, 15));
            googleMap.addMarker(new MarkerOptions().position(user).title("You are here"));

            fetchNearbyPlaces(location);
        });
    }

    private void fetchNearbyPlaces(Location userLocation) {
        String loc = userLocation.getLatitude() + "," + userLocation.getLongitude();

        Call<NearbySearchResponse> call = api.getNearbyPlaces(
                loc,
                10000,
                "hospital|pharmacy",
                getString(R.string.google_maps_key)
        );

        call.enqueue(new Callback<NearbySearchResponse>() {
            @Override
            public void onResponse(Call<NearbySearchResponse> call, Response<NearbySearchResponse> response) {

                if (!response.isSuccessful()) return;

                nearbyPlacesList.clear();
                googleMap.clear();

                googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                                .title("You are here")
                );

                NearbySearchResponse data = response.body();
                if (data == null || data.results == null) return;

                for (NearbySearchResponse.Result r : data.results) {
                    double lat = r.geometry.location.lat;
                    double lng = r.geometry.location.lng;

                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(r.name)
                    );

                    // Calculate distance
                    float[] results = new float[1];
                    Location.distanceBetween(
                            userLocation.getLatitude(),
                            userLocation.getLongitude(),
                            lat,
                            lng,
                            results
                    );

                    float distanceInMeters = results[0];
                    String distanceText = distanceInMeters >= 1000
                            ? String.format("%.1f km away", distanceInMeters / 1000)
                            : String.format("%.0f m away", distanceInMeters);

                    // Add NearbyPlace with distance and address
                    nearbyPlacesList.add(
                            new NearbyPlace(r.name, new LatLng(lat, lng), distanceText, r.vicinity)
                    );
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NearbySearchResponse> call, Throwable t) {
                Log.e(TAG, "API Failure", t);
            }
        });
    }

    // Lifecycle
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
