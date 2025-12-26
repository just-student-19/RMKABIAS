package com.example.oya.timeservice;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView textViewResult;
    private Button buttonGetTime;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String host = "time.nist.gov";
    private final int port = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textView);
        buttonGetTime = findViewById(R.id.button);

        buttonGetTime.setOnClickListener(v -> getTimeFromServer());
    }

    private void getTimeFromServer() {
        buttonGetTime.setEnabled(false);
        textViewResult.setText("Загружаем время...");

        executor.execute(() -> {
            try {
                Socket socket = new Socket(host, port);
                BufferedReader reader = SocketUtils.getReader(socket);

                reader.readLine();

                String timeString = reader.readLine();
                socket.close();

                String parsedTime = parseTimeString(timeString);

                runOnUiThread(() -> {
                    textViewResult.setText(parsedTime);
                    buttonGetTime.setEnabled(true);
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    textViewResult.setText("Ошибка подключения");
                    buttonGetTime.setEnabled(true);
                    Toast.makeText(this, "Ошибка: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private String parseTimeString(String timeString) {
        try {
            String[] parts = timeString.split(" ");
            if (parts.length >= 3) {
                String dateStr = parts[1]; // 23-12-15
                String timeStr = parts[2]; // 14:45:12

                LocalDateTime dateTime = LocalDateTime.parse(
                        dateStr + "T" + timeStr,
                        DateTimeFormatter.ofPattern("yy-MM-dd'T'HH:mm:ss")
                );

                DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern("dd.MM.yyyy HH:mm:ss");

                return "Текущее время:\n" + dateTime.format(formatter);
            }
            return "Некорректный формат данных";
        } catch (DateTimeParseException e) {
            return "Ошибка парсинга времени\nИсходная строка:\n" + timeString;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}