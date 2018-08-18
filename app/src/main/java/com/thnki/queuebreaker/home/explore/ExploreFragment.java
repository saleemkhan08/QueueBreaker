package com.thnki.queuebreaker.home.explore;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.restaurant.MenuKotlinActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ExploreFragment extends Fragment implements ExploreView {

    public static final String RESTAURANT = "restaurant";

    @BindView(R.id.restaurants_recycler_view)
    RecyclerView restaurantsRecyclerView;

    @BindView(R.id.no_restaurants_found)
    TextView noRestaurantsFound;

    @BindView(R.id.restaurants_progress_bar)
    ProgressBar progressBar;

    ExplorePresenter presenter;
    private Unbinder unbinder;

    public static ExploreFragment getInstance() {
        return new ExploreFragment();
    }

    public ExploreFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.explore_fragment, container, false);
        unbinder = ButterKnife.bind(this, parentView);
        presenter = new ExplorePresenter(this);
        return parentView;
    }

    private void setUpRecyclerView(RecyclerView.Adapter restaurantsAdapter) {
        restaurantsRecyclerView.setAdapter(restaurantsAdapter);
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void displayRestaurants(List<Restaurants> restaurantsList) {
        noRestaurantsFound.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        RecyclerView.Adapter restaurantsAdapter = new FirestoreRestaurantAdapter(restaurantsList, presenter);
        setUpRecyclerView(restaurantsAdapter);
    }

    @Override
    public void displayNoRestaurantsFound() {
        progressBar.setVisibility(View.GONE);
        noRestaurantsFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void launchMenuActivity(Restaurants model) {
        Intent intent = new Intent(getActivity(), MenuKotlinActivity.class);
        intent.putExtra(RESTAURANT, model);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.clearListeners();
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
