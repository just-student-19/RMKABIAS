package com.example.oya.myplaces;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.oya.myplaces.MapFragment;
import com.example.oya.myplaces.PlacesListFragment;
import com.example.oya.myplaces.Place;

public class MainActivity extends AppCompatActivity
        implements PlacesListFragment.OnPlaceSelectedListener {

    private MapFragment mapFragment;
    private PlacesListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Создаем фрагменты
        mapFragment = new MapFragment();
        listFragment = new PlacesListFragment();
        listFragment.setOnPlaceSelectedListener(this);

        // Показываем список заведений по умолчанию
        showFragment(listFragment, false);
    }

    @Override
    public void onPlaceSelected(Place place) {
        // Показываем карту с выбранным местом
        showFragment(mapFragment, true);
        mapFragment.showPlaceOnMap(place);
    }

    private void showFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}