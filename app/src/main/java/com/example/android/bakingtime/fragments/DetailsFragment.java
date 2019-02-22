package com.example.android.bakingtime.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.utils.PaneUtils;
import com.example.android.bakingtime.adapters.IngredientsAdapter;
import com.example.android.bakingtime.adapters.StepsAdapter;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.viewModel.DetailsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsFragment extends Fragment implements StepsAdapter.ItemClickListener {

  private DetailsViewModel viewDetailsViewModel;
  private Recipe recipe;
  private int stepId;

  @BindView(R.id.recipe_name_textview)
  TextView recipeNameTextView;

  @BindView(R.id.ingredients_recycler_view)
  RecyclerView ingredientsRecyclerView;

  @BindView(R.id.steps_recycler_view)
  RecyclerView stepsRecyclerView;

  public DetailsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Intent intent = getActivity().getIntent();
    recipe =
        intent.getParcelableExtra(getActivity().getResources().getString(R.string.key_to_recipe));
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewDetailsViewModel = ViewModelProviders.of(getActivity()).get(DetailsViewModel.class);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_details, container, false);
    ButterKnife.bind(this, view);

    recipeNameTextView.setText(recipe.getName());

    ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(getActivity());
    ingredientsAdapter.setIngredientsList(recipe.getIngredients());
    ingredientsRecyclerView.setAdapter(ingredientsAdapter);

    stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    StepsAdapter stepsAdapter = new StepsAdapter(getActivity(), this);
    stepsAdapter.setStepsList(recipe.getSteps());
    stepsRecyclerView.setAdapter(stepsAdapter);

    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (PaneUtils.hasTwoPanes(getContext())) {
      if (savedInstanceState != null) {
        stepId = savedInstanceState.getInt("stepId");
      }
      viewDetailsViewModel.select(stepId);
    }
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("stepId", stepId);
  }

  @Override
  public void onItemClickListener(int stepId) {
    viewDetailsViewModel.select(stepId);
    this.stepId = stepId;
  }
}
