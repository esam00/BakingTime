package com.example.android.bakingtime.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.RecipeListAdapter;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.viewModel.RecipesViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfigurationActivity extends AppCompatActivity
    implements RecipeListAdapter.ItemClickListener {

  @BindView(R.id.recipes_recycler_view)
  @Nullable
  RecyclerView recipesRecyclerView;

  private RecipeListAdapter recipeListAdapter;

  private int widgetId;
  private static final String PREFS_NAME = "com.example.android.bakingtime.widget.BakingTimeWidgetProvider";
  private static final String PREF_PREFIX_KEY_ID = "recipe_widget_id_";
  private static final String PREF_PREFIX_KEY_NAME = "recipe_widget_name_";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_configuration);
    setResult(RESULT_CANCELED);

    ButterKnife.bind(this);

    recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    recipeListAdapter = new RecipeListAdapter(this, this);
    recipesRecyclerView.setAdapter(recipeListAdapter);

    RecipesViewModel viewModel = ViewModelProviders.of(this).get(RecipesViewModel.class);
    viewModel
        .getRecipeList(null)
        .observe(
            this,
            new Observer<List<Recipe>>() {
              @Override
              public void onChanged(@Nullable List<Recipe> recipes) {
                recipeListAdapter.setRecipeList(recipes);
              }
            });

    // Find the widget id from the intent.
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      widgetId = extras.getInt(
              AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    // If they gave us an intent without the widget id, just bail.
    if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
    }
  }

  @Override
  public void onItemClickListener(Recipe recipe) {
      showAppWidget(recipe);
  }

  private void showAppWidget(Recipe recipe) {
    saveRecipeIdToPref(this, widgetId, recipe);

    // TO DO, Perform the configuration and get an instance of the AppWidgetManager//
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    BakingTimeWidgetProvider.updateAppWidget(this, appWidgetManager, widgetId, recipe.getId(), recipe.getName());

    // Create the return intent//
    Intent resultValue = new Intent();
    // Pass the original appWidgetId//
    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
    // Set the results from the configuration Activity//
    setResult(RESULT_OK, resultValue);
    // Finish the Activity//
    finish();
  }

  // Write the prefix to the SharedPreferences object for this widget
  static void saveRecipeIdToPref(Context context, int appWidgetId, Recipe recipe) {
    SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
    prefs.putInt(PREF_PREFIX_KEY_ID + appWidgetId, recipe.getId());
    prefs.putString(PREF_PREFIX_KEY_NAME + appWidgetId, recipe.getName());
    prefs.apply();
  }
  // Read the prefix from the SharedPreferences object for this widget.
  // If there is no preference saved, get the default from a resource
  static int loadRecipeIdFromPref(Context context, int appWidgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    return prefs.getInt(PREF_PREFIX_KEY_ID + appWidgetId, -1);
  }

  static String loadRecipeNameFromPref(Context context, int appWidgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    return prefs.getString(PREF_PREFIX_KEY_NAME + appWidgetId, "");
  }
}
