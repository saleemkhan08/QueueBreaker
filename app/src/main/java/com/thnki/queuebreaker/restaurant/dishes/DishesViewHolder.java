package com.thnki.queuebreaker.restaurant.dishes;


import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.utils.SquareImageView;

class DishesViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    SquareImageView image;
    TextView description;
    TextView price;
    FloatingActionButton addToCart;
    ImageView increaseItemCount;
    TextSwitcher numberOfItems;
    ImageView decreaseItemCount;
    ImageView editDish;

    DishesViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.dish_name);
        description = itemView.findViewById(R.id.dish_description);
        image = itemView.findViewById(R.id.dishImage);
        price = itemView.findViewById(R.id.dish_price);
        increaseItemCount = itemView.findViewById(R.id.increaseItemCount);
        numberOfItems = itemView.findViewById(R.id.numberOfItems);
        decreaseItemCount = itemView.findViewById(R.id.decreaseItemCount);
        addToCart = itemView.findViewById(R.id.addToCart);
        editDish = itemView.findViewById(R.id.editDish);
        numberOfItems.setFactory(() -> {
            Context context = itemView.getContext();
            TextView textView = new TextView(context);
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            textView.setGravity(Gravity.CENTER);
            return textView;
        });
    }

    void showAddToCart(){
        addToCart.setVisibility(View.VISIBLE);
        increaseItemCount.setVisibility(View.INVISIBLE);
        decreaseItemCount.setVisibility(View.INVISIBLE);
        numberOfItems.setVisibility(View.INVISIBLE);
    }

    void showCounter(){
        addToCart.setVisibility(View.INVISIBLE);
        increaseItemCount.setVisibility(View.VISIBLE);
        decreaseItemCount.setVisibility(View.VISIBLE);
        numberOfItems.setVisibility(View.VISIBLE);
    }

}