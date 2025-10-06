package com.example.carebridge.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.carebridge.R;
import com.example.carebridge.controller.AuthController;
import com.example.carebridge.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private TextInputLayout tilUsername, tilPassword;
    private MaterialButton btnLogin;
    private LottieAnimationView loadingAnimation, successAnimation;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupAnimations();
        setupClickListeners();
        setupBackPressedHandler();

        authController = new AuthController(this);
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        btnLogin = findViewById(R.id.btnLogin);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        successAnimation = findViewById(R.id.successAnimation);
    }

    private void setupAnimations() {
        View loginCard = findViewById(R.id.loginCard);
        loginCard.setAlpha(0f);
        loginCard.setTranslationY(50f);
        loginCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateUsername();
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validatePassword();
        });
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void attemptLogin() {
        if (!validateInputs()) return;

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        showLoading(true);

        authController.login(username, password, new AuthController.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                showLoading(false);
                handleSuccessfulLogin(user);
            }

            @Override
            public void onFailure(String message) {
                showLoading(false);
                handleLoginFailure(message);
            }
        });
    }

    private boolean validateInputs() {
        return validateUsername() & validatePassword();
    }

    private boolean validateUsername() {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            tilUsername.setError(getString(R.string.error_username_required));
            return false;
        } else {
            tilUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = etPassword.getText().toString();
        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_password_required));
            return false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_length));
            return false;
        } else {
            tilPassword.setError(null);
            return true;
        }
    }

    private void showLoading(boolean show) {
        if (show) {
            btnLogin.setVisibility(View.INVISIBLE);
            loadingAnimation.setVisibility(View.VISIBLE);
            loadingAnimation.playAnimation();
        } else {
            loadingAnimation.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            loadingAnimation.cancelAnimation();
        }
    }

    private void handleSuccessfulLogin(User user) {
        successAnimation.setVisibility(View.VISIBLE);
        successAnimation.playAnimation();

        successAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                successAnimation.setVisibility(View.GONE);

                Intent intent;
                if ("Patient".equals(user.getRole())) {
                    intent = new Intent(LoginActivity.this, PatientDashboardActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, GuardianDashboardActivity.class);
                }

                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }

    private void handleLoginFailure(String message) {
        btnLogin.animate()
                .translationX(10f)
                .setDuration(50)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        btnLogin.animate()
                                .translationX(-10f)
                                .setDuration(50)
                                .withEndAction(() -> btnLogin.animate().translationX(0f).setDuration(50).start())
                                .start();
                    }
                })
                .start();

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
