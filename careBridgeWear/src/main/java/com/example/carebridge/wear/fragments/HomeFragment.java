package com.example.carebridge.wear.fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

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

    private View[] indicators;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initIndicators();
        setupViewPager();
        startTimeUpdates();
    }

    private void initIndicators() {
        indicators = new View[]{
                binding.homeIndicator0,
                binding.homeIndicator1,
                binding.homeIndicator2,
                binding.homeIndicator3,
                binding.homeIndicator4,
                binding.homeIndicator5  // ADD THIS: 6th indicator
        };

        for (int i = 0; i < indicators.length; i++) {
            int index = i;
            indicators[i].setOnClickListener(v -> binding.viewPager.setCurrentItem(index, true));
        }
    }

    private void setupViewPager() {
        HomePagerAdapter adapter = new HomePagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });

        updateIndicators(0);
    }

    private void updateIndicators(int activePos) {
        for (int i = 0; i < indicators.length; i++) {
            View indicator = indicators[i];

            if (i == activePos) {
                indicator.setBackgroundResource(R.drawable.indicator_active);
                animateActive(indicator);
            } else {
                indicator.setBackgroundResource(R.drawable.indicator_inactive);
                animateInactive(indicator);
            }

            animateMargin(indicator, i == activePos);
        }
    }

    // --- ANIMATIONS ---

    private void animateActive(View view) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.25f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.25f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        anim.setDuration(250);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    private void animateInactive(View view) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.25f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.25f, 1f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        anim.setDuration(200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    // Smooth sliding margin animation
    private void animateMargin(View view, boolean isActive) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        int start = params.leftMargin;
        int end = isActive ? 16 : 8; // active dot spreads more

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(250);
        animator.addUpdateListener(animation -> {
            params.leftMargin = (int) animation.getAnimatedValue();
            params.rightMargin = (int) animation.getAnimatedValue();
            view.setLayoutParams(params);
        });

        animator.start();
    }

    // --- TIME ---

    private void startTimeUpdates() {
        timeHandler = new Handler();
        timeRunnable = () -> {
            updateTime();
            timeHandler.postDelayed(timeRunnable, 1000);
        };
        timeHandler.post(timeRunnable);
    }

    private void updateTime() {
        if (binding == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        binding.homeTime.setText(sdf.format(new Date()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timeHandler != null) timeHandler.removeCallbacks(timeRunnable);
        binding = null;
    }
}