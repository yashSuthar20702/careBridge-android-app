package com.example.carebridge.wear;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.example.carebridge.wear.fragments.*;

public class MainActivity extends Activity implements BaseFragment.FragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load home fragment initially
        showHomeFragment();
    }

    private void showHomeFragment() {
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.commit();
    }

    @Override
    public void onNavigateToScreen(int page) {
        navigateToFragment(page);
    }

    public void navigateToFragment(int page) {
        android.app.Fragment fragment;

        switch (page) {
            case 0:
                fragment = new CallFragment();
                break;
            case 1:
                fragment = new MedicineFragment();
                break;
            case 2:
                fragment = new PatientHealthFragment();
                break;
            case 3:
                fragment = new GuardianFragment();
                break;
            default:
                fragment = new CallFragment();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackToHome() {
        getFragmentManager().popBackStackImmediate(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
        showHomeFragment();
    }

    @Override
    public void onMakeCall(String phoneNumber) {
        // Handle phone calls if needed
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            onBackToHome();
        } else {
            super.onBackPressed();
        }
    }
}