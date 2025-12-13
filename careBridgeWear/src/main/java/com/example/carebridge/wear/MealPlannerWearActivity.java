package com.example.carebridge.wear;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.shared.controller.MealController;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.example.carebridge.wear.adapters.MealAdapter;
import com.example.carebridge.wear.databinding.ActivityMealPlannerWearBinding;
import com.example.carebridge.wear.models.MealItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * MealPlannerWearActivity

 * Displays the daily meal plan for a patient on Wear OS.
 * The meal data is fetched from the backend using the
 * patient (case) ID stored in Shared Preferences.
 */
public class MealPlannerWearActivity extends AppCompatActivity {

    private static final String TAG = "MealPlannerWearActivity";

    private ActivityMealPlannerWearBinding binding;
    private MealController mealController;

    private MealAdapter adapter;
    private final List<MealItem> meals = new ArrayList<>();

    private String patientId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityMealPlannerWearBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize controller responsible for API calls
        mealController = new MealController();

        setupRecyclerView();
        loadPatientIdAndFetchMeals();
    }

    /**
     * Sets up the RecyclerView for displaying meal items
     */
    private void setupRecyclerView() {
        adapter = new MealAdapter(meals);
        binding.mealRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.mealRecyclerView.setAdapter(adapter);
    }

    /**
     * Retrieves patient ID from Shared Preferences
     * and fetches the meal plan if available
     */
    private void loadPatientIdAndFetchMeals() {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        patientId = sharedPrefManager.getReferenceId();

        if (patientId != null && !patientId.isEmpty()) {
            fetchMealPlan(patientId);
        } else {
            Toast.makeText(
                    this,
                    getString(R.string.error_patient_id_not_found),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Fetches the meal plan for the given patient ID
     */
    private void fetchMealPlan(String patientId) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_meal_plan));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mealController.fetchMealPlanByCaseId(
                patientId,
                new MealController.MealFetchCallback() {

                    @Override
                    public void onSuccess(JSONObject mealPlan) {
                        progressDialog.dismiss();

                        try {
                            meals.clear();

                            // Map backend response to UI model
                            meals.add(new MealItem(
                                    getString(R.string.meal_morning),
                                    getString(R.string.meal_time_morning),
                                    mealPlan.optString("morning_meal", getString(R.string.meal_not_available))
                            ));

                            meals.add(new MealItem(
                                    getString(R.string.meal_afternoon),
                                    getString(R.string.meal_time_afternoon),
                                    mealPlan.optString("afternoon_meal", getString(R.string.meal_not_available))
                            ));

                            meals.add(new MealItem(
                                    getString(R.string.meal_evening),
                                    getString(R.string.meal_time_evening),
                                    mealPlan.optString("evening_meal", getString(R.string.meal_not_available))
                            ));

                            meals.add(new MealItem(
                                    getString(R.string.meal_night),
                                    getString(R.string.meal_time_night),
                                    mealPlan.optString("night_meal", getString(R.string.meal_not_available))
                            ));

                            adapter.notifyDataSetChanged();

                            Log.d(TAG, "Meal plan loaded successfully");

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing meal plan JSON", e);
                            Toast.makeText(
                                    MealPlannerWearActivity.this,
                                    getString(R.string.error_loading_meal_plan),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        progressDialog.dismiss();

                        Toast.makeText(
                                MealPlannerWearActivity.this,
                                getString(R.string.error_meal_fetch_failed, message),
                                Toast.LENGTH_SHORT
                        ).show();

                        Log.e(TAG, "Meal fetch failed: " + message);
                    }
                }
        );
    }
}