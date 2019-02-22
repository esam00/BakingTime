package com.example.android.bakingtime.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable {

  private int id;

  public int getId() {
    return id;
  }

  public Recipe setId(int id) {
    this.id = id;
    return this;
  }

  private String name;

  public String getName() {
    return name;
  }

  public Recipe setName(String name) {
    this.name = name;
    return this;
  }

  private int servings;

  public int getServings() {
    return servings;
  }

  public void setServings(int servings) {
    this.servings = servings;
  }

  private String image;

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  private List<Ingredient> ingredients;

  private List<Step> steps;

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  public List<Step> getSteps() {
    return steps;
  }

  public void setSteps(List<Step> steps) {
    this.steps = steps;
  }

  public Recipe() {}

  private Recipe(Parcel in) {
    id = in.readInt();
    name = in.readString();
    servings = in.readInt();
    image = in.readString();
    ingredients = new ArrayList<>();
    in.readTypedList(ingredients, Ingredient.CREATOR);
    steps = new ArrayList<>();
    in.readTypedList(steps, Step.CREATOR);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeInt(id);
    out.writeString(name);
    out.writeInt(servings);
    out.writeString(image);
    out.writeTypedList(ingredients);
    out.writeTypedList(steps);
  }

  public static final Creator<Recipe> CREATOR =
      new Creator<Recipe>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Recipe createFromParcel(Parcel in) {
          return new Recipe(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Recipe[] newArray(int size) {
          return new Recipe[size];
        }
      };
}
