package ru.mirea.kichibekov.mireaproject_lesson3;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.mirea.kichibekov.mireaproject_lesson3.databinding.ActivityRegistrationBinding;
import ru.mirea.kichibekov.mireaproject_lesson3.ui.map.MapFragment;

public class Registration extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityRegistrationBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
// [START initialize_auth] Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        @SuppressLint("HardwareIds")
        String m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //Доп задания
        searchApps();
        System.out.println("Индивидуальный код " + m_androidId);
        binding.textID.setText("ID вашего устройства=" + m_androidId);
        //Конец доп заданий
        binding.signedInButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(binding.emailPasswordFields.getText());
                String password = String.valueOf(binding.passEdit.getText());
                signIn(email, password, v);
            }
        });
        binding.emailPasswordButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(binding.emailPasswordFields.getText());
                String password = String.valueOf(binding.passEdit.getText());
                createAccount(email, password, v);
            }
        });
// [END initialize_auth]
    }

    @SuppressLint("SetTextI18n")
    private void searchApps(){
        PackageManager pm = getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();
        View externalLayout = LayoutInflater.from(this).inflate(R.layout.banner_layout, null, false);
        TextView textBanner = externalLayout.findViewById(R.id.bannerTextView);
        for(ApplicationInfo app : apps) {
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                installedApps.add(app);
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
                installedApps.add(app);
            }
        }

        // Создаем список для хранения имен установленных приложений
        List<String> appNames = new ArrayList<>();

        // Перебираем приложения и получаем их имена
        for (ApplicationInfo appInfo : installedApps) {
            String appName = appInfo.loadLabel(pm).toString();
            appNames.add(appName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                appNames);
        for (String app: appNames) {
            if (app.equals("AnyDesk")){
                textBanner.setText("У вас установлено запрещённое приложение AnyDesk, войти не получится");
                showBlockingBanner();
            }
        }
        binding.textApp.setAdapter(adapter);
    }
    private void showBlockingBanner() {
        // Получаем корневой Layout вашей активности
        RelativeLayout rootLayout = findViewById(R.id.rootLayout);

        // Создаем баннер из разметки
        LayoutInflater inflater = LayoutInflater.from(this);
        View bannerView = inflater.inflate(R.layout.banner_layout, rootLayout, false);
        // Добавляем баннер в корневой Layout
        rootLayout.addView(bannerView);

        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.banner_layout);
        dialog.setCancelable(false); // Запретить закрытие диалога при нажатии вне его области
        dialog.show();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
// Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // [END on_start_check_user]
    private void updateUI(FirebaseUser user) {
        if (user != null) {
//            binding.textView.setText(getString(R.string.emailpassword_status_fmt,
//                    user.getEmail(), user.isEmailVerified()));
//            binding.textViewUID.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//            binding.emailPasswordButtons.setVisibility(View.GONE);
//            binding.passEdit.setVisibility(View.GONE);
//            binding.signedInButtons.setVisibility(View.GONE);

        } else {
            binding.textView.setText(R.string.signed_out);
            binding.textViewUID.setText(null);
            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.passEdit.setVisibility(View.VISIBLE);
            binding.signedInButtons.setVisibility(View.VISIBLE);
        }
    }

    private void createAccount(String email, String password, View v) {
        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }
// [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            goSystem(v);
                        } else {
// If sign in fails, display a message to the user.

                            Log.w(TAG, "createUserWithEmail:failure",

                                    task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
// [END create_user_with_email]
    }


    private void signIn(String email, String password, View v) {
        Log.d(TAG, "signIn:" + email);
// [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            goSystem(v);
                        } else {
// If sign in fails, display a message to the user.

                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
// [START_EXCLUDE]

                        if (!task.isSuccessful()) {

                            binding.textView.setText(R.string.auth_failed);
                        }

// [END_EXCLUDE]

                    }
                });
// [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    public void goSystem(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}