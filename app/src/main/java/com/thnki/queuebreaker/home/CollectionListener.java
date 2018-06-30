package com.thnki.queuebreaker.home;

import java.util.ArrayList;

public interface CollectionListener<T> {
    void onCollectionChanged(ArrayList<T> collection);
}
