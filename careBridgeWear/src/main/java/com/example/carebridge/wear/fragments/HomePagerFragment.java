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
import com.example.carebridge.wear.R;
import com.example.carebridge.wear.databinding.FragmentHomePagerBinding;
import com.example.carebridge.wear.utils.Constants;

public class HomePagerFragment extends Fragment {

    private FragmentHomePagerBinding binding;
    private int position;

    public static HomePagerFragment newInstance(int position) {
        HomePagerFragment fragment = new HomePagerFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(Constants.ARG_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomePagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupButton();
    }

    /**
     * Set up button icon, label, and click handler based on position
     */
    private void setupButton() {
        int[] buttonIcons = getButtonIcons();
        String[] buttonLabels = getButtonLabels();

        binding.homeMainButton.setImageResource(buttonIcons[position]);
        binding.homeButtonLabel.setText(buttonLabels[position]);

        binding.homeMainButton.setOnClickListener(v -> handleButtonClick());
    }

    /**
     * Get array of button icons for each position
     */
    private int[] getButtonIcons() {
        return new int[]{
                R.drawable.ic_phone,
                R.drawable.ic_pill,
                R.drawable.ic_activity,
                R.drawable.ic_user,
                R.drawable.ic_heart,
                R.drawable.ic_logout
        };
    }

    /**
     * Get array of button labels for each position
     */
    private String[] getButtonLabels() {
        return new String[]{
                getString(R.string.call),
                getString(R.string.medicine),
                getString(R.string.patient_health),
                getString(R.string.guardian_info),
                getString(R.string.health_monitor),
                getString(R.string.logout)
        };
    }

    /**
     * Handle button click based on position
     */
    private void handleButtonClick() {
        switch (position) {
            case Constants.BUTTON_CALL:
                startActivity(new Intent(requireContext(), CallActivity.class));
                break;
            case Constants.BUTTON_MEDICINE:
                startActivity(new Intent(requireContext(), MedicineActivity.class));
                break;
            case Constants.BUTTON_PATIENT_HEALTH:
                startActivity(new Intent(requireContext(), HealthInfoActivity.class));
                break;
            case Constants.BUTTON_GUARDIAN_INFO:
                startActivity(new Intent(requireContext(), GuardianActivity.class));
                break;
            case Constants.BUTTON_HEALTH_MONITOR:
                startActivity(new Intent(requireContext(), HealthMonitorActivity.class));
                break;
            case Constants.BUTTON_LOGOUT:
                handleLogout();
                break;
        }
    }

    /**
     * Handle logout action
     */
    private void handleLogout() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).logout();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}