package com.example.prosportswear.activity;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prosportswear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

     EditText emailT, passT;
    Button loginBtn;
    TextView register;
    ProgressBar progress;
    FirebaseAuth mauth;

    private static final String TAG = "LoginActivity"; // Define TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mauth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailT = findViewById(R.id.email);
        passT = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        register = findViewById(R.id.register);
        progress = findViewById(R.id.progress);

        // Navigate to Register Activity
        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // Login Button Click Event
        loginBtn.setOnClickListener(view -> {
            String email = emailT.getText().toString().trim();
            String pass = passT.getText().toString().trim(); // Fixed incorrect variable

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            progress.setVisibility(View.VISIBLE); // Show progress bar

            // Firebase Login
            mauth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progress.setVisibility(View.GONE); // Hide progress bar

                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mauth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
