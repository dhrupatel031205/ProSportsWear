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
        progress = findViewById(R.id.progress); // Make sure you have a ProgressBar in XML

        // Navigate to Login Activity
        login.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Register Button Click Event
        registerBtn.setOnClickListener(view -> {
            progress.setVisibility(View.VISIBLE); // Show progress bar

            String email = emailT.getText().toString().trim();
            String pass = passT.getText().toString().trim();

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
                        progress.setVisibility(View.GONE); // Hide progress bar

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    });
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
