package com.thnki.queuebreaker.restaurant.categories;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.thnki.queuebreaker.home.FirestoreCollectionRepository;

import java.text.MessageFormat;

public class FirestoreMenuCategoriesRepository extends FirestoreCollectionRepository<MenuCategory> {


    private FirestoreMenuCategoriesRepository(String restaurantId) {
        super(MessageFormat.format("/restaurants/{0}/menu_categories", restaurantId));
    }

    @Override
    protected MenuCategory getObject(QueryDocumentSnapshot document) {
        return document.toObject(MenuCategory.class);
    }

    public static FirestoreMenuCategoriesRepository getInstance(String currentRestaurantId) {
        return new FirestoreMenuCategoriesRepository(currentRestaurantId);
    }

    @Override
    protected String getOrderField() {
        return "order";
    }
}
