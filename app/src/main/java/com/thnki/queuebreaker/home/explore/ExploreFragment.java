package com.thnki.queuebreaker.home.explore;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thnki.queuebreaker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExploreFragment extends Fragment implements ExploreView {

    @BindView(R.id.search_bar)
    EditText searchBar;

    @BindView(R.id.restaurants_recycler_view)
    RecyclerView restaurantsRecyclerView;

    @BindView(R.id.no_restaurants_found)
    TextView noRestaurantsFound;

    @BindView(R.id.restaurants_progress_bar)
    ProgressBar progressBar;

    ExplorePresenter presenter;

    public static ExploreFragment getInstance() {
        return new ExploreFragment();
    }

    public ExploreFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.explore_fragment, container, false);
        ButterKnife.bind(this, parentView);
        presenter = new ExplorePresenter(this);
        return parentView;
    }

    @Override
    public void displayRestaurants(RecyclerView.Adapter restaurantsAdapter) {
        noRestaurantsFound.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        setUpRecyclerView(restaurantsAdapter);
    }

    private void setUpRecyclerView(RecyclerView.Adapter restaurantsList) {
        restaurantsRecyclerView.setAdapter(restaurantsList);
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void displayNoRestaurantsFound() {
        noRestaurantsFound.setVisibility(View.VISIBLE);
    }


}
