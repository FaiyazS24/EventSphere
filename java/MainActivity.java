## Developed By: Faiyaz Sattar & Mohit Singh

package com.example.eventsclientapp1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsclientapp1.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener {

    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    /**
     *
     * Initializes views, checks user authentication, sets up the toolbar and navigation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore.setLoggingEnabled(true);
        // Initializing Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Checking if user is signed in
        if (mAuth.getCurrentUser() == null) {
            // No user is signed in, redirecting to SignInActivity
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            return;
        }

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Events");

        // Init BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_events); // Highlighting current item
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                // Already in MainActivity
                return true;
            } else if (id == R.id.nav_saved_events) {
                // Navigate > SavedEventsActivity
                startActivity(new Intent(MainActivity.this, SavedEventsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                // Navigate > CartActivity
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                finish();
                return true;
            }
            return false;
        });


        // Init RV //
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this, this);
        recyclerViewEvents.setAdapter(eventAdapter);

        // Loading events from Firestore
        loadEvents();
    }


    /**
     * Fetches events from Firestore and updates the RecyclerView.
     *
     *
     * access collectioin w/ id > acess other collection nosql savedEvents
     * lambda fn if ok clear list then set uid with the event and add
     * */

    private void loadEvents() {
        // Clear the existing data
        eventList.clear();

        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());
                            eventList.add(event);
                        }
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        String errorMsg = task.getException().getMessage();
                        Log.e("MainActivity", "Error getting events: " + errorMsg);
                        Toast.makeText(MainActivity.this, "Error getting events: " + errorMsg,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * Handles event item clicks to show event details.
     *
     */
    @Override
    public void onItemClick(Event event) {// Implement EventAdapter.OnItemClickListener methods
        Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }

    /**
     *
     * Handles saving events to the user's saved events.
     */
    @Override
    public void onSaveClick(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please sign in to save events.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("savedEvents")
                .document(event.getId())
                .set(event)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving event: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // sign-out toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); //main menu from menu/main_menu.xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MainActivity", "Menu item selected: " + item.getTitle());

        if (item.getItemId() == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.action_upcoming) {
            startActivity(new Intent(this, UpcomingActivity.class));
            return true;
        }
        else if (item.getItemId() == R.id.action_past) {
            startActivity(new Intent(this, PastEventsActivity.class));
            return true;
        }
        else if (item.getItemId() == R.id.action_update_profile) {
            startActivity(new Intent(this, UpdateProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume called");

        loadEvents();
    }

}
