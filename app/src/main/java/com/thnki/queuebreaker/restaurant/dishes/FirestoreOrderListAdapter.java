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
import com.thnki.queuebreaker.restaurant.OrderListener;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class FirestoreOrderListAdapter extends RecyclerView.Adapter<OrderListViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Order> orders;
    private OnStartDragListener dragStartListener;
    private OrderListener orderListener;


    public FirestoreOrderListAdapter(List<Order> menuCategories,
                                     OnStartDragListener dragStartListener,
                                     OrderListener orderListener) {
        this.dragStartListener = dragStartListener;
        this.orders = menuCategories;
        this.orderListener = orderListener;
    }

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder viewHolder, int position) {
        Order order = orders.get(position);
        Dishes dish = order.getDishes();
        viewHolder.name.setText(dish.getName());
        viewHolder.description.setText(dish.getDescription());
        viewHolder.image.setImageURI(dish.getImage());
        viewHolder.itemView.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(viewHolder);
            }
            return false;
        });
        setItemCountAndPrice(viewHolder, order);
        viewHolder.increaseItemCount
                .setOnClickListener(view -> incrementCounter(viewHolder, order));
        viewHolder.decreaseItemCount
                .setOnClickListener(view -> decrementCounter(viewHolder, order));
    }

    private void setItemCountAndPrice(@NonNull OrderListViewHolder viewHolder, Order model) {
        int price = Integer.parseInt(model.getDishes().getPrice()) * model.getCount();
        String priceStr = viewHolder.itemView.getContext().getString(R.string.Rs) + price;
        viewHolder.numberOfItems.setText(MessageFormat.format("{0}", model.getCount()));
        viewHolder.totalPrice.setText(priceStr);
    }

    private void decrementCounter(OrderListViewHolder viewHolder, Order model) {
        int count = orderListener.onDishCountDecremented(model.getDishes());
        if (count > 0) {
            setItemCountAndPrice(viewHolder, model, count);
        } else {
            int position = viewHolder.getAdapterPosition();
            orders.remove(position);
            orderListener.onDishRemoved(model.getDishes());
            notifyItemRemoved(position);
        }
    }

    private void incrementCounter(OrderListViewHolder viewHolder, Order model) {
        setItemCountAndPrice(viewHolder, model,
                orderListener.onDishCountIncremented(model.getDishes()));
    }

    private void setItemCountAndPrice(OrderListViewHolder viewHolder, Order model, int count) {
        int position = viewHolder.getAdapterPosition();
        model.setCount(count);
        orders.set(position, model);
        notifyItemChanged(position);
        setItemCountAndPrice(viewHolder, model);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        orders.get(fromPosition).setOrder(toPosition);
        orders.get(toPosition).setOrder(fromPosition);
        Collections.swap(orders, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        orders.remove(position);
        notifyItemRemoved(position);
    }

}
