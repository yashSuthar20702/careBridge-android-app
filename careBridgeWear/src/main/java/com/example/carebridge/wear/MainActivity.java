package com.example.carebridge.wear;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.carebridge.wear.databinding.ActivityMainBinding;
import com.example.carebridge.wear.fragments.HomeFragment;
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import androidx.wear.widget.ConfirmationOverlay;

public class MainActivity extends FragmentActivity {

    private ActivityMainBinding binding;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wearSharedPrefManager = new WearSharedPrefManager(this);

        // Redirect user if not logged in
        if (!wearSharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Load HomeFragment only once
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    public void logout() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);

        TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        AlertDialog dialog =
                new AlertDialog.Builder(this, R.style.WearDialogTheme)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            wearSharedPrefManager.logout();

            new ConfirmationOverlay()
                    .setType(ConfirmationOverlay.SUCCESS_ANIMATION)
                    .showOn(this);

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        });
    }
}
