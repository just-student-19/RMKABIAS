package com.example.oya.myplaces;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import com.example.oya.myplaces.R;
import com.example.oya.myplaces.Place;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Конфигурация OSMDroid
        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );

        mapView = view.findViewById(R.id.map_view);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Центр на Москве
        mapView.getController().setCenter(new GeoPoint(55.751574, 37.573856));
        mapView.getController().setZoom(12.0);

        // Проверка разрешений
        checkLocationPermission();

        return view;
    }

    public void showPlaceOnMap(Place place) {
        if (mapView == null) return;

        mapView.getOverlays().clear();

        Marker marker = new Marker(mapView);
        GeoPoint point = new GeoPoint(place.getLatitude(), place.getLongitude());
        marker.setPosition(point);
        marker.setTitle(place.getName());
        marker.setSnippet(place.getAddress());
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            Toast.makeText(getContext(),
                    place.getName() + "\n" + place.getAddress(),
                    Toast.LENGTH_LONG).show();
            return true;
        });

        mapView.getOverlays().add(marker);

        mapView.getController().animateTo(point);

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationOverlay == null) {
                setupLocationOverlay();
            }
            mapView.getOverlays().add(locationOverlay);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            setupLocationOverlay();
        }
    }

    private void setupLocationOverlay() {
        locationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(requireContext()),
                mapView
        );
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupLocationOverlay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }
}