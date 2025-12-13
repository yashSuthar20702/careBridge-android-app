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
 * HomePagerFragment

 * Represents a single page inside the Wear OS home ViewPager.
 * Displays one main action button based on the current position.
 */
public class HomePagerFragment extends Fragment {

    private FragmentHomePagerBinding binding;
    private int position = Constants.POSITION_FIRST;

    /**
     * Factory method to create fragment with page position.
     */
    public static HomePagerFragment newInstance(int position) {
        HomePagerFragment fragment = new HomePagerFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Reads page position from arguments.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getInt(
                    Constants.ARG_POSITION,
                    Constants.POSITION_FIRST
            );
        }
    }

    /**
     * Inflates layout using ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHomePagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes button after view creation.
     */
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        setupButton();
    }

    /**
     * Configures button icon, label, and click action.
     */
    private void setupButton() {
        int[] buttonIcons = getButtonIcons();
        int[] buttonLabels = getButtonLabels();

        if (position >= buttonIcons.length) {
            return;
        }

        binding.homeMainButton.setImageResource(
                buttonIcons[position]
        );

        binding.homeButtonLabel.setText(
                getString(buttonLabels[position])
        );

        binding.homeMainButton.setOnClickListener(
                v -> handleButtonClick()
        );
    }

    /**
     * Returns drawable resources for each page.
     */
    private int[] getButtonIcons() {
        return new int[]{
                R.drawable.ic_phone_scaled,
                R.drawable.ic_pill_scaled,
                R.drawable.ic_meal,
                R.drawable.ic_activity_scaled,
                R.drawable.ic_user_scaled,
                R.drawable.ic_heart_scaled,
                R.drawable.ic_logout_scaled
        };
    }

    /**
     * Returns string resources for each page label.
     */
    private int[] getButtonLabels() {
        return new int[]{
                R.string.call,
                R.string.medicine,
                R.string.meal_planner,
                R.string.patient_health,
                R.string.guardian_info,
                R.string.health_monitor,
                R.string.logout
        };
    }

    /**
     * Handles navigation based on page position.
     */
    private void handleButtonClick() {
        Intent intent = null;

        switch (position) {

            case Constants.BUTTON_CALL:
                intent = new Intent(requireContext(), CallActivity.class);
                break;

            case Constants.BUTTON_MEDICINE:
                intent = new Intent(requireContext(), MedicineActivity.class);
                break;

            case Constants.BUTTON_MEAL:
                intent = new Intent(requireContext(), MealPlannerWearActivity.class);
                break;

            case Constants.BUTTON_PATIENT_HEALTH:
                intent = new Intent(requireContext(), HealthInfoActivity.class);
                break;

            case Constants.BUTTON_GUARDIAN_INFO:
                intent = new Intent(requireContext(), GuardianActivity.class);
                break;

            case Constants.BUTTON_HEALTH_MONITOR:
                intent = new Intent(requireContext(), HealthMonitorActivity.class);
                break;

            case Constants.BUTTON_LOGOUT:
                handleLogout();
                return;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    /**
     * Triggers logout via MainActivity.
     */
    private void handleLogout() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).logout();
        }
    }

    /**
     * Clears ViewBinding reference.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}