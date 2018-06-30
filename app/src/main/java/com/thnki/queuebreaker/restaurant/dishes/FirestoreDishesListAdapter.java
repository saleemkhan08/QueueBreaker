package com.thnki.queuebreaker.restaurant.dishes;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.home.FirestoreDocumentModel;
import com.thnki.queuebreaker.restaurant.ItemTouchHelperAdapter;
import com.thnki.queuebreaker.restaurant.OnStartDragListener;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class FirestoreDishesListAdapter<T extends FirestoreDocumentModel> extends RecyclerView.Adapter<DishesListViewHolder>
        implements ItemTouchHelperAdapter {

    private static final int ORDER_LIST = 2;
    private static final int DISH_LIST = 1;
    private List<T> dishes;
    private OnStartDragListener dragStartListener;

    public FirestoreDishesListAdapter(List<T> menuCategories, OnStartDragListener dragStartListener) {

        this.dragStartListener = dragStartListener;
        this.dishes = menuCategories;
    }

    @NonNull
    @Override
    public DishesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ORDER_LIST) {
            return new OrderListViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.order_row, parent, false));
        }
        return new DishesListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_row, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull DishesListViewHolder viewHolder, int position) {
        FirestoreDocumentModel model = dishes.get(position);
        Dishes dish;
        if (model instanceof Dishes) {
            dish = (Dishes) model;
        } else {
            Order order = ((Order) model);
            dish = order.getDishes();
            int price = Integer.parseInt(dish.getPrice()) * order.getCount();
            String priceStr = viewHolder.itemView.getContext().getString(R.string.Rs) + price;
            ((OrderListViewHolder) viewHolder).numberOfItems
                    .setText(MessageFormat.format("{0}", order.getCount()));

            ((OrderListViewHolder) viewHolder).totalPrice
                    .setText(priceStr);

        }
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
    public int getItemViewType(int position) {
        return dishes.get(position) instanceof Dishes ? DISH_LIST : ORDER_LIST;
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
