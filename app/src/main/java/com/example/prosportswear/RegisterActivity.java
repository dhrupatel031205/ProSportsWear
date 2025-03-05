package com.example.prosportswear;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameT, emailT, passT;
    private Button registerBtn;
    private TextView login;
    private FirebaseAuth auth;
    private ProgressBar progress;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI Elements
        nameT = findViewById(R.id.editTextName);
        emailT = findViewById(R.id.editTextEmail);
        passT = findViewById(R.id.editTextPassword);
        registerBtn = findViewById(R.id.buttonRegister);
        login = findViewById(R.id.textViewLogin);
        progress = findViewById(R.id.progress);

        // Navigate to Login Activity
        login.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Register Button Click Event
        registerBtn.setOnClickListener(view -> {
            progress.setVisibility(View.VISIBLE);

            String name = nameT.getText().toString().trim();
            String email = emailT.getText().toString().trim();
            String pass = passT.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(RegisterActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                return;
            }

            // Firebase Registration
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        progress.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Save user name in Firebase Authentication
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)  // Set the user's name
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
