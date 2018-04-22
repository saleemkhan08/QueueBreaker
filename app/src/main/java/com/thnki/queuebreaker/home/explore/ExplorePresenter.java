package com.thnki.queuebreaker.home.explore;


import java.util.List;

class ExplorePresenter implements DataUpdateListener {
    private ExploreView exploreView;
    private RestaurantsRepository repository;

    ExplorePresenter(ExploreView exploreView) {

        this.exploreView = exploreView;
        this.repository = new FirebaseRestaurantsRepository();
        this.repository.setDataListener(this);
    }

    @Override
    public void onDataChanged(List<Restaurants> restaurantsList) {
        if (restaurantsList.size() > 0) {
            exploreView.displayRestaurants(repository.getAdapter());
        } else {
            exploreView.displayNoRestaurantsFound();
        }
    }
}
