## Developed By: Faiyaz Sattar & Mohit Singh
    
package com.example.eventsclientapp1;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextCurrentPassword, editTextNewPassword;
    private Button buttonUpdatePassword, buttonDeleteAccount;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);

        // Set up Update Password Button
        buttonUpdatePassword.setOnClickListener(v -> updatePassword());

        // Set up Delete Account Button
        buttonDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void updatePassword() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter both current and new passwords.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            editTextNewPassword.setError("Password must be at least 6 characters.");
            return;
        }

        if (currentUser != null && currentUser.getEmail() != null) {
            // Reauthenticate user
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update password
                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(passwordTask -> {
                                        if (passwordTask.isSuccessful()) {
                                            Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Failed to update password: " + passwordTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Reauthentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account? You will be signed out automatically.");
        builder.setPositiveButton("Yes", (dialog, which) -> deleteAccount());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteAccount() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            editTextCurrentPassword.setError("Current password is required.");
            return;
        }

        if (currentUser != null && currentUser.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser.delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to delete account: " + deleteTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Reauthentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }
    }
}
