package com.example.oya.myplaces;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oya.myplaces.R;
import com.example.oya.myplaces.PlacesAdapter;
import com.example.oya.myplaces.Place;
import java.util.ArrayList;
import java.util.List;

public class PlacesListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private OnPlaceSelectedListener listener;

    public interface OnPlaceSelectedListener {
        void onPlaceSelected(Place place);
    }

    public void setOnPlaceSelectedListener(OnPlaceSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Place> places = getSamplePlaces();
        adapter = new PlacesAdapter(places, place -> {
            if (listener != null) {
                listener.onPlaceSelected(place);
            }
        });

        recyclerView.setAdapter(adapter);
        return view;
    }

    private List<Place> getSamplePlaces() {
        List<Place> places = new ArrayList<>();
        places.add(new Place(1, "Ресторан 'Белый Лебедь'",
                "Русская кухня, уютная атмосфера",
                "ул. Тверская, 15", "Ресторан",
                55.7610, 37.6175, R.drawable.baseline_restaurant_24));

        places.add(new Place(2, "Кофейня 'Coffee House'",
                "Свежая выпечка, авторский кофе",
                "ул. Арбат, 23", "Кафе",
                55.7497, 37.5904, R.drawable.baseline_local_cafe_24));

        places.add(new Place(3, "Библиотека им. Ленина",
                "Крупнейшая библиотека России",
                "ул. Воздвиженка, 3/5", "Культура",
                55.7507, 37.6096, R.drawable.outline_book_6_24));

        places.add(new Place(4, "Торговый центр 'Европейский'",
                "Более 500 магазинов, кинотеатр",
                "пл. Киевского Вокзала, 2", "Шоппинг",
                55.7440, 37.5667, R.drawable.local_mall_24));

        places.add(new Place(5, "Парк Горького",
                "Центральный парк культуры и отдыха",
                "ул. Крымский Вал, 9", "Парк",
                55.7295, 37.6018, R.drawable.rounded_attractions_24));

        return places;
    }
}