package com.thnki.queuebreaker.restaurant.dishes;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.restaurant.ItemTouchHelperAdapter;
import com.thnki.queuebreaker.restaurant.OnStartDragListener;

import java.util.Collections;
import java.util.List;

public class FirestoreDishesListAdapter extends RecyclerView.Adapter<DishesListViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Dishes> dishes;
    private OnStartDragListener dragStartListener;

    public FirestoreDishesListAdapter(List<Dishes> menuCategories, OnStartDragListener dragStartListener) {

        this.dragStartListener = dragStartListener;
        this.dishes = menuCategories;
    }

    @NonNull
    @Override
    public DishesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DishesListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_row, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull DishesListViewHolder viewHolder, int position) {
        Dishes dish = dishes.get(position);
        viewHolder.name.setText(dish.getName());
        viewHolder.description.setText(dish.getDescription());
        viewHolder.image.setImageURI(dish.getImage());
        viewHolder.itemView.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(viewHolder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        dishes.get(fromPosition).setOrder(toPosition);
        dishes.get(toPosition).setOrder(fromPosition);
        Collections.swap(dishes, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        dishes.remove(position);
        notifyItemRemoved(position);
    }

}
