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

public class HomePagerFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private FragmentHomePagerBinding binding;
    private int position;

    public static HomePagerFragment newInstance(int position) {
        HomePagerFragment fragment = new HomePagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
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

    private void setupButton() {
        int[] buttonIcons = {
                R.drawable.ic_phone,
                R.drawable.ic_pill,
                R.drawable.ic_activity,
                R.drawable.ic_user,
                R.drawable.ic_heart,  // Health Monitor icon
                R.drawable.ic_logout
        };

        String[] buttonLabels = {
                "Call",
                "Medicine",
                "Patient Info",
                "Guardian Info",
                "Health Monitor",  // New label
                "Logout"
        };

        binding.homeMainButton.setImageResource(buttonIcons[position]);
        binding.homeButtonLabel.setText(buttonLabels[position]);

        binding.homeMainButton.setOnClickListener(v -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(requireContext(), CallActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(requireContext(), MedicineActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(requireContext(), HealthInfoActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(requireContext(), GuardianActivity.class));
                    break;
                case 4:
                    // Health Monitor
                    startActivity(new Intent(requireContext(), HealthMonitorActivity.class));
                    break;
                case 5:
                    // Handle logout
                    if (requireActivity() instanceof MainActivity) {
                        ((MainActivity) requireActivity()).logout();
                    }
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}