package com.example.carebridge.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("update_medicine_status.php")
    Call<String> updateMedicineStatus(
            @Field("log_id") String logId,
            @Field("status") String status
    );
}
