package com.thnki.queuebreaker.restaurant.dishes;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.thnki.queuebreaker.home.FirestoreCollectionRepository;

import java.text.MessageFormat;

public class FirestoreDishRepository extends FirestoreCollectionRepository<Dishes> {

    private FirestoreDishRepository(String restaurantId, String categoryId) {
        super(MessageFormat.format("/restaurants/{0}/menu_categories/{1}/dishes", restaurantId, categoryId));
    }

    @Override
    protected Dishes getObject(QueryDocumentSnapshot document) {
        return document.toObject(Dishes.class);
    }

    public static FirestoreDishRepository getInstance(String currentRestaurantId, String categoryId) {
        return new FirestoreDishRepository(currentRestaurantId, categoryId);
    }

    @Override
    protected String getOrderField() {
        return "order";
    }
}
