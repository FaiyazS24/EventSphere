package com.example.eventsclientapp1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    private RecyclerView      recyclerViewCart;
    private CartAdapter       cartAdapter;
    private List<Event>       cartList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button               buttonCheckout;
    private BottomNavigationView bottomNavigationView;
    private Toolbar              toolbar;

    /**
     *
     * Initializes views, checks user authentication, sets up the toolbar and navigation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Init views
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cart");

        // Init BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart); // Highlighting current item
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                // Navigate > MainActivity
                startActivity(new Intent(CartActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_saved_events) {
                // Navigate > SavedEventsActivity
                startActivity(new Intent(CartActivity.this, SavedEventsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                // Already in CartActivity
                return true;
            }
            return false;
        });

        // Init RecyclerView
        recyclerViewCart.setLayoutManager(new LinearLayoutManager (this));
        cartList = new ArrayList<> ();
        cartAdapter = new CartAdapter (cartList, this);
        recyclerViewCart.setAdapter(cartAdapter);

        // Loading cart items
        loadCartItems();

        buttonCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("cart_events", new ArrayList<>(cartList)); // Pass events
            startActivity(intent);
        });

    }

    /**
     * access collectioin w/ id > acess other collection nosql savedEvents
     * lambda fn if ok clear list then set uid with the event and add
     * */
    private void loadCartItems() {
        // Clearing the existing data
        cartList.clear();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please sign in to view your cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("cart")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cartList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            if (event != null) {
                                event.setId(document.getId());
                                cartList.add(event);
                            }
                        }
                        cartAdapter.notifyDataSetChanged();
                    } else {
                        String errorMsg = task.getException().getMessage();
                        Log.e("CartActivity", "Error loading cart items: " + errorMsg);
                        Toast.makeText(this, "Error loading cart items: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void checkout() {
        // Implementing checkout functionality
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please sign in to checkout.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Event event : cartList) {
            // Performing purchase logic
            purchaseEvent(event);
        }
    }

    private void purchaseEvent(Event event) {

        DocumentReference eventRef
                = db.collection("events").document(event.getId());

        DocumentReference userPurchaseRef
                = db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .collection("purchases").document(event.getId());

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            long ticketsAvailable = snapshot.getLong("ticketsAvailable");

            if (ticketsAvailable > 0) {
                transaction.update(eventRef, "ticketsAvailable", ticketsAvailable - 1);
                transaction.set(userPurchaseRef, event);
                // Removing from cart
                transaction.delete(db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .collection("cart").document(event.getId()));
            } else {
                throw new FirebaseFirestoreException ("No tickets available for " + event.getEventName(),
                        FirebaseFirestoreException.Code.ABORTED);
            }
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(CartActivity.this, "Purchased: " + event.getEventName(), Toast.LENGTH_SHORT).show();
            cartList.remove(event);
            cartAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(CartActivity.this, "Purchase failed for " + event.getEventName() + ": " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onRemoveFromCart(Event event) {
        // Implementing remove from cart functionality
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please sign in to remove items from your cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("cart").document(event.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    cartList.remove(event);
                    cartAdapter.notifyDataSetChanged();
                    Toast.makeText(CartActivity.this, "Event removed from cart.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Error removing from cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("CartActivity", "onResume called");
        loadCartItems ();
    }

}
