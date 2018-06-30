package com.thnki.queuebreaker.restaurant.categories;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thnki.queuebreaker.R;

class MenuCategoryViewHolder extends RecyclerView.ViewHolder {
    TextView category;
    ImageView edit;
    EditText editCategory;
    ImageView saveEdit;

    MenuCategoryViewHolder(View itemView) {
        super(itemView);
        category = itemView.findViewById(R.id.category);
        edit = itemView.findViewById(R.id.edit);
        editCategory = itemView.findViewById(R.id.editCategory);
        saveEdit = itemView.findViewById(R.id.saveEdit);
    }

}