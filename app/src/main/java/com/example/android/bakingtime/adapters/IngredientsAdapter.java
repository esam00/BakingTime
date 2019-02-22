package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.models.Ingredient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

  private final Context context;
  private List<Ingredient> ingredientsList;

  public void setIngredientsList(List<Ingredient> ingredientsList) {
    this.ingredientsList = ingredientsList;
    notifyDataSetChanged();
  }

  public IngredientsAdapter(Context context) {
    this.context = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.ingredient_layout, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Ingredient ingredient = ingredientsList.get(position);

    holder.quantityTextView.setText(String.valueOf(ingredient.getQuantity()));
    holder.measureTextView.setText(ingredient.getMeasure());
    holder.ingredientTextView.setText(ingredient.getIngredient());
  }

  @Override
  public int getItemCount() {
    return ingredientsList == null ? 0 : ingredientsList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.quantity_textview)
    public
    TextView quantityTextView;

    @BindView(R.id.measure_textview)
    public
    TextView measureTextView;

    @BindView(R.id.ingredient_textview)
    public
    TextView ingredientTextView;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
