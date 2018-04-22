package com.thnki.queuebreaker.home.explore;


import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

public class FirebaseRestaurantsAdapter extends FirebaseRecyclerAdapter<Restaurants, RestaurantsViewHolder> {

    public FirebaseRestaurantsAdapter(Class<Restaurants> modelClass, int modelLayout, Class<RestaurantsViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(final RestaurantsViewHolder viewHolder, final Restaurants model, int position) {

        viewHolder.restaurantName.setText(model.name);
        viewHolder.restaurantAddress.setText(model.address);
        viewHolder.restaurantIcon.setImageURI(model.icon);
        viewHolder.restaurantRating.setRating(model.rating);
        viewHolder.restaurantCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Context context = view.getContext();
//                Intent intent = new Intent(context, OrderingActivity.class);
//                intent.putExtra(OrderingActivity.RESTAURANT_ID, model.restaurantId);
//                view.getContext().startActivity(intent);
            }
        });

    }
}
