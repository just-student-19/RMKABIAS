package com.example.oya.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuthApp";

    // Firebase
    private FirebaseAuth mAuth;

    // UI elements
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mEmailSignInButton;
    private Button mEmailCreateAccountButton;
    private Button mSignOutButton;
    private Button mVerifyEmailButton;
    private View mEmailPasswordFields;
    private View mEmailPasswordButtons;
    private View mSignedInButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        mStatusTextView = findViewById(R.id.statusTextView);
        mDetailTextView = findViewById(R.id.detailTextView);
        mEmailField = findViewById(R.id.emailEditText);
        mPasswordField = findViewById(R.id.passwordEditText);
        mEmailSignInButton = findViewById(R.id.emailSignInButton);
        mEmailCreateAccountButton = findViewById(R.id.emailCreateAccountButton);
        mSignOutButton = findViewById(R.id.signOutButton);
        mVerifyEmailButton = findViewById(R.id.verifyEmailButton);
        mEmailPasswordFields = findViewById(R.id.emailPasswordFields);
        mEmailPasswordButtons = findViewById(R.id.emailPasswordButtons);
        mSignedInButtons = findViewById(R.id.signedInButtons);

        // Set click listeners
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmailField.getText().toString(),
                        mPasswordField.getText().toString());
            }
        });

        mEmailCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(mEmailField.getText().toString(),
                        mPasswordField.getText().toString());
            }
        });

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        mVerifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in
            mStatusTextView.setText(getString(R.string.signed_in));
            mDetailTextView.setText("Email: " + user.getEmail() +
                    "\nUID: " + user.getUid() +
                    "\nVerified: " + user.isEmailVerified());

            // Show signed-in UI
            mEmailPasswordFields.setVisibility(View.GONE);
            mEmailPasswordButtons.setVisibility(View.GONE);
            mSignedInButtons.setVisibility(View.VISIBLE);

            // Enable verify email button if email is not verified
            mVerifyEmailButton.setEnabled(!user.isEmailVerified());

        } else {
            // User is signed out
            mStatusTextView.setText(getString(R.string.signed_out));
            mDetailTextView.setText(null);

            // Show signed-out UI
            mEmailPasswordFields.setVisibility(View.VISIBLE);
            mEmailPasswordButtons.setVisibility(View.VISIBLE);
            mSignedInButtons.setVisibility(View.GONE);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(MainActivity.this,
                                    "Account created successfully!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            String errorMessage = "Authentication failed.";
                            if (task.getException() instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        errorMessage = "Invalid email format.";
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        errorMessage = "Password is too weak.";
                                        break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        errorMessage = "Email already in use.";
                                        break;
                                }
                            }

                            Toast.makeText(MainActivity.this, errorMessage,
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(MainActivity.this,
                                    "Signed in successfully!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            String errorMessage = "Sign in failed.";
                            if (task.getException() instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        errorMessage = "Invalid email format.";
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        errorMessage = "Wrong password.";
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        errorMessage = "User not found.";
                                        break;
                                    case "ERROR_USER_DISABLED":
                                        errorMessage = "User account is disabled.";
                                        break;
                                }
                            }

                            Toast.makeText(MainActivity.this, errorMessage,
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
        Toast.makeText(this, "Signed out.", Toast.LENGTH_SHORT).show();
    }

    private void sendEmailVerification() {
        // Disable button
        mVerifyEmailButton.setEnabled(false);

        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            mVerifyEmailButton.setEnabled(true);
            return;
        }

        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable button
                        mVerifyEmailButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}