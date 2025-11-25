package com.example.carebridge.wear.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.adapters.HomePagerAdapter;
import com.example.carebridge.wear.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler timeHandler;
    private Runnable timeRunnable;
    private HomePagerAdapter homePagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewPager();
        startTimeUpdates();
    }

    private void setupViewPager() {
        homePagerAdapter = new HomePagerAdapter(this);
        binding.viewPager.setAdapter(homePagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });

        // Set up indicator clicks
        binding.homeIndicator0.setOnClickListener(v -> binding.viewPager.setCurrentItem(0, true));
        binding.homeIndicator1.setOnClickListener(v -> binding.viewPager.setCurrentItem(1, true));
        binding.homeIndicator2.setOnClickListener(v -> binding.viewPager.setCurrentItem(2, true));
        binding.homeIndicator3.setOnClickListener(v -> binding.viewPager.setCurrentItem(3, true));
        binding.homeIndicator4.setOnClickListener(v -> binding.viewPager.setCurrentItem(4, true));
    }

    private void updateIndicators(int position) {
        binding.homeIndicator0.setBackgroundResource(position == 0 ? R.drawable.indicator_active : R.drawable.indicator_inactive);
        binding.homeIndicator1.setBackgroundResource(position == 1 ? R.drawable.indicator_active : R.drawable.indicator_inactive);
        binding.homeIndicator2.setBackgroundResource(position == 2 ? R.drawable.indicator_active : R.drawable.indicator_inactive);
        binding.homeIndicator3.setBackgroundResource(position == 3 ? R.drawable.indicator_active : R.drawable.indicator_inactive);
        binding.homeIndicator4.setBackgroundResource(position == 4 ? R.drawable.indicator_active : R.drawable.indicator_inactive);

    }

    private void startTimeUpdates() {
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        binding.homeTime.setText(currentTime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timeHandler != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
        binding = null;
    }
}