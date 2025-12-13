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
import com.example.carebridge.wear.R;
import com.example.carebridge.wear.adapters.MealAdapter;
import com.example.carebridge.wear.databinding.FragmentMealPlannerBinding;
import com.example.carebridge.wear.models.MealItem;
import com.example.carebridge.wear.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * MealPlannerFragment
 * Displays daily meal plan for the patient on Wear OS.
 * Fetches data from backend using patient case ID.
 */
public class MealPlannerFragment extends Fragment {

    private static final String TAG = "MealPlannerFragment";

    private FragmentMealPlannerBinding binding;
    private MealController mealController;
    private MealAdapter adapter;
    private final List<MealItem> meals = new ArrayList<>();
    private String patientId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        binding = FragmentMealPlannerBinding.inflate(inflater, container, false);

        setupRecyclerView();
        initializeController();
        loadPatientAndFetchMeals();

        return binding.getRoot();
    }

    /**
     * Initializes RecyclerView and adapter.
     */
    private void setupRecyclerView() {
        adapter = new MealAdapter(meals);
        binding.mealRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.mealRecyclerView.setAdapter(adapter);
    }

    /**
     * Initializes MealController.
     */
    private void initializeController() {
        mealController = new MealController();
    }

    /**
     * Loads patient ID and validates it before API call.
     */
    private void loadPatientAndFetchMeals() {
        SharedPrefManager sharedPrefManager =
                new SharedPrefManager(requireContext());

        patientId = sharedPrefManager.getReferenceId();

        if (patientId == null || patientId.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.error_patient_id_not_found),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        fetchMealPlan(patientId);
    }

    /**
     * Fetches meal plan from backend using case ID.
     */
    private void fetchMealPlan(@NonNull String patientId) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage(
                getString(R.string.loading_meal_plan)
        );
        progressDialog.setCancelable(false);
        progressDialog.show();

        mealController.fetchMealPlanByCaseId(
                patientId,
                new MealController.MealFetchCallback() {

                    @Override
                    public void onSuccess(JSONObject mealPlan) {
                        progressDialog.dismiss();
                        parseAndDisplayMeals(mealPlan);
                    }

                    @Override
                    public void onFailure(String message) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.error_failed_with_message, message),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    /**
     * Parses meal JSON and updates RecyclerView.
     */
    private void parseAndDisplayMeals(@NonNull JSONObject mealPlan) {
        try {
            meals.clear();

            meals.add(new MealItem(
                    getString(R.string.meal_morning),
                    Constants.MEAL_TIME_BREAKFAST,
                    mealPlan.optString(
                            Constants.JSON_MORNING_MEAL,
                            Constants.VALUE_NOT_AVAILABLE
                    )
            ));

            meals.add(new MealItem(
                    getString(R.string.meal_afternoon),
                    Constants.MEAL_TIME_LUNCH,
                    mealPlan.optString(
                            Constants.JSON_AFTERNOON_MEAL,
                            Constants.VALUE_NOT_AVAILABLE
                    )
            ));

            meals.add(new MealItem(
                    getString(R.string.meal_evening),
                    Constants.MEAL_TIME_EVENING,
                    mealPlan.optString(
                            Constants.JSON_EVENING_MEAL,
                            Constants.VALUE_NOT_AVAILABLE
                    )
            ));

            meals.add(new MealItem(
                    getString(R.string.meal_night),
                    Constants.MEAL_TIME_DINNER,
                    mealPlan.optString(
                            Constants.JSON_NIGHT_MEAL,
                            Constants.VALUE_NOT_AVAILABLE
                    )
            ));

            adapter.notifyDataSetChanged();
            Log.d(TAG, getString(R.string.log_meal_plan_loaded));

        } catch (Exception e) {
            Log.e(TAG, getString(R.string.log_meal_parse_error), e);
            Toast.makeText(
                    requireContext(),
                    getString(R.string.error_failed_load_meal_plan),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}