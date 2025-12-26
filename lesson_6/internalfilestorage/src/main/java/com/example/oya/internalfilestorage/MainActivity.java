package com.example.oya.internalfilestorage;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextDate, editTextDescription;
    private Button buttonWrite;
    private TextView textViewStatus;
    private static final String FILENAME = "history_note.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDate = findViewById(R.id.editTextDate);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonWrite = findViewById(R.id.buttonWrite);
        textViewStatus = findViewById(R.id.textViewStatus);

        editTextDate.setText("12 апреля 1961");
        editTextDescription.setText("Первый полёт человека в космос. Юрий Гагарин на корабле 'Восток-1' совершил орбитальный облёт Земли.");

        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile();
            }
        });
    }

    private void writeToFile() {
        String date = editTextDate.getText().toString();
        String description = editTextDescription.getText().toString();

        if (date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date());

        String content = "=== Историческая дата ===\n" +
                "Дата: " + date + "\n" +
                "Описание: " + description + "\n" +
                "Записано: " + timestamp ;

        try (FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE)) {
            fos.write(content.getBytes());

            String filePath = getFilesDir() + "/" + FILENAME;
            textViewStatus.setText("Статус: записано в " + filePath);
            Toast.makeText(this, "Файл сохранён", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            textViewStatus.setText("Статус: ошибка записи");
            Toast.makeText(this, "Ошибка записи файла", Toast.LENGTH_SHORT).show();
        }
    }
}