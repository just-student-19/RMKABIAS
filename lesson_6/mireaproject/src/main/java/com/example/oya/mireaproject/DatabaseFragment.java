package com.example.oya.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class DatabaseFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_database, container, false);

        TextView textView = root.findViewById(R.id.text_database);
        textView.setText("Раздел 'База данных'\n\n" +
                "Здесь можно интегрировать Room Database, но пока ничего нет.... :(");

        return root;
    }
}