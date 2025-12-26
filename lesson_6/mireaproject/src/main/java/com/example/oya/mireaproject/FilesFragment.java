package com.example.oya.mireaproject;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FilesFragment extends Fragment {

    private EditText editTextInput;
    private TextView textViewResult;
    private Button buttonEncrypt, buttonDecrypt;
    private FloatingActionButton fabAddNote;

    private static final String ENCRYPTION_KEY = "MIR2024";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_files, container, false);

        editTextInput = root.findViewById(R.id.editTextInput);
        textViewResult = root.findViewById(R.id.textViewResult);
        buttonEncrypt = root.findViewById(R.id.buttonEncrypt);
        buttonDecrypt = root.findViewById(R.id.buttonDecrypt);
        fabAddNote = root.findViewById(R.id.fabAddNote);

        editTextInput.setText("Привет от студента МИРЭА!");

        buttonEncrypt.setOnClickListener(v -> encryptText());
        buttonDecrypt.setOnClickListener(v -> decryptText());
        fabAddNote.setOnClickListener(v -> showAddNoteDialog());

        return root;
    }

    private void encryptText() {
        String input = editTextInput.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(requireContext(), "Введите текст", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            char keyChar = ENCRYPTION_KEY.charAt(i % ENCRYPTION_KEY.length());
            encrypted.append((char) (c ^ keyChar));
        }

        textViewResult.setText("Зашифровано:\n" + encrypted);
        saveToFile("encrypted.txt", encrypted.toString());
    }

    private void decryptText() {
        String input = editTextInput.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(requireContext(), "Введите текст", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            char keyChar = ENCRYPTION_KEY.charAt(i % ENCRYPTION_KEY.length());
            decrypted.append((char) (c ^ keyChar));
        }

        textViewResult.setText("Расшифровано:\n" + decrypted);
        saveToFile("decrypted.txt", decrypted.toString());
    }

    private void saveToFile(String fileName, String content) {
        try {
            FileOutputStream fos = requireContext().openFileOutput(fileName,
                    requireContext().MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(requireContext(), "Сохранено: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Новая запись");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_note, null);
        EditText editTitle = dialogView.findViewById(R.id.editTextNoteTitle);
        EditText editContent = dialogView.findViewById(R.id.editTextNoteContent);

        builder.setView(dialogView);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String title = editTitle.getText().toString();
            String content = editContent.getText().toString();

            if (!title.isEmpty() && !content.isEmpty()) {
                String note = "Заголовок: " + title + "\n" +
                        "Дата: " + new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date()) + "\n\n" +
                        content;

                saveToFile("note_" + System.currentTimeMillis() + ".txt", note);
                textViewResult.setText("Запись сохранена:\n\n" + note);
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}