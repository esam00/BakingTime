package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.models.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

  private final Context context;
  private ItemClickListener itemClickListener;
  private List<Step> stepsList;

  public void setStepsList(List<Step> stepsList) {
    this.stepsList = stepsList;
    notifyDataSetChanged();
  }

  public StepsAdapter(Context context, ItemClickListener listener) {
    this.context = context;
    this.itemClickListener = listener;
  }

  public interface ItemClickListener {
    void onItemClickListener(int stepId);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.step_list_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Step step = stepsList.get(position);
    holder.shortDescriptionTextView.setText(step.getShortDescription());
  }

  @Override
  public int getItemCount() {
    return stepsList == null ? 0 : stepsList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.short_description_text_view)
    TextView shortDescriptionTextView;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      itemClickListener.onItemClickListener(stepsList.get(getAdapterPosition()).getId());
    }
  }
}
