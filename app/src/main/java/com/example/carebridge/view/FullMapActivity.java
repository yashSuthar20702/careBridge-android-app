package com.example.carebridge.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.R;
import com.example.carebridge.adapters.NearbyPlacesAdapter;
import com.example.carebridge.view.NearbyPlacesApi;
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

        Log.d(TAG, "Activity started");

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
            Log.d(TAG, "MapsInitializer success");
        } catch (Exception e) {
            Log.e(TAG, "MapsInitializer error", e);
        }

        mapView.getMapAsync(map -> {
            googleMap = map;
            Log.d(TAG, "GoogleMap ready");
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
        Log.d(TAG, "Checking permission...");

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION
            );
            return;
        }

        googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Log.e(TAG, "Location is NULL");
                return;
            }

            Log.d(TAG, "User location: " + location.getLatitude() + ", " + location.getLongitude());

            LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user, 15));
            googleMap.addMarker(new MarkerOptions().position(user).title("You are here"));

            fetchNearbyPlaces(location);
        });
    }

    private void fetchNearbyPlaces(Location userLocation) {
        Log.d(TAG, "Fetching REAL nearby places via NearbySearch API...");

        String loc = userLocation.getLatitude() + "," + userLocation.getLongitude();

        Call<NearbySearchResponse> call = api.getNearbyPlaces(
                loc,
                2000,               // 2km radius
                "hospital|pharmacy",
                getString(R.string.google_maps_key)
        );

        call.enqueue(new Callback<NearbySearchResponse>() {
            @Override
            public void onResponse(Call<NearbySearchResponse> call, Response<NearbySearchResponse> response) {

                if (!response.isSuccessful()) {
                    Log.e(TAG, "API Error: " + response.code());
                    return;
                }

                nearbyPlacesList.clear();
                googleMap.clear();

                googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                                .title("You are here")
                );

                NearbySearchResponse data = response.body();

                if (data == null || data.results == null) {
                    Log.e(TAG, "Empty response");
                    return;
                }

                Log.d(TAG, "✅ Places found: " + data.results.size());

                for (NearbySearchResponse.Result r : data.results) {

                    double lat = r.geometry.location.lat;
                    double lng = r.geometry.location.lng;

                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(r.name)
                    );

                    nearbyPlacesList.add(
                            new NearbyPlace(r.name, new LatLng(lat, lng), r.vicinity)
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
