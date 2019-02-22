package com.example.android.bakingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.example.android.bakingtime.R;
import com.example.android.bakingtime.models.Ingredient;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.utils.WebService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class WidgetRemoteViewsService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
  }
}

class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  final Context context;
  int recipeId;
  List<Ingredient> ingredients;

  public WidgetRemoteViewsFactory(Context context, Intent intent) {
    this.context = context;
    recipeId = intent.getIntExtra("recipe", -1);
    Timber.d("Recipe Id for this widget is: %s", recipeId);
  }

  @Override
  public void onCreate() {}

  @Override
  public void onDataSetChanged() {
    if (recipeId != -1) {
      Retrofit retrofit =
          new Retrofit.Builder()
              .baseUrl("https://d17h27t6h515a5.cloudfront.net")
              .addConverterFactory(GsonConverterFactory.create())
              .build();

      WebService webService = retrofit.create(WebService.class);
      try {
        Response<List<Recipe>> response = webService.getRecipeList().execute();
        for (Recipe recipe : response.body()) {
          if (recipe.getId() == recipeId) {
            ingredients = recipe.getIngredients();
            Timber.d("%s ingredients loaded", ingredients.size());
          }
        }
      } catch (IOException ex) {
        Timber.d(ex, "failed to load recipes");
      }
    } else {
      ingredients = new ArrayList<>();
    }
  }

  @Override
  public void onDestroy() {}

  @Override
  public int getCount() {
    return ingredients == null ? 0 : ingredients.size();
  }

  @Override
  public RemoteViews getViewAt(int position) {
    if (position == AdapterView.INVALID_POSITION || ingredients == null) {
      return null;
    }

    Ingredient ingredient = ingredients.get(position);
    String s =
        String.format(
            "%s %s of %s",
            ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient());

    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.ingredient_layout_widget);
    rv.setTextViewText(R.id.quantity_textview, s);

    return rv;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }
}
