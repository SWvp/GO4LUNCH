package com.kardabel.go4lunch;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.kardabel.go4lunch.databinding.AuthenticationBinding;
import com.kardabel.go4lunch.usecase.CreateUserUseCase;
import com.kardabel.go4lunch.usecase.GetCurrentUserUseCase;

import java.util.Arrays;
import java.util.List;

public class AuthenticationActivity extends BaseActivity<AuthenticationBinding> {

    private static final int RC_SIGN_IN = 123;

    @Override
    public AuthenticationBinding getViewBinding() {
        return AuthenticationBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton();
    }

    // Update Login Button when activity is resuming
    private void updateLoginButton() {
        binding.loginButton.setText(GetCurrentUserUseCase.getCurrentUser() != null ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }

    private void setupListeners() {
        // Login/Profile Button
        binding.loginButton.setOnClickListener(view -> {
            if (GetCurrentUserUseCase.getCurrentUser() != null) {
                startProfileActivity();
            } else {
                startSignInActivity();
            }
        });
    }

    // Launching Profile Activity
    private void startProfileActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // auth methods
    private void startSignInActivity() {
        //Choose auth providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());


        // Launch activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        //.setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    // Show Snack Bar with a message
    private void showSnackBar(String message) {
        Snackbar.make(binding.authenticationLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                CreateUserUseCase.createUser();
                showSnackBar(getString(R.string.connection_succeed));
            } else {
                // ERRORS
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError() != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }
}
