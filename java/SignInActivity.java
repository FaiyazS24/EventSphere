## Developed By: Faiyaz Sattar & Mohit Singh
    
package com.example.eventsclientapp1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventsclientapp1.databinding.ActivitySignInBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using View Binding
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Seting onClickListener for Sign In button
        binding.buttonSignIn.setOnClickListener(view -> signIn());

        // Seting onClickListener for Sign Up TextView
        binding.textViewSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signIn() {
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        if (!validateForm(email, password)) {
            return;
        }

        // Signing in existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Toast.makeText(SignInActivity.this, "Sign in successful.", Toast.LENGTH_SHORT).show();
                        // Redirect to MainActivity
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Sign in fails
                        Toast.makeText(SignInActivity.this, "Sign in failed: " + task.getException().getMessage(),
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
