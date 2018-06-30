package com.thnki.queuebreaker.home.explore;


import android.os.Parcel;
import android.os.Parcelable;

import com.thnki.queuebreaker.home.FirestoreDocumentModel;

public class Restaurants extends FirestoreDocumentModel implements Parcelable {
    String address;
    String name;
    float rating;
    String icon;

    public Restaurants() {
    }

    protected Restaurants(Parcel in) {
        id = in.readString();
        address = in.readString();
        name = in.readString();
        rating = in.readFloat();
        icon = in.readString();
    }

    public static final Creator<Restaurants> CREATOR = new Creator<Restaurants>() {
        @Override
        public Restaurants createFromParcel(Parcel in) {
            return new Restaurants(in);
        }

        @Override
        public Restaurants[] newArray(int size) {
            return new Restaurants[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(address);
        dest.writeString(name);
        dest.writeFloat(rating);
        dest.writeString(icon);
    }
}
