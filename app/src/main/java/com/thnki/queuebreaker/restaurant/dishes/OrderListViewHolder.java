package com.thnki.queuebreaker.restaurant.dishes;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.thnki.queuebreaker.R;

class OrderListViewHolder extends DishesListViewHolder {
    ImageView increaseItemCount;
    TextSwitcher numberOfItems;
    ImageView decreaseItemCount;
    TextSwitcher totalPrice;

    OrderListViewHolder(View itemView) {
        super(itemView);
        increaseItemCount = itemView.findViewById(R.id.increaseItemCount);
        numberOfItems = itemView.findViewById(R.id.numberOfItems);
        decreaseItemCount = itemView.findViewById(R.id.decreaseItemCount);
        totalPrice = itemView.findViewById(R.id.total_price);

        numberOfItems.setFactory(() -> {
            Context context = itemView.getContext();
            TextView textView = new TextView(context);
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(18);
            return textView;
        });

        totalPrice.setFactory(() -> {
            Context context = itemView.getContext();
            TextView textView = new TextView(context);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(24);
            return textView;
        });
    }
}


