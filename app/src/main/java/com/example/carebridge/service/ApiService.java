package com.example.carebridge.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("updateStatus.php")
    Call<JsonObject> updateMedicineStatus(
            @Field("log_id") String logId,
            @Field("taken_status") String takenStatus
    );

}
