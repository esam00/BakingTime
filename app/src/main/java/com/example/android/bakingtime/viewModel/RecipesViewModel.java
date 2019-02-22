package com.example.android.bakingtime.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.bakingtime.SimpleIdlingResource;
import com.example.android.bakingtime.utils.WebService;
import com.example.android.bakingtime.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RecipesViewModel extends AndroidViewModel {
  private final WebService webService;

  public RecipesViewModel(@NonNull Application application) {
    super(application);

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl("https://d17h27t6h515a5.cloudfront.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    webService = retrofit.create(WebService.class);
  }

  public LiveData<List<Recipe>> getRecipeList(@Nullable final SimpleIdlingResource idlingResource) {
    final MutableLiveData<List<Recipe>> data = new MutableLiveData<>();

    if (idlingResource != null) {
        idlingResource.setIdleState(false);
    }
    webService
        .getRecipeList()
        .enqueue(
            new Callback<List<Recipe>>() {
              @Override
              public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                data.setValue(response.body());
                  if (idlingResource != null) {
                      idlingResource.setIdleState(true);
                  }
              }

              @Override
              public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Timber.d(t, "Failed to get recipes.");
                  if (idlingResource != null) {
                      idlingResource.setIdleState(true);
                  }
              }
            });
    return data;
  }
}
