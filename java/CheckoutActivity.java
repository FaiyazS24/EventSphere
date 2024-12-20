## Developed By: Faiyaz Sattar & Mohit Singh
    
package com.example.eventsclientapp1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCheckout;
    private TextView textViewTotalPrice;
    private EditText editTextCardNumber, editTextExpiryDate, editTextCVV;
    private List<Event> checkoutEventList;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerViewCheckout = findViewById(R.id.recyclerViewCheckout);
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        editTextCardNumber = findViewById(R.id.editTextCardNumber);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextCVV = findViewById(R.id.editTextCVV);

        recyclerViewCheckout.setLayoutManager(new LinearLayoutManager(this));
        checkoutEventList = new ArrayList<>();
        eventAdapter = new EventAdapter(checkoutEventList, null, this); // No interaction needed here
        recyclerViewCheckout.setAdapter(eventAdapter);

        // Get the events passed from CartActivity
        if (getIntent() != null && getIntent().hasExtra("cart_events")) {
            checkoutEventList.addAll((List<Event>) getIntent().getSerializableExtra("cart_events"));
            eventAdapter.notifyDataSetChanged();
        }

        calculateTotalPrice();

        findViewById(R.id.buttonConfirmPurchase).setOnClickListener(v -> validateAndConfirmPurchase());
    }

    private void calculateTotalPrice() {
        double totalPrice = 0;
        for (Event event : checkoutEventList) {
            totalPrice += event.getPrice();
        }
        textViewTotalPrice.setText("Total: $" + String.format("%.2f", totalPrice));
    }

    private void validateAndConfirmPurchase() {
        String cardNumber = editTextCardNumber.getText().toString().trim();
        String expiryDate = editTextExpiryDate.getText().toString().trim();
        String cvv = editTextCVV.getText().toString().trim();

        if (TextUtils.isEmpty(cardNumber)) {
            editTextCardNumber.setError("Card number is required.");
            return;
        }

        if (TextUtils.isEmpty(expiryDate)) {
            editTextExpiryDate.setError("Expiry date is required.");
            return;
        }

        if (TextUtils.isEmpty(cvv)) {
            editTextCVV.setError("CVV is required.");
            return;
        }

        // Basic validation passed
        processPurchase();
    }

    private void processPurchase() {
        for (Event event : checkoutEventList) {
            deductTicket(event); // Deduct ticket from Firestore
            addToPurchasedEvents(event); // Add to purchased events
        }

        clearCart();

        Toast.makeText(this, "Purchase Confirmed!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UpcomingActivity.class)); // Navigates to UpcomingActivity
        finish();
    }


    private void deductTicket(Event event) {
        DocumentReference eventRef = db.collection("events").document(event.getId());
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int availableTickets = documentSnapshot.getLong("ticketsAvailable").intValue();
                if (availableTickets > 0) {
                    eventRef.update("ticketsAvailable", availableTickets - 1)
                            .addOnFailureListener(e -> Toast.makeText(this, "Error deducting ticket: " + e.getMessage(), Toast.LENGTH_LONG).show());
                } else {
                    Toast.makeText(this, event.getEventName() + " is sold out.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToPurchasedEvents(Event event) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("purchasedEvents")
                .document(event.getId())
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    // Event added to purchased events
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving purchased event: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void clearCart() {
        String userId = mAuth.getCurrentUser().getUid();
        for (Event event : checkoutEventList) {
            db.collection("users").document(userId)
                    .collection("cart").document(event.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Removed " + event.getEventName() + " from cart.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error removing event: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}
