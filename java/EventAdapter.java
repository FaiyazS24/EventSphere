## Developed By: Faiyaz Sattar & Mohit Singh
    
package com.example.eventsclientapp1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Event event);
        void onSaveClick(Event event);
    }

    public EventAdapter(List<Event> eventList, OnItemClickListener listener, Context context) {
        this.eventList = eventList;
        this.listener = listener;
        this.context = context;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventName, eventDate, eventPrice, eventLocation, purchasedText;
        ImageButton saveButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventPrice = itemView.findViewById(R.id.eventPrice);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            saveButton = itemView.findViewById(R.id.saveButton);
            purchasedText = itemView.findViewById(R.id.purchasedText);
        }

        public void bind(Event event, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(event));
            saveButton.setOnClickListener(v -> listener.onSaveClick(event));
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventDate());
        holder.eventPrice.setText("Price: $" + event.getPrice());
        holder.eventLocation.setText(event.getLocation());

        // Load event image
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(holder.eventImage.getContext())
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.loading)
                    .into(holder.eventImage);
        }

        if (context instanceof PastEventsActivity) {
            holder.saveButton.setVisibility(View.GONE);
            holder.purchasedText.setVisibility(View.VISIBLE);
            holder.purchasedText.setText("Attended");
            holder.purchasedText.setTextColor(Color.RED);
        }
        else if (context instanceof UpcomingActivity) {
            holder.saveButton.setVisibility(View.GONE);
            holder.purchasedText.setVisibility(View.VISIBLE);
            holder.purchasedText.setTextColor(Color.GREEN);
        } else {
            holder.saveButton.setVisibility(View.VISIBLE);
            holder.purchasedText.setVisibility(View.GONE);
        }

        if (!(context instanceof UpcomingActivity) && !(context instanceof PastEventsActivity)) {
            holder.bind(event, listener);
        }

        // Enable long press deletion only if the context is SavedEventsActivity
        if (context instanceof SavedEventsActivity) {
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteConfirmationDialog(event, position);
                return true;
            });
        }
        else if (context instanceof PastEventsActivity) {
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteConfirmationDialog(event, position);
                return true;
            });
        }
    }

    private void showDeleteConfirmationDialog(Event event, int position) {
        String title, message, collectionPath;

        if (context instanceof SavedEventsActivity) {
            title = "Delete Saved Event";
            message = "Are you sure you want to delete this event from your saved events?";
            collectionPath = "savedEvents";
        } else if (context instanceof PastEventsActivity) {
            title = "Delete Past Event";
            message = "Are you sure you want to delete this event from your past events?";
            collectionPath = "pastEvents";
        } else {
            return; // Exit if not a recognized activity
        }

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent(event, position, collectionPath))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    private void deleteEvent(Event event, int position, String collectionPath) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection(collectionPath)
                .document(event.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    eventList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, eventList.size());
                    Toast.makeText(context, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
