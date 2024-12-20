package com.example.eventsclientapp1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                // Handle event click (e.g., navigate to event details)
                Toast.makeText(getContext(), "Clicked on: " + event.getEventName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveClick(Event event) {
                saveEvent(event);
            }
        }, getContext());
        recyclerViewEvents.setAdapter(eventAdapter);

        loadEvents();

        return view;
    }

    private void loadEvents() {
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
                        Toast.makeText(getContext(), "Error getting events: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveEvent(Event event) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please sign in to save events.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("savedEvents")
                .document(event.getId())
                .set(event)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving event: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
