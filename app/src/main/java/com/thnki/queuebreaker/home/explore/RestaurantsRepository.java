package com.thnki.queuebreaker.home.explore;

import android.support.v7.widget.RecyclerView;

public interface RestaurantsRepository {
    String RESTAURANTS = "restaurants";

    RecyclerView.Adapter getAdapter();

    void setDataListener(DataUpdateListener listener);
}
