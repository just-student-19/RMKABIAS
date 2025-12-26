package com.example.oya.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;

public class PostsFragment extends Fragment {

    private TextView resultTextView;
    private Button loadButton;
    private Button clearButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        resultTextView = view.findViewById(R.id.resultTextView);
        loadButton = view.findViewById(R.id.loadButton);
        clearButton = view.findViewById(R.id.clearButton);

        setupButtonListeners();

        return view;
    }

    private void setupButtonListeners() {
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromNetwork();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextView.setText("");
            }
        });
    }

    private void loadDataFromNetwork() {
        resultTextView.setText("Загружаем данные...");
        loadButton.setEnabled(false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceholderApi api = retrofit.create(JsonPlaceholderApi.class);

        api.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                loadButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    displayPosts(response.body());
                    Toast.makeText(getActivity(),
                            "Загружено " + response.body().size() + " постов",
                            Toast.LENGTH_SHORT).show();
                } else {
                    resultTextView.setText("Ошибка загрузки: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                loadButton.setEnabled(true);
                resultTextView.setText("Ошибка: " + t.getMessage());
                Toast.makeText(getActivity(),
                        "Ошибка соединения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPosts(List<Post> posts) {
        StringBuilder result = new StringBuilder();

        int limit = Math.min(5, posts.size());

        for (int i = 0; i < limit; i++) {
            Post post = posts.get(i);
            result.append("Пост #").append(post.getId())
                    .append("\nЗаголовок: ").append(post.getTitle())
                    .append("\nТекст: ").append(post.getBody())
                    .append("\n\n");
        }

        resultTextView.setText(result.toString());
    }
}