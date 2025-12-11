package com.example.carebridge.wear.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.shared.controller.MealController;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.example.carebridge.wear.adapters.MealAdapter;
import com.example.carebridge.wear.databinding.FragmentMealPlannerBinding;
import com.example.carebridge.wear.models.MealItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MealPlannerFragment extends Fragment {

    private static final String TAG = "MealPlannerFragment";

    private FragmentMealPlannerBinding binding;
    private MealController mealController;
    private MealAdapter adapter;
    private List<MealItem> meals;
    private String patientId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentMealPlannerBinding.inflate(inflater, container, false);

        mealController = new MealController();
        meals = new ArrayList<>();
        adapter = new MealAdapter(meals);

        binding.mealRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.mealRecyclerView.setAdapter(adapter);

        // Get patientId
        SharedPrefManager sharedPref = new SharedPrefManager(requireContext());
        patientId = sharedPref.getReferenceId();

        if (patientId != null && !patientId.isEmpty()) {
            fetchMealPlan(patientId);
        } else {
            Toast.makeText(getContext(), "Patient ID not found", Toast.LENGTH_SHORT).show();
        }

        return binding.getRoot();
    }

    private void fetchMealPlan(String patientId) {
        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading meal plan...");
        progress.setCancelable(false);
        progress.show();

        // Call the API method that fetches meal plan by case_id only
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

                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Meal plan loaded successfully");

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing meal plan JSON", e);
                    Toast.makeText(getContext(), "Failed to load meal plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String message) {
                progress.dismiss();
                Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
