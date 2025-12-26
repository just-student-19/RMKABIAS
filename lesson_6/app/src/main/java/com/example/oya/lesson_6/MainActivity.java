package com.example.oya.lesson_6;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText editTextGroup, editTextNumber, editTextMovie;
    private Button buttonSave;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextGroup = findViewById(R.id.editTextGroup);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextMovie = findViewById(R.id.editTextMovie);
        buttonSave = findViewById(R.id.buttonSave);

        sharedPref = getSharedPreferences("mirea_settings", Context.MODE_PRIVATE);

        loadData();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("GROUP", editTextGroup.getText().toString());
        editor.putInt("NUMBER", Integer.parseInt(editTextNumber.getText().toString()));
        editor.putString("MOVIE", editTextMovie.getText().toString());
        editor.apply();
    }

    private void loadData() {
        String group = sharedPref.getString("GROUP", "");
        int number = sharedPref.getInt("NUMBER", 0);
        String movie = sharedPref.getString("MOVIE", "");

        editTextGroup.setText(group);
        editTextNumber.setText(String.valueOf(number));
        editTextMovie.setText(movie);
    }
}