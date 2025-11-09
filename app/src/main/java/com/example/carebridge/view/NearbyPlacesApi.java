package com.example.carebridge.view;

import com.example.carebridge.model.NearbySearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbyPlacesApi {

    @GET("maps/api/place/nearbysearch/json")
    Call<NearbySearchResponse> getNearbyPlaces(
            @Query("location") String location, // "lat,lng"
            @Query("radius") int radius,        // meters
            @Query("types") String types,       // hospital|pharmacy
            @Query("key") String apiKey
    );
}
