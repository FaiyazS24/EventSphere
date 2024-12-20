package com.example.eventsclientapp1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUpcoming;
    private EventAdapter eventAdapter;
    private List<Event> upcomingEvents;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerViewUpcoming = findViewById(R.id.recyclerViewUpcoming);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(this));

        upcomingEvents = new ArrayList<>();
        eventAdapter = new EventAdapter(upcomingEvents, null, this); // Disable clicks
        recyclerViewUpcoming.setAdapter(eventAdapter);

        loadPurchasedEvents();
    }

    @SuppressLint("NewApi")
    private void loadPurchasedEvents() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("purchasedEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        upcomingEvents.clear();
                        task.getResult().forEach(document -> {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());
                            upcomingEvents.add(event);
                        });
                        movePastEvents();
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading purchased events: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void movePastEvents() {
        String userId = mAuth.getCurrentUser().getUid();
        List<Event> pastEvents = new ArrayList<>();
        List<Event> upcomingEventsList = new ArrayList<>();

        // Format for date comparison
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        Date currentDate = new Date();

        try {
            // Strip time from current date
            currentDate = dateFormat.parse(dateFormat.format(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Event event : upcomingEvents) {
            try {
                // Parse event date
                Date eventDate = dateFormat.parse(event.getEventDate());

                if (eventDate != null) {
                    // Compare eventDate with currentDate
                    if (eventDate.before(currentDate)) {
                        pastEvents.add(event); // Add to past events
                    } else {
                        upcomingEventsList.add(event); // Keep in upcoming events
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Update Firestore for past events
        for (Event pastEvent : pastEvents) {
            db.collection("users").document(userId).collection("pastEvents")
                    .document(pastEvent.getId())
                    .set(pastEvent)
                    .addOnSuccessListener(aVoid -> {
                        // Remove from purchasedEvents
                        db.collection("users").document(userId).collection("purchasedEvents")
                                .document(pastEvent.getId())
                                .delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    upcomingEvents.remove(pastEvent);
                                    eventAdapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to remove from purchasedEvents", e));
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to move to pastEvents", e));
        }
    }

}

