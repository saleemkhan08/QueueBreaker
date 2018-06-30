package com.thnki.queuebreaker.restaurant.categories;

import android.os.Parcel;
import android.os.Parcelable;

import com.thnki.queuebreaker.home.FirestoreDocumentModel;

import org.jetbrains.annotations.NotNull;

public class MenuCategory extends FirestoreDocumentModel implements Parcelable {
    private String name;

    public MenuCategory() {
    }

    public MenuCategory(@NotNull String name, int order) {
        this.name = name;
        setOrder(order);
    }

    protected MenuCategory(Parcel in) {
        name = in.readString();
    }

    public static final Creator<MenuCategory> CREATOR = new Creator<MenuCategory>() {
        @Override
        public MenuCategory createFromParcel(Parcel in) {
            return new MenuCategory(in);
        }

        @Override
        public MenuCategory[] newArray(int size) {
            return new MenuCategory[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MenuCategory && ((MenuCategory) obj).getId().equals(id);
    }
}
