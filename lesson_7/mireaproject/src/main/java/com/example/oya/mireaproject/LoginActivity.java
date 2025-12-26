package com.example.oya.mireaproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar progressBar;
    private TextView statusTextView;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        initViews();

        checkCurrentUser();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        statusTextView = findViewById(R.id.statusTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        showProgress(true);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgress(false);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            // Ошибка входа
                            Toast.makeText(LoginActivity.this,
                                    "Ошибка: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        showProgress(true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgress(false);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Ошибка: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Введите email");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Введите пароль");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Пароль должен быть не менее 6 символов");
            return false;
        }

        return true;
    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            registerButton.setEnabled(true);
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}