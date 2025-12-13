package com.example.carebridge.wear.service;

import com.example.carebridge.wear.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * WearApiClient

 * Provides a singleton Retrofit instance for
 * Wear OS network communication.

 * All configuration values are centralized
 * using Constants to avoid hardcoded values.
 */
public final class WearApiClient {

    // Single Retrofit instance (Singleton pattern)
    private static Retrofit retrofit;

    // Private constructor to prevent instantiation
    private WearApiClient() {
    }

    /**
     * Returns a singleton Retrofit client.
     *
     * @return Retrofit instance configured for Wear OS
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}