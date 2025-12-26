package com.example.oya.httpurlconnection;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView textViewResult;
    private Button buttonGetData;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String IP_INFO_URL = "https://ipinfo.io/json";

    // Сервис для погоды (заполним координатами позже)
    private String WEATHER_URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textView);
        buttonGetData = findViewById(R.id.button);

        buttonGetData.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                getIpAndWeatherInfo();
            } else {
                Toast.makeText(this, "Нет подключения к интернету",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void getIpAndWeatherInfo() {
        buttonGetData.setEnabled(false);
        textViewResult.setText("Загружаем данные...");

        executor.execute(() -> {
            try {
                String ipInfoJson = downloadUrl(IP_INFO_URL);
                JSONObject ipInfo = new JSONObject(ipInfoJson);

                String ip = ipInfo.optString("ip", "Неизвестно");
                String city = ipInfo.optString("city", "Неизвестно");
                String region = ipInfo.optString("region", "Неизвестно");
                String country = ipInfo.optString("country", "Неизвестно");
                String loc = ipInfo.optString("loc", ""); // координаты

                String weatherInfo = "";
                if (!loc.isEmpty()) {
                    String[] coords = loc.split(",");
                    if (coords.length == 2) {
                        String lat = coords[0];
                        String lon = coords[1];
                        WEATHER_URL = String.format(
                                "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true",
                                lat, lon
                        );

                        String weatherJson = downloadUrl(WEATHER_URL);
                        JSONObject weatherData = new JSONObject(weatherJson)
                                .getJSONObject("current_weather");

                        double temperature = weatherData.getDouble("temperature");
                        double windspeed = weatherData.getDouble("windspeed");
                        String weathercode = weatherData.getString("weathercode");

                        weatherInfo = String.format(
                                "\n\n🌤 Погода:\nТемпература: %.1f°C\nСкорость ветра: %.1f км/ч\nКод погоды: %s",
                                temperature, windspeed, weathercode
                        );
                    }
                }

                final String result = String.format(
                        "IP информация:\nIP: %s\nГород: %s\nРегион: %s\nСтрана: %s%s",
                        ip, city, region, country, weatherInfo
                );

                runOnUiThread(() -> {
                    textViewResult.setText(result);
                    buttonGetData.setEnabled(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    textViewResult.setText("Ошибка загрузки данных");
                    buttonGetData.setEnabled(true);
                    Toast.makeText(this, "Ошибка: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String downloadUrl(String urlString) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream)
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } else {
                throw new IOException("HTTP ошибка: " + responseCode);
            }
        } finally {
            if (inputStream != null) inputStream.close();
            if (connection != null) connection.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}