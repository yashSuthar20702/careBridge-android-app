package com.example.carebridge.wear.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
    protected FragmentInteractionListener listener;

    public interface FragmentInteractionListener {
        void onBackToHome();
        void onMakeCall(String phoneNumber);
        void onNavigateToScreen(int page);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    protected abstract int getLayoutRes();
    protected abstract void initializeViews(View view);
    protected abstract void setupSwipeGestures(View view);
}