package com.example.carebridge.wear.fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.carebridge.wear.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * HomeFragment

 * Displays the main Wear OS home screen with:
 * - ViewPager navigation
 * - Animated page indicators
 * - Live time display
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler timeHandler;
    private Runnable timeRunnable;
    private View[] indicators;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        initIndicators();
        setupViewPager();
        startTimeUpdates();
    }

    /**
     * Initializes page indicators and click navigation.
     */
    private void initIndicators() {
        indicators = new View[]{
                binding.homeIndicator0,
                binding.homeIndicator1,
                binding.homeIndicator2,
                binding.homeIndicator3,
                binding.homeIndicator4,
                binding.homeIndicator5,
                binding.homeIndicator6
        };

        for (int i = Constants.POSITION_FIRST; i < indicators.length; i++) {
            final int index = i;
            indicators[i].setOnClickListener(
                    v -> binding.viewPager.setCurrentItem(index, true)
            );
        }
    }

    /**
     * Sets up ViewPager with FragmentStateAdapter.
     */
    private void setupViewPager() {
        HomePagerAdapter adapter =
                new HomePagerAdapter(requireActivity());

        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        updateIndicators(position);
                    }
                }
        );

        updateIndicators(Constants.POSITION_FIRST);
    }

    /**
     * Updates indicator appearance based on selected page.
     */
    private void updateIndicators(int activePosition) {
        for (int i = Constants.POSITION_FIRST; i < indicators.length; i++) {
            View indicator = indicators[i];

            if (i == activePosition) {
                indicator.setBackgroundResource(
                        R.drawable.indicator_active
                );
                animateActive(indicator);
            } else {
                indicator.setBackgroundResource(
                        R.drawable.indicator_inactive
                );
                animateInactive(indicator);
            }

            animateMargin(indicator, i == activePosition);
        }
    }

    /**
     * Scales indicator up when active.
     */
    private void animateActive(View view) {
        ObjectAnimator animator =
                ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(
                                View.SCALE_X,
                                Constants.FLOAT_SCALE_INACTIVE,
                                Constants.FLOAT_SCALE_ACTIVE
                        ),
                        PropertyValuesHolder.ofFloat(
                                View.SCALE_Y,
                                Constants.FLOAT_SCALE_INACTIVE,
                                Constants.FLOAT_SCALE_ACTIVE
                        )
                );

        animator.setDuration(Constants.ANIMATION_DURATION_MEDIUM);
        animator.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );
        animator.start();
    }

    /**
     * Scales indicator down when inactive.
     */
    private void animateInactive(View view) {
        ObjectAnimator animator =
                ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(
                                View.SCALE_X,
                                Constants.FLOAT_SCALE_ACTIVE,
                                Constants.FLOAT_SCALE_INACTIVE
                        ),
                        PropertyValuesHolder.ofFloat(
                                View.SCALE_Y,
                                Constants.FLOAT_SCALE_ACTIVE,
                                Constants.FLOAT_SCALE_INACTIVE
                        )
                );

        animator.setDuration(Constants.ANIMATION_DURATION_SHORT);
        animator.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );
        animator.start();
    }

    /**
     * Animates margin spacing for active/inactive indicators.
     */
    private void animateMargin(View view, boolean isActive) {
        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        int startMargin = params.leftMargin;
        int endMargin = isActive
                ? Constants.INDICATOR_MARGIN_ACTIVE
                : Constants.INDICATOR_MARGIN_INACTIVE;

        ValueAnimator animator =
                ValueAnimator.ofInt(startMargin, endMargin);

        animator.setDuration(Constants.ANIMATION_DURATION_MEDIUM);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            params.leftMargin = value;
            params.rightMargin = value;
            view.setLayoutParams(params);
        });

        animator.start();
    }

    /**
     * Starts handler to update time every second.
     */
    private void startTimeUpdates() {
        timeHandler = new Handler(Looper.getMainLooper());

        timeRunnable = () -> {
            updateTime();
            timeHandler.postDelayed(
                    timeRunnable,
                    Constants.UPDATE_INTERVAL_FAST
            );
        };

        timeHandler.post(timeRunnable);
    }

    /**
     * Updates current time on home screen.
     */
    private void updateTime() {
        if (binding == null) return;

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        Constants.TIME_FORMAT_HH_MM,
                        Locale.getDefault()
                );

        binding.homeTime.setText(
                formatter.format(new Date())
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }

        binding = null;
    }
}