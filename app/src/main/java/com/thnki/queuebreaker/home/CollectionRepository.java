package com.thnki.queuebreaker.home;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public interface CollectionRepository<T> {

    List<T> getCollection();

    void addCollectionListener(CollectionListener<T> listener, String key);

    void clearListeners();

    void notifyAllListeners();

    void removeCollectionListener(String key);

    String addItem(T item, OnSuccessListener<? super Void> success, OnFailureListener failure);

    void removeItem(String id, OnSuccessListener<? super Void> success, OnFailureListener failure);

    void removeItem(String id);
}
