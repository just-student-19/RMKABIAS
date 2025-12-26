package com.example.oya.notebook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextFileName, editTextQuote;
    private Button buttonSaveFile, buttonLoadFile, buttonCreateExamples;
    private TextView textViewFileInfo;

    private Uri currentFileUri = null;

    private final ActivityResultLauncher<Intent> createFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        currentFileUri = uri;
                        saveToFile(uri);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> openFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        currentFileUri = uri;
                        loadFromFile(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextFileName = findViewById(R.id.editTextFileName);
        editTextQuote = findViewById(R.id.editTextQuote);
        buttonSaveFile = findViewById(R.id.buttonSaveFile);
        buttonLoadFile = findViewById(R.id.buttonLoadFile);

        buttonSaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrSelectFileForWriting();
            }
        });

        buttonLoadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFileForReading();
            }
        });

        editTextFileName.setText("Моя_цитата");
        editTextQuote.setText("Пример цитаты для тестирования приложения.");
    }

    private void createOrSelectFileForWriting() {
        String fileName = editTextFileName.getText().toString();
        if (fileName.isEmpty()) {
            fileName = "quote_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        }

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName + ".txt");

        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,
                DocumentsContract.buildDocumentUri(
                        "com.android.externalstorage.documents",
                        "primary:Documents"));

        createFileLauncher.launch(intent);
    }

    private void selectFileForReading() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        String[] mimeTypes = {"text/plain", "application/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        openFileLauncher.launch(intent);
    }

    private void saveToFile(Uri uri) {
        String quote = editTextQuote.getText().toString();

        if (quote.isEmpty()) {
            Toast.makeText(this, "Введите цитату для сохранения", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date());
        String content = "Цитата от " + timestamp + ":\n\n" + quote + "\n\n--- Конец цитаты ---";

        try (OutputStream outputStream = getContentResolver().openOutputStream(uri);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {

            writer.write(content);
            writer.flush();

            textViewFileInfo.setText("Файл сохранён:\n" + uri.getPath() +
                    "\nРазмер: " + content.length() + " символов");
            Toast.makeText(this, "Цитата сохранена", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка записи: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFromFile(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            editTextQuote.setText(content.toString());

            String fileName = getFileName(uri);
            editTextFileName.setText(fileName.replace(".txt", ""));

            textViewFileInfo.setText("Файл загружен:\n" + uri.getPath() +
                    "\nРазмер: " + content.length() + " символов");
            Toast.makeText(this, "Цитата загружена", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка чтения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private String getFileName(Uri uri) {
        String displayName = null;
        try (android.database.Cursor cursor = getContentResolver().query(
                uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(
                        android.provider.OpenableColumns.DISPLAY_NAME));
            }
        }
        return displayName != null ? displayName : "неизвестный_файл";
    }

}