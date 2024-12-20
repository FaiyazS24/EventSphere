package com.example.eventsclientapp1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Event> cartList;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onRemoveFromCart(Event event);
    }

    public CartAdapter(List<Event> cartList, OnCartItemListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView textViewEventName, textViewLocation, textViewPrice;
        Button buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }

        public void bind(Event event, OnCartItemListener listener) {
            buttonRemove.setOnClickListener(v -> listener.onRemoveFromCart(event));
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Event event = cartList.get(position);

        holder.textViewEventName.setText(event.getEventName());
        holder.textViewLocation.setText(event.getLocation());
        holder.textViewPrice.setText("Price: $" + event.getPrice());

        // Load image using Glide
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(holder.eventImage.getContext())
                    .load(event.getImageUrl())
                    .into(holder.eventImage);
        }

        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
