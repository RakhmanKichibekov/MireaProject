package ru.mirea.kichibekov.mireaproject_lesson3.ui.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ru.mirea.kichibekov.mireaproject_lesson3.R;
import ru.mirea.kichibekov.mireaproject_lesson3.databinding.FragmentMicrophoneBinding;
import ru.mirea.kichibekov.mireaproject_lesson3.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private Context mContext;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mContext = inflater.getContext();
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExternalStorageWritable();
                writeFileToExternalStorage();
                Toast.makeText(mContext, "Сохранено", Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    /* Проверяем внешнее хранилище на доступность чтения */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void writeFileToExternalStorage() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        String fileName = String.valueOf(binding.nameFile.getText());
        File file = new File(path, fileName + ".txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsoluteFile());
            OutputStreamWriter output = new OutputStreamWriter(fileOutputStream);
// Запись строки в файл
            String age = String.valueOf(binding.EtAge.getText());
            String name = String.valueOf(binding.EtName.getText());
            String lastname = String.valueOf(binding.EtLastName.getText());
            String email = String.valueOf(binding.EtEmail.getText());
            output.write("Возраст" + age + "\n" +
                    "Имя" + name + "\n"+
                    "Фамилия" + lastname + "\n" +
                    "Email" + email);
// Закрытие потока записи
            output.close();
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

//    public void readFileFromExternalStorage() {
//        File path = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOCUMENTS);
//        String fileName = String.valueOf(binding.nameFile.getText());
//        File file = new File(path, fileName + ".txt");
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file.getAbsoluteFile());
//
//            InputStreamReader inputStreamReader = new InputStreamReader(
//                    fileInputStream,
//                    StandardCharsets.UTF_8);
//
//            List<String> lines = new ArrayList<String>();
//            BufferedReader reader = new BufferedReader(inputStreamReader);
//            String line = reader.readLine();
//            while (line != null) {
//                lines.add(line);
//                line = reader.readLine();
//            }
//            Log.w("ExternalStorage", String.format("Read from file %s successful", lines.toString()));
//            binding.quoteEdit.setText(lines.toString());
//        } catch (Exception e) {
//            Log.w("ExternalStorage", String.format("Read from file %s failed", e.getMessage()));
//        }
//    }
}