package com.example.oya.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText editTextName, editTextAge, editTextGroup, editTextMovie;
    private Spinner spinnerLanguage;
    private CheckBox checkBoxNotifications;
    private Button buttonSaveProfile;
    private TextView textViewProfileInfo;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "user_profile";

    private static final String[] PROGRAMMING_LANGUAGES = {
            "Java", "Kotlin", "Python", "C++", "JavaScript", "C#", "Swift"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextName = root.findViewById(R.id.editTextName);
        editTextAge = root.findViewById(R.id.editTextAge);
        editTextGroup = root.findViewById(R.id.editTextGroup);
        editTextMovie = root.findViewById(R.id.editTextMovie);
        spinnerLanguage = root.findViewById(R.id.spinnerLanguage);
        checkBoxNotifications = root.findViewById(R.id.checkBoxNotifications);
        buttonSaveProfile = root.findViewById(R.id.buttonSaveProfile);
        textViewProfileInfo = root.findViewById(R.id.textViewProfileInfo);

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        setupSpinner();

        loadProfileData();

        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
                displayProfileInfo();
            }
        });

        displayProfileInfo();

        return root;
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                PROGRAMMING_LANGUAGES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);
    }

    private void saveProfileData() {
        String name = editTextName.getText().toString();
        String ageStr = editTextAge.getText().toString();
        String group = editTextGroup.getText().toString();
        String movie = editTextMovie.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name", name);

        try {
            int age = Integer.parseInt(ageStr);
            editor.putInt("age", age);
        } catch (NumberFormatException e) {
            editor.putInt("age", 0);
        }

        editor.putString("group", group);
        editor.putString("movie", movie);
        editor.putString("language", spinnerLanguage.getSelectedItem().toString());
        editor.putBoolean("notifications", checkBoxNotifications.isChecked());

        editor.apply();

        Toast.makeText(requireContext(), "Профиль сохранен", Toast.LENGTH_SHORT).show();
    }

    private void loadProfileData() {
        editTextName.setText(sharedPreferences.getString("name", ""));

        int age = sharedPreferences.getInt("age", 0);
        editTextAge.setText(age > 0 ? String.valueOf(age) : "");

        editTextGroup.setText(sharedPreferences.getString("group", ""));
        editTextMovie.setText(sharedPreferences.getString("movie", ""));

        String savedLanguage = sharedPreferences.getString("language", "Java");
        ArrayAdapter adapter = (ArrayAdapter) spinnerLanguage.getAdapter();

        if (adapter != null) {
            int position = adapter.getPosition(savedLanguage);
            if (position >= 0) {
                spinnerLanguage.setSelection(position);
            } else {
                // Если сохраненного языка нет в списке, выбираем первый
                spinnerLanguage.setSelection(0);
            }
        }

        checkBoxNotifications.setChecked(sharedPreferences.getBoolean("notifications", true));
    }

    private void displayProfileInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Сохраненный профиль:\n\n");
        info.append("Имя: ").append(sharedPreferences.getString("name", "Не указано")).append("\n");
        info.append("Возраст: ").append(sharedPreferences.getInt("age", 0)).append("\n");
        info.append("Группа: ").append(sharedPreferences.getString("group", "Не указано")).append("\n");
        info.append("Любимый фильм: ").append(sharedPreferences.getString("movie", "Не указано")).append("\n");
        info.append("Язык программирования: ").append(sharedPreferences.getString("language", "Не выбран")).append("\n");
        info.append("Уведомления: ").append(sharedPreferences.getBoolean("notifications", false) ? "Включены" : "Выключены");

        textViewProfileInfo.setText(info.toString());
    }
}