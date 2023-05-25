package ru.mirea.kichibekov.mireaproject_lesson3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            binding.textView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            binding.textViewUID.setText(getString(R.string.firebase_status_fmt, user.getUid()));
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