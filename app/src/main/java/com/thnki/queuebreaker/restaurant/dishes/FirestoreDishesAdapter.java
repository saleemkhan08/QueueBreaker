package com.thnki.queuebreaker.restaurant.dishes;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.restaurant.OrderListener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FirestoreDishesAdapter extends RecyclerView.Adapter<DishesViewHolder> implements OrderUpdateListener {

    private List<Dishes> dishes;
    private OnEditDishListener listener;
    private OrderListener orderListener;
    private LinkedHashMap<String, Order> orders;
    private static ArrayList<String> imageUploadProgressList;
    private static ArrayList<String> imageUploadFailList;

    public FirestoreDishesAdapter(OnEditDishListener listener, OrderListener orderListener, List<Dishes> dishes) {
        this.dishes = dishes;
        this.listener = listener;
        this.orderListener = orderListener;
        orders = orderListener.getOrders();
        this.orderListener.addOrderUpdateListener(this);
    }

    public void removeOrderUpdateListener() {
        orderListener.removeOrderUpdateListener(this);
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
        Log.d("ImageUpdate", "onBindViewHolder");
        final Dishes model = dishes.get(position);
        viewHolder.name.setText(model.getName());
        viewHolder.description.setText(model.getDescription());
        String price = viewHolder.itemView.getContext().getString(R.string.Rs) + model.getPrice();
        viewHolder.price.setText(price);
        viewHolder.image.setImageURI(model.getImage());
        updateItemCount(viewHolder, model, getNumOfItems(model));

        updateProgressView(viewHolder, position);

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

    private void updateProgressView(DishesViewHolder viewHolder, int position) {
        if (imageUploadProgressList != null) {
            for (String id : imageUploadProgressList) {
                if (dishes.get(position).getId().equals(id)) {
                    viewHolder.progressContainer.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.progressContainer.setVisibility(View.GONE);
                }
            }
        }
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

    @Override
    public void onOrderUpdate(@Nullable Order updatedOrder) {
        Log.d("OrderUpdate", "onOrderUpdate");
        try {
            for (int i = 0; i < dishes.size(); i++) {
                Dishes dish = dishes.get(i);
                if (updatedOrder.getDishes().getId().equals(dish.getId())) {
                    notifyItemChanged(i);
                    Log.d("OrderUpdate", "i : " + i);
                }
            }
        } catch (NullPointerException e) {
            Log.d("OrderUpdate", e.getMessage());
        }
    }

    public void notifyItemImageUploaded(@Nullable String id, Uri downloadUrl) {
        if (imageUploadProgressList == null) {
            imageUploadProgressList = new ArrayList<>();
        }
        imageUploadProgressList.remove(id);
        for (int i = 0; i < dishes.size(); i++) {
            Dishes dish = dishes.get(i);
            if (dish.getId().equals(id)) {
                dish.setImage(downloadUrl.toString());
                dishes.set(i, dish);
                notifyItemChanged(i);
            }
        }

    }

    public void notifyItemImageUploadFailed(@Nullable String id) {
        Log.d("ImageUpdate", "notifyItemImageUploadFailed");
        if (imageUploadFailList == null) {
            imageUploadFailList = new ArrayList<>();
        }
        imageUploadFailList.add(id);
        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getId().equals(id)) {
                notifyItemChanged(i);
            }
        }
    }

    public void notifyItemImageUploadStarted(@Nullable String id, Uri uri) {
        Log.d("ImageUpdate", "notifyItemImageUploadStarted");
        if (imageUploadProgressList == null) {
            imageUploadProgressList = new ArrayList<>();
        }
        imageUploadProgressList.add(id);
        for (int i = 0; i < dishes.size(); i++) {
            Dishes dish = dishes.get(i);
            if (dish.getId().equals(id)) {
                dish.setImage(uri.toString());
                dishes.set(i, dish);
                notifyItemChanged(i);
            }
        }
    }
}
