package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.models.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

  private final ItemClickListener itemClickListener;

  private final Context context;
  private List<Recipe> recipeList;

  public void setRecipeList(List<Recipe> recipeList) {
    this.recipeList = recipeList;
    notifyDataSetChanged();
  }

  public RecipeListAdapter(@NonNull Context context, ItemClickListener itemClickListener) {
    this.context = context;
    this.itemClickListener = itemClickListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Recipe item = recipeList.get(position);
    holder.nameTextView.setText(item.getName());
  }

  @Override
  public int getItemCount() {
    return recipeList == null ? 0 : recipeList.size();
  }

  public interface ItemClickListener {
    void onItemClickListener(Recipe recipe);
  }

  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.name_textview)
    TextView nameTextView;

    /**
     * Constructor for the TaskViewHolders.
     *
     * @param itemView The view inflated in onCreateViewHolder
     */
    public ViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      itemClickListener.onItemClickListener(recipeList.get(getAdapterPosition()));
    }
  }
}
