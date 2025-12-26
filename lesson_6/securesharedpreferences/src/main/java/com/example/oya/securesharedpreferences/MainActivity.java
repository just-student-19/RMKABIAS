package com.example.oya.securesharedpreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private TextView textViewPoet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPoet = findViewById(R.id.textViewPoet);

        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    masterKeyAlias,
                    getBaseContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            securePrefs.edit().putString("FAVORITE_POET", "Александр Пушкин").apply();

            String poet = securePrefs.getString("FAVORITE_POET", "Неизвестно");
            textViewPoet.setText("Любимый поэт: " + poet);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}