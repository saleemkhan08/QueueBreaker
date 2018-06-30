package com.thnki.queuebreaker.restaurant.categories;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.restaurant.ItemTouchHelperAdapter;
import com.thnki.queuebreaker.restaurant.OnStartDragListener;

import java.util.Collections;
import java.util.List;

public class FirestoreMenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryViewHolder>
        implements ItemTouchHelperAdapter {

    private List<MenuCategory> menuCategories;
    private OnStartDragListener dragStartListener;

    public FirestoreMenuCategoryAdapter(List<MenuCategory> menuCategories, OnStartDragListener dragStartListener) {
        this.dragStartListener = dragStartListener;
        this.menuCategories = menuCategories;
    }

    @NonNull
    @Override
    public MenuCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_row, parent, false);
        return new MenuCategoryViewHolder(itemView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MenuCategoryViewHolder viewHolder, int position) {
        final MenuCategory model = menuCategories.get(position);
        viewHolder.category.setText(model.getName());
        viewHolder.edit.setOnClickListener(view -> editCategory(viewHolder, position));
        viewHolder.itemView.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(viewHolder);
            }
            return false;
        });
    }

    private void editCategory(MenuCategoryViewHolder viewHolder, int position) {
        showEditOptions(viewHolder, View.VISIBLE, View.GONE);
        viewHolder.editCategory.setText(menuCategories.get(position).getName());
        viewHolder.editCategory.requestFocus();
        viewHolder.saveEdit.setOnClickListener( view -> {
            menuCategories
                .get(position).setName(viewHolder.editCategory.getText().toString());
            showEditOptions(viewHolder, View.GONE, View.VISIBLE);
            notifyItemChanged(position);
        });
    }

    private void showEditOptions(MenuCategoryViewHolder viewHolder, int gone, int visible) {
        viewHolder.saveEdit.setVisibility(gone);
        viewHolder.edit.setVisibility(visible);

        viewHolder.editCategory.setVisibility(gone);
        viewHolder.category.setVisibility(visible);
    }

    @Override
    public int getItemCount() {
        return menuCategories.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        menuCategories.get(fromPosition).setOrder(toPosition);
        menuCategories.get(toPosition).setOrder(fromPosition);

        Collections.swap(menuCategories, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        menuCategories.remove(position);
        notifyItemRemoved(position);
    }

}
