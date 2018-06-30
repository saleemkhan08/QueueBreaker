package com.thnki.queuebreaker.home.explore;


import java.util.List;

public interface ExploreView {
    void displayRestaurants(List<Restaurants> restaurantsList);
    void displayNoRestaurantsFound();
    void launchMenuActivity(Restaurants model);
}
