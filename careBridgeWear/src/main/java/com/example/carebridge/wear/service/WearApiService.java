package com.example.carebridge.wear.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * WearApiService

 * Retrofit interface that defines all
 * backend API endpoints used by the
 * Wear OS application.

 * This interface handles medicine-related
 * operations such as updating taken status.
 */
public interface WearApiService {

    /**
     * Updates the taken status of a medicine.
     *
     * @param logId        Unique ID of the medicine log entry
     * @param takenStatus Status value (e.g., "taken" or "not_taken")
     * @return JSON response from the backend
     */
    @FormUrlEncoded
    @POST("updateStatus.php")
    Call<JsonObject> updateMedicineStatus(
            @Field("log_id") String logId,
            @Field("taken_status") String takenStatus
    );
}