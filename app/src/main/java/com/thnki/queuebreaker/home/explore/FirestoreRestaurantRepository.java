package com.thnki.queuebreaker.home.explore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.thnki.queuebreaker.home.FirestoreCollectionRepository;

public class FirestoreRestaurantRepository extends FirestoreCollectionRepository<Restaurants> {

    private static final String RESTAURANTS = "restaurants";

    FirestoreRestaurantRepository() {
        super(RESTAURANTS);
    }

    @Override
    protected String getOrderField() {
        return "restaurantId";
    }

    @Override
    protected Restaurants getObject(QueryDocumentSnapshot document) {
        return document.toObject(Restaurants.class);
    }

}
