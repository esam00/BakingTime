package com.example.android.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.RecipesActivity;

/**
 * Implementation of App Widget functionality.
 */
public class BakingTimeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int recipeId, String recipeName) {

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget);
        rv.setTextViewText(R.id.recipe_name_text_view, recipeName);

        Intent intentToRemoteViewsService = new Intent(context, WidgetRemoteViewsService.class);
        intentToRemoteViewsService.putExtra("recipe", recipeId);
        rv.setRemoteAdapter(R.id.ingredients_list_view, intentToRemoteViewsService);
        rv.setEmptyView(R.id.ingredients_list_view, R.id.empty_view);

        Intent intent = new Intent(context, RecipesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        rv.setOnClickPendingIntent(R.id.baking_time_widget_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rv);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetService.startActionUpdateWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            int recipeId = ConfigurationActivity.loadRecipeIdFromPref(context, appWidgetId);
            String recipeName = ConfigurationActivity.loadRecipeNameFromPref(context, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeId, recipeName);
        }
    }
}

