package com.example.android.bakingtime.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient
    implements Parcelable {

    private double quantity;
    public double getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private String measure;
    public String getMeasure() { return measure; }
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    private String ingredient;
    public String getIngredient() { return ingredient; }
    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public Ingredient() {}

    private Ingredient(Parcel in) {
        quantity = in.readDouble();
        measure = in.readString();
        ingredient = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
