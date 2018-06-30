package com.thnki.queuebreaker.restaurant.dishes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.restaurant.OrderListener;

import java.util.LinkedHashMap;
import java.util.List;

public class FirestoreDishesAdapter extends RecyclerView.Adapter<DishesViewHolder> {

    private List<Dishes> dishes;
    private OnEditDishListener listener;
    private OrderListener orderListener;
    private LinkedHashMap<String, Order> orders;

    public FirestoreDishesAdapter(OnEditDishListener listener, OrderListener orderListener, List<Dishes> dishes) {
        this.dishes = dishes;
        this.listener = listener;
        this.orderListener = orderListener;
        orders = orderListener.getOrders();
    }

    @NonNull
    @Override
    public DishesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_card_row, parent, false);
        return new DishesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DishesViewHolder viewHolder, int position) {
        final Dishes model = dishes.get(position);
        viewHolder.name.setText(model.getName());
        viewHolder.description.setText(model.getDescription());
        String price = viewHolder.itemView.getContext().getString(R.string.Rs) + model.getPrice();
        viewHolder.price.setText(price);
        viewHolder.image.setImageURI(model.getImage());
        updateItemCount(viewHolder, model, getNumOfItems(model));

        viewHolder.addToCart.setOnClickListener(view -> {
            updateItemCount(viewHolder, model, orderListener.onDishAdded(model));
        });

        viewHolder.increaseItemCount.setOnClickListener(view -> {
            updateItemCount(viewHolder, model, orderListener.onDishCountIncremented(model));
        });

        viewHolder.decreaseItemCount.setOnClickListener(view -> {
            updateItemCount(viewHolder, model, orderListener.onDishCountDecremented(model));
        });

        viewHolder.editDish.setOnClickListener(view -> {
            listener.editDish(model);
        });
    }

    private void updateItemCount(@NonNull DishesViewHolder viewHolder, Dishes model, int itemCount) {
        if (itemCount > 0) {
            viewHolder.showCounter();
            viewHolder.numberOfItems.setText(itemCount + "");
        } else {
            viewHolder.showAddToCart();
            orderListener.onDishRemoved(model);
        }
    }

    private int getNumOfItems(Dishes model) {
        if (orders == null) {
            return 0;
        }
        Order order = orders.get(model.getId());
        if (order == null) {
            return 0;
        }
        return order.getCount();
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}
