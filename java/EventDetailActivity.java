## Developed By: Faiyaz Sattar & Mohit Singh
    
package com.example.eventsclientapp1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {

    private TextView textViewEventName, textViewDescription, textViewAvailableTickets, textViewPrice, textViewLocation, textviewDate;
    private ImageView imageViewEvent;
    private Button buttonAddToCart;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String eventId;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialize views
        textViewEventName = findViewById(R.id.textViewEventName);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewAvailableTickets = findViewById(R.id.textViewAvailableTickets);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewLocation = findViewById(R.id.textViewLocation);
        textviewDate = findViewById(R.id.textViewDate);
        imageViewEvent = findViewById(R.id.imageViewEvent);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get event ID from intent
        eventId = getIntent().getStringExtra("event_id");

        if (eventId != null) {
            loadEventDetails();
        } else {
            Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonAddToCart.setOnClickListener(v -> addToCart());
    }

    private void loadEventDetails() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            event.setId(documentSnapshot.getId());
                            displayEventDetails();
                        }
                    } else {
                        Toast.makeText(EventDetailActivity.this, "Event does not exist.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EventDetailActivity.this, "Error loading event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void displayEventDetails() {
        textViewEventName.setText(event.getEventName());
        textViewDescription.setText(event.getDescription());
        textViewPrice.setText("$" + event.getPrice());
        textViewLocation.setText(event.getLocation());
        textviewDate.setText(event.getEventDate());

        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.loading)
                    .into(imageViewEvent);
        }

        ImageView availabilityImage = findViewById(R.id.imageView3);

        int ticketsAvailable = event.getTicketsAvailable(); // Use the integer value directly
        if (ticketsAvailable > 0) {
            availabilityImage.setImageResource(R.drawable.available);
            textViewAvailableTickets.setText("Tickets Available: " + event.getTicketsAvailable());
        } else {
            availabilityImage.setImageResource(R.drawable.unavailable);
            textViewAvailableTickets.setText("Tickets Unavailable");
        }

    }

    private void addToCart() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please sign in to add items to the cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")
                .document(event.getId())
                .set(event)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Event added to cart!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding to cart: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
