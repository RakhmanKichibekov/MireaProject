package ru.mirea.kichibekov.mireaproject_lesson3.ui.map;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import ru.mirea.kichibekov.mireaproject_lesson3.R;
import ru.mirea.kichibekov.mireaproject_lesson3.databinding.FragmentMapBinding;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private Context mContext;

    private MapView mapView;

    private TextView textView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        mContext = inflater.getContext();
        textView = binding.text1;
        mapView = binding.mapView;
        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(55.794229, 37.700772);
        mapController.setCenter(startPoint);

        MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(new
                GpsMyLocationProvider(mContext), mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(locationNewOverlay);
//Compas
        CompassOverlay compassOverlay = new CompassOverlay(mContext, new
                InternalCompassOrientationProvider(mContext), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        final DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);

        drawIcon("МИРЭА - Российский техногический университет, кузница миллиардеров",
                R.drawable.image_rtu, R.drawable.rtu, 55.794229, 37.700772);
        //marker.setTitle("Title");
        drawIcon("Авиапарк - торгово-развлекательный центр. Время работы: 10:00 - 22:00",
                R.drawable.image_aviapark, R.drawable.aviapark, 55.7904430, 37.5334820);
        drawIcon("Большой театр - Государственный академический Большой театр России",
                R.drawable.image_theatre, R.drawable.theatre, 55.760221, 37.618561);
        drawIcon("Avenue - торгово-развлекательный центр. Время работы: 10:00 - 22:00",
                R.drawable.image_avenue, R.drawable.aviapark, 55.663024, 37.481001);
        return binding.getRoot();
    }

    public void drawIcon(String text, Integer drawable, Integer icon, Double aLatitude, Double aLongitude) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(aLatitude, aLongitude));
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                textView.setText(text);
                binding.imageView.setImageResource(drawable);
                return true;
            }
        });
        mapView.getOverlays().add(marker);

        marker.setIcon(ResourcesCompat.getDrawable(getResources(),
                icon, null));
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(mContext,
                PreferenceManager.getDefaultSharedPreferences(mContext));
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(mContext,

                PreferenceManager.getDefaultSharedPreferences(mContext));

        if (mapView != null) {
            mapView.onPause();
        }
    }
}