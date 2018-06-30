package com.thnki.queuebreaker.home.explore;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thnki.queuebreaker.R;

import java.util.List;

public class FirestoreRestaurantAdapter extends RecyclerView.Adapter<RestaurantsViewHolder> {

    private List<Restaurants> restaurantsList;
    private ExplorePresenter presenter;

    FirestoreRestaurantAdapter(List<Restaurants> restaurantsList, ExplorePresenter presenter){
        this.restaurantsList = restaurantsList;
        this.presenter = presenter;
    }
    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_row, parent, false);
        return new RestaurantsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder viewHolder, int position) {
        Restaurants model = restaurantsList.get(position);
        viewHolder.restaurantName.setText(model.name);
        viewHolder.restaurantAddress.setText(model.address);
        viewHolder.restaurantIcon.setImageURI(model.icon);
        viewHolder.restaurantRating.setRating(model.rating);
        //viewHolder.restaurantCard.setOnClickListener(view -> EventBus.getDefault().post(model));
        viewHolder.restaurantCard.setOnClickListener(view -> presenter.launchMenu(model));
    }

    @Override
    public int getItemCount() {
        return restaurantsList.size();
    }
}
