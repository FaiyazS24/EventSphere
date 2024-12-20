## Developed By: Faiyaz Sattar & Mohit Singh
    
package com.example.eventsclientapp1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PastEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPastEvents;
    private EventAdapter eventAdapter;
    private List<Event> pastEventsList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerViewPastEvents = findViewById(R.id.recyclerViewPastEvents);
        recyclerViewPastEvents.setLayoutManager(new LinearLayoutManager(this));

        pastEventsList = new ArrayList<>();
        eventAdapter = new EventAdapter(pastEventsList, null, this);
        recyclerViewPastEvents.setAdapter(eventAdapter);

        loadPastEvents();
    }

    @SuppressLint("NewApi")
    private void loadPastEvents() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("pastEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pastEventsList.clear();
                        task.getResult().forEach(document -> {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());
                            pastEventsList.add(event);
                        });
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading past events: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
