package com.example.carebridge.wear.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.carebridge.wear.fragments.HomePagerFragment;
import com.example.carebridge.wear.utils.Constants;

/**
 * Adapter for Wear OS home ViewPager.
 * Responsible for providing fragments based on pager position.
 */
public class HomePagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor receives FragmentActivity required by FragmentStateAdapter.
     */
    public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Creates and returns a fragment for the given position.
     * Each page uses the same fragment class with different position data.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return HomePagerFragment.newInstance(position);
    }

    /**
     * Returns total number of pages in the home pager.
     * Value is centralized in Constants to avoid hardcoding.
     */
    @Override
    public int getItemCount() {
        return Constants.HOME_PAGER_COUNT;
    }
}