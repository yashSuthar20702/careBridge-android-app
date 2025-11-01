package com.example.carebridge.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
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

/** Login activity handling user authentication with animated UI and input validation */
public class LoginActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private TextInputEditText etUsername, etPassword;
    private TextInputLayout tilUsername, tilPassword;
    private MaterialButton btnLogin;
    private LottieAnimationView successAnimation;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupAnimations();
        setupClickListeners();
        setupBackPressedHandler();
        setupKeyboardScroll();

        authController = new AuthController(this);

        // Redirect to dashboard if user is already authenticated
        if (authController.isLoggedIn()) {
            redirectToDashboard(authController.getCurrentUser());
        }
    }

    /** Initialize all UI component references from layout */
    private void initializeViews() {
        scrollView = findViewById(R.id.scrollView);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        btnLogin = findViewById(R.id.btnLogin);
        successAnimation = findViewById(R.id.successAnimation);
    }

    /** Setup entrance animations for login card */
    private void setupAnimations() {
        View loginCard = findViewById(R.id.loginCard);
        loginCard.setAlpha(0f);
        loginCard.setTranslationY(50f);
        loginCard.animate().alpha(1f).translationY(0f).setDuration(800).start();
    }

    /** Configure click listeners for login button and input fields */
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        etUsername.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) scrollToView(etUsername); else validateUsername(); });
        etPassword.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) scrollToView(etPassword); else validatePassword(); });
    }

    /** Handle back button press to exit app */
    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { finish(); }
        });
    }

    /** Auto-scroll when keyboard appears to keep fields visible */
    private void setupKeyboardScroll() {
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keyboardHeight = screenHeight - r.bottom;
            if (keyboardHeight > screenHeight * 0.15) {
                View focused = getCurrentFocus();
                if (focused != null) scrollView.post(() -> scrollToView(focused));
            }
        });
    }

    /** Smooth scroll to make specified view visible */
    private void scrollToView(View view) {
        scrollView.post(() -> scrollView.smoothScrollTo(0, view.getTop() - 100));
    }

    /** Validate credentials and initiate login process */
    private void attemptLogin() {
        if (!validateInputs()) return;

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        btnLogin.setEnabled(false);
        authController.login(username, password, new AuthController.LoginCallback() {
            @Override
            public void onSuccess(User user) { handleSuccessfulLogin(user); }

            @Override
            public void onFailure(String message) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Validate both username and password inputs */
    private boolean validateInputs() { return validateUsername() & validatePassword(); }

    /** Validate username field for empty input */
    private boolean validateUsername() {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) { tilUsername.setError(getString(R.string.error_username_required)); return false; }
        else { tilUsername.setError(null); return true; }
    }

    /** Validate password field for empty input and minimum length */
    private boolean validatePassword() {
        String password = etPassword.getText().toString();
        if (password.isEmpty()) { tilPassword.setError(getString(R.string.error_password_required)); return false; }
        else if (password.length() < 6) { tilPassword.setError(getString(R.string.error_password_length)); return false; }
        else { tilPassword.setError(null); return true; }
    }

    /** Handle successful login by redirecting to appropriate dashboard */
    private void handleSuccessfulLogin(User user) { redirectToDashboard(user); }

    /** Redirect to patient or guardian dashboard based on user role */
    private void redirectToDashboard(User user) {
        Intent intent = getString(R.string.patient_role).equals(user.getRole()) ?
                new Intent(this, PatientDashboardActivity.class) :
                new Intent(this, GuardianDashboardActivity.class);
        intent.putExtra(getString(R.string.intent_user_key), user);
        startActivity(intent);
        finish();
    }
}