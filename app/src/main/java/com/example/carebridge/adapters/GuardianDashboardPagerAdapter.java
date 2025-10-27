package com.example.carebridge.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.carebridge.fragment.GuardianHomeFragment;
import com.example.carebridge.fragment.GuardianPersonalFragment;
import com.example.carebridge.fragment.GuardianPatientsFragment;

public class GuardianDashboardPagerAdapter extends FragmentStateAdapter {

    public GuardianDashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new GuardianPersonalFragment();
            case 2:
                return new GuardianPatientsFragment();
            default:
                return new GuardianHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
