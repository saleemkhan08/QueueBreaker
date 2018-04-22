package com.thnki.queuebreaker.home.explore;


import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thnki.queuebreaker.R;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRestaurantsRepository implements RestaurantsRepository, ValueEventListener {

    private DatabaseReference restaurantsRef;
    private DataUpdateListener listener;

    public FirebaseRestaurantsRepository() {

        restaurantsRef = FirebaseDatabase.getInstance().getReference()
                .child(RestaurantsRepository.RESTAURANTS);
        restaurantsRef.addValueEventListener(this);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new FirebaseRestaurantsAdapter(Restaurants.class, R.layout.restaurant_row,
                RestaurantsViewHolder.class, restaurantsRef);
    }

    @Override
    public void setDataListener(DataUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        List<Restaurants> restaurantsList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            restaurantsList.add(snapshot.getValue(Restaurants.class));
        }
        listener.onDataChanged(restaurantsList);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
