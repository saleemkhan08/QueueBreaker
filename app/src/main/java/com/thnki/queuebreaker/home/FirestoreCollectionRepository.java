package com.thnki.queuebreaker.home;

import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thnki.queuebreaker.model.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FirestoreCollectionRepository<T extends FirestoreDocumentModel>
        implements EventListener<QuerySnapshot>, CollectionRepository<T> {

    private final CollectionReference collectionRef;
    private final Query collectionQuery;
    private ArrayList<T> itemList;
    private Map<String, CollectionListener<T>> listeners;

    public FirestoreCollectionRepository(String path) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        collectionRef = firestore.collection(path);
        collectionQuery = collectionRef.orderBy(getOrderField());
        collectionQuery.addSnapshotListener(this);
        listeners = new HashMap<>();
        itemList = new ArrayList<>();
    }

    protected abstract String getOrderField();


    public void addCollectionListener(CollectionListener<T> listener, String key) {
        this.listeners.put(key, listener);
    }

    @Override
    public void removeCollectionListener(String key) {
        this.listeners.remove(key);
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            ToastMsg.show(e.getMessage());
        }
        if (queryDocumentSnapshots != null) {
            for (DocumentChange snapshot : queryDocumentSnapshots.getDocumentChanges()) {
                QueryDocumentSnapshot document = snapshot.getDocument();
                T firestoreDocumentModel = getObject(document);
                firestoreDocumentModel.setId(document.getId());
                switch (snapshot.getType()) {
                    case ADDED:
                        itemList.add(firestoreDocumentModel);
                        break;
                    case REMOVED:
                        removeItems(firestoreDocumentModel);
                        break;
                    case MODIFIED:
                        updateItems(firestoreDocumentModel);
                }
            }
        }
        notifyAllListeners();
    }

    @Override
    public void notifyAllListeners() {
        for (Map.Entry<String, CollectionListener<T>> listenerEntrySet : listeners.entrySet()) {
            listenerEntrySet.getValue().onCollectionChanged(itemList);
        }
    }

    protected abstract T getObject(QueryDocumentSnapshot document);

    private void updateItems(T firestoreDocumentModel) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getId().equals(firestoreDocumentModel.getId())) {
                itemList.set(i, firestoreDocumentModel);
            }
        }
    }

    private void removeItems(T firestoreDocumentModel) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getId().equals(firestoreDocumentModel.getId())) {
                itemList.remove(i);
            }
        }
    }

    @Override
    public List<T> getCollection() {
        return itemList;
    }

    @Override
    public void clearListeners() {
        listeners.clear();
    }

    @Override
    public String addItem(T item, OnSuccessListener<? super Void> success, OnFailureListener failure) {

        DocumentReference docRef = collectionRef.document();
        String id = docRef.getId();
        item.setId(id);
        docRef.set(item).addOnSuccessListener(success)
                .addOnFailureListener(failure);
        return id;
    }

    @Override
    public void removeItem(String id, OnSuccessListener<? super Void> success, OnFailureListener failure) {
        collectionRef.document(id)
                .delete()
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }

    @Override
    public void removeItem(String id) {
        collectionRef.document(id)
                .delete();
    }

    public void updateItem(T item, OnSuccessListener<? super Void> success, OnFailureListener failure) {
        collectionRef.document(item.getId())
                .set(item)
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }
}
