package com.example.carebridge.wear;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.shared.controller.MealController;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.example.carebridge.wear.adapters.MealAdapter;
import com.example.carebridge.wear.models.MealItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MealPlannerWearActivity extends AppCompatActivity {

    private static final String TAG = "MealPlannerWearActivity";

    private MealController mealController;
    private String patientId;

    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private List<MealItem> meals;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_planner_wear);

        mealController = new MealController();

        // RecyclerView setup
        recyclerView = findViewById(R.id.meal_recycler_view);
        meals = new ArrayList<>();
        adapter = new MealAdapter(meals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Get patientId from SharedPrefManager
        SharedPrefManager sharedPref = new SharedPrefManager(this);
        patientId = sharedPref.getReferenceId();

        if (patientId != null && !patientId.isEmpty()) {
            fetchMealPlan(patientId);
        } else {
            Toast.makeText(this, "Patient ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMealPlan(String patientId) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading meal plan...");
        progress.setCancelable(false);
        progress.show();

        // Fetch meal plan by case_id only
        mealController.fetchMealPlanByCaseId(patientId, new MealController.MealFetchCallback() {
            @Override
            public void onSuccess(JSONObject mealPlan) {
                progress.dismiss();

                try {
                    meals.clear();

                    meals.add(new MealItem("Morning", "08:00 AM", mealPlan.optString("morning_meal", "-")));
                    meals.add(new MealItem("Afternoon", "01:00 PM", mealPlan.optString("afternoon_meal", "-")));
                    meals.add(new MealItem("Evening", "06:00 PM", mealPlan.optString("evening_meal", "-")));
                    meals.add(new MealItem("Night", "09:00 PM", mealPlan.optString("night_meal", "-")));

                    adapter.notifyDataSetChanged();  // Important! Updates the RecyclerView

                    Log.d(TAG, "Meal plan loaded successfully");

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing meal plan JSON", e);
                    Toast.makeText(MealPlannerWearActivity.this, "Failed to load meal plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String message) {
                progress.dismiss();
                Toast.makeText(MealPlannerWearActivity.this, "Failed: " + message, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Meal fetch failed: " + message);
            }
        });
    }
}
