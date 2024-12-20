package com.example.eventsclientapp1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SavedEventsActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener {

    private RecyclerView recyclerViewSavedEvents;
    private EventAdapter eventAdapter;
    private List<Event> savedEventList;
    private FirebaseFirestore db;
    private FirebaseAuth         mAuth;
    private BottomNavigationView bottomNavigationView;
    private Toolbar              toolbar;


    /**
     *
     * Initializes views, checks user authentication, sets up the toolbar and navigation.
     *
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Saved Events");

        // Initializing BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_saved_events); // Highlight current item
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                // Navigate >  MainActivity
                startActivity(new Intent(SavedEventsActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_saved_events) {
                // Already in SavedEventsActivity
                return true;
            } else if (id == R.id.nav_cart) {
                // Navigate >  CartActivity
                startActivity(new Intent(SavedEventsActivity.this, CartActivity.class));
                finish();
                return true;
            }
            return false;
        });



        // Initialize RecyclerView
        recyclerViewSavedEvents = findViewById(R.id.recyclerViewSavedEvents);
        recyclerViewSavedEvents.setLayoutManager(new LinearLayoutManager(this));

        savedEventList = new ArrayList<>();
        eventAdapter = new EventAdapter(savedEventList, this, this);
        recyclerViewSavedEvents.setAdapter(eventAdapter);

        loadSavedEvents();
    }

    /**Fetches saved events from the user's savedEvents subcollection.
     *
     *
     * access collectioin w/ id > acess other collection nosql savedEvents
     * lambda fn if ok clear list then set uid with the event and add
     * */

    private void loadSavedEvents() {
        // Clearing existing data
        savedEventList.clear();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please sign in to view saved events.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("savedEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        savedEventList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            if (event != null) {
                                event.setId(document.getId());
                                savedEventList.add(event);
                            }
                        }
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("SavedEventsActivity", "Error getting saved events", task.getException());
                        Toast.makeText(this, "Error getting saved events: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    // Implement EventAdapter.OnItemClickListener methods
    @Override
    public void onItemClick(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }

    @Override
    public void onSaveClick(Event event) {
        //  unsave functionality
    }

    // sign-out toolbar

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("SavedEventsActivity", "onResume called");

        loadSavedEvents();
    }

}
