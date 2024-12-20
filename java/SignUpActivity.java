package com.example.eventsclientapp1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventsclientapp1.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using View Binding
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Seting onClickListener for Sign Up button
        binding.buttonSignUp.setOnClickListener(view -> createAccount());

        // Seting onClickListener for Sign In TextView
        binding.textViewSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void createAccount() {
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        if (!validateForm(email, password)) {
            return;
        }

        // Creating a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success
                        Toast.makeText(SignUpActivity.this, "Sign up successful.", Toast.LENGTH_SHORT).show();
                        // Redirect to MainActivity
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Sign up fails
                        Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.setError("Required.");
            valid = false;
        } else {
            binding.editTextEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.setError("Required.");
            valid = false;
        } else {
            binding.editTextPassword.setError(null);
        }

        return valid;
    }
}
