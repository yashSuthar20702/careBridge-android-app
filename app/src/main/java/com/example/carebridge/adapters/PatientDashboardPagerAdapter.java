package com.example.carebridge.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.carebridge.fragment.GuardianInfoFragment;
import com.example.carebridge.fragment.HomeFragment;
import com.example.carebridge.fragment.PersonalInfoFragment;

public class PatientDashboardPagerAdapter extends FragmentStateAdapter {

    public PatientDashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new PersonalInfoFragment();
            case 2:
                return new GuardianInfoFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
