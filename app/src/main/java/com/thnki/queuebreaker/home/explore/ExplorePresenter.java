package com.thnki.queuebreaker.home.explore;


import com.thnki.queuebreaker.home.CollectionListener;
import com.thnki.queuebreaker.home.CollectionRepository;

import java.util.ArrayList;

class ExplorePresenter implements CollectionListener<Restaurants> {
    private static final String TAG = "ExplorePresenter";
    private ExploreView exploreView;
    private CollectionRepository<Restaurants> repository;

    ExplorePresenter(ExploreView exploreView) {
        this.exploreView = exploreView;
        repository = new FirestoreRestaurantRepository();
        repository.addCollectionListener(this, TAG);
    }

    @Override
    public void onCollectionChanged(ArrayList<Restaurants> restaurantsList) {
        if (restaurantsList.size() > 0) {
            exploreView.displayRestaurants(restaurantsList);
        } else {
            exploreView.displayNoRestaurantsFound();
        }
    }

    public void launchMenu(Restaurants model) {
        exploreView.launchMenuActivity(model);
    }

    public void clearListeners() {
        repository.clearListeners();
    }
}
