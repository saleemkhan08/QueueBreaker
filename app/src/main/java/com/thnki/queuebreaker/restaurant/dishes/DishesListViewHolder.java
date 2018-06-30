package com.thnki.queuebreaker.restaurant.dishes;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.utils.SquareImageView;

class DishesListViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    SquareImageView image;
    TextView description;

    DishesListViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.dish_name);
        description = itemView.findViewById(R.id.dish_description);
        image = itemView.findViewById(R.id.dishImage);

    }
}


