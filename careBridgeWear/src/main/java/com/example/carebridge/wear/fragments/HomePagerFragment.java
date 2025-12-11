package com.example.carebridge.wear.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carebridge.wear.CallActivity;
import com.example.carebridge.wear.GuardianActivity;
import com.example.carebridge.wear.HealthInfoActivity;
import com.example.carebridge.wear.HealthMonitorActivity;
import com.example.carebridge.wear.MainActivity;
import com.example.carebridge.wear.MedicineActivity;
import com.example.carebridge.wear.MealPlannerWearActivity;
import com.example.carebridge.wear.R;
import com.example.carebridge.wear.databinding.FragmentHomePagerBinding;
import com.example.carebridge.wear.utils.Constants;

/**
 *  HomePagerFragment
 * This fragment represents ONE page inside the ViewPager on the Wear OS home screen.
 * It dynamically changes button icon, label, and click behavior based on position.
 */
public class HomePagerFragment extends Fragment {

    //  ViewBinding object for accessing UI elements safely
    private FragmentHomePagerBinding binding;

    //  Stores the position of the current page
    private int position;

    /**
     *  Factory Method
     * Creates fragment and passes page position using Bundle
     */
    public static HomePagerFragment newInstance(int position) {
        HomePagerFragment fragment = new HomePagerFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_POSITION, position); // Save position
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *  Called when fragment is created
     * Reads the position value from Bundle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(Constants.ARG_POSITION);
        }
    }

    /**
     *  Inflates the UI layout using ViewBinding
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomePagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     *  Called after the view is created
     * Used to setup the button
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupButton(); // Initialize button logic
    }

    /**
     *  Sets button icon, label, and click listener dynamically
     */
    private void setupButton() {
        int[] buttonIcons = getButtonIcons();     // Load all icons
        String[] buttonLabels = getButtonLabels(); // Load all labels

        // Set correct icon & text based on position
        binding.homeMainButton.setImageResource(buttonIcons[position]);
        binding.homeButtonLabel.setText(buttonLabels[position]);

        // Handle click action
        binding.homeMainButton.setOnClickListener(v -> handleButtonClick());
    }

    /**
     *  Returns array of button icons based on feature
     */
    private int[] getButtonIcons() {
        return new int[]{
                R.drawable.ic_phone_scaled,      // 0 - Call
                R.drawable.ic_pill_scaled,       // 1 - Medicine
                R.drawable.ic_meal,              // 2 - Meal Planner
                R.drawable.ic_activity_scaled,  // 3 - Patient Health
                R.drawable.ic_user_scaled,      // 4 - Guardian
                R.drawable.ic_heart_scaled,     // 5 - Health Monitor
                R.drawable.ic_logout_scaled     // 6 - Logout
        };
    }

    /**
     *  Returns array of button labels
     */
    private String[] getButtonLabels() {
        return new String[]{
                getString(R.string.call),             // 0
                getString(R.string.medicine),         // 1
                getString(R.string.meal_planner),     // 2
                getString(R.string.patient_health),  // 3
                getString(R.string.guardian_info),    // 4
                getString(R.string.health_monitor),  // 5
                getString(R.string.logout)            // 6
        };
    }

    /**
     *  Handles navigation based on selected button position
     */
    private void handleButtonClick() {
        switch (position) {

            case 0:
                startActivity(new Intent(requireContext(), CallActivity.class));
                break;

            case 1:
                startActivity(new Intent(requireContext(), MedicineActivity.class));
                break;

            case 2:
                startActivity(new Intent(requireContext(), MealPlannerWearActivity.class));
                break;

            case 3:
                startActivity(new Intent(requireContext(), HealthInfoActivity.class));
                break;

            case 4:
                startActivity(new Intent(requireContext(), GuardianActivity.class));
                break;

            case 5:
                startActivity(new Intent(requireContext(), HealthMonitorActivity.class));
                break;

            case 6:
                handleLogout();
                break;
        }
    }

    /**
     *  Calls logout method from MainActivity
     */
    private void handleLogout() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).logout();
        }
    }

    /**
     *  Clears binding reference to prevent memory leaks
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}