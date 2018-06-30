package com.thnki.queuebreaker.home.explore;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.utils.SquareImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

class RestaurantsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.restaurant_name)
    public TextView restaurantName;

    @BindView(R.id.restaurant_address)
    public TextView restaurantAddress;

    @BindView(R.id.restaurant_icon)
    public SquareImageView restaurantIcon;

    @BindView(R.id.restaurant_rating)
    public RatingBar restaurantRating;

    @BindView(R.id.restaurant_card)
    RelativeLayout restaurantCard;

    public RestaurantsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}