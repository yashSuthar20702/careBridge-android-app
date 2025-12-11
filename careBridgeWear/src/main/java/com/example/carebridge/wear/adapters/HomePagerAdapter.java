package com.example.carebridge.wear.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.carebridge.wear.fragments.HomePagerFragment;
import com.example.carebridge.wear.utils.Constants;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // ✅ ALWAYS show ROUND BUTTON for every page
        return HomePagerFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return Constants.HOME_PAGER_COUNT; // ✅ MUST = 7
    }
}