package com.thnki.queuebreaker.home.explore;


import android.support.v7.widget.RecyclerView;

public interface ExploreView {
    void displayRestaurants(RecyclerView.Adapter restaurantsList);
    void displayNoRestaurantsFound();
}
