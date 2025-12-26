package com.example.oya.mireaproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button logoutButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        initViews();
        showUserInfo();
        setupFragment();
    }

    private void initViews() {
        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void showUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            welcomeTextView.setText("Добро пожаловать, " + email + "!");
        } else {
            goToLoginActivity();
        }
    }

    private void setupFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, new PostsFragment());
        transaction.commit();
    }

    private void logoutUser() {
        auth.signOut();
        Toast.makeText(this, "Вы вышли из системы", Toast.LENGTH_SHORT).show();
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}