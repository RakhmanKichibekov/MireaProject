package ru.mirea.kichibekov.mireaproject_lesson3.ui.compas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import ru.mirea.kichibekov.mireaproject_lesson3.databinding.FragmentCompasBinding;

public class CompasFragment extends Fragment implements SensorEventListener {

    private FragmentCompasBinding binding;

    private ImageView ivDinamic;

    private TextView tvDegree;

    private Float currentDegree = 0f;

    private SensorManager sensorManager;

    private Context mContext;

    private boolean isWork = false;

    private static final int REQUEST_CODE_PERMISSION = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompasBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        mContext = inflater.getContext();
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        init();
        return binding.getRoot();
    }

    private void init() {
        int storagePermissionStatus = ContextCompat.checkSelfPermission(mContext, Manifest.permission.LOCATION_HARDWARE);
        if (storagePermissionStatus
                == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            // Выполняется запрос к пользователь на получение необходимых разрешений
            ActivityCompat.requestPermissions(requireActivity(), new String[] {android.Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
        ivDinamic = binding.ivDynamic;
        tvDegree = binding.tvDegree;
        System.out.println("Инициализировано");
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_UI);
    //sensorManager.getOrientation()
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = event.values[0]*100;

        tvDegree.setText(String.format("Degree from North: %s degrees",Float.toString(degree)));
        //Log.d(CompasFragment.class.getSimpleName(), String.format("Degree from North: %s degrees",Float.toString(degree)));
        RotateAnimation ra = new RotateAnimation(
                currentDegree, -degree, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(210);
        ra.setFillAfter(true);

        ivDinamic.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}