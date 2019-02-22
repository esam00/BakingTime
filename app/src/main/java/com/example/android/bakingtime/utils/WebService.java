package com.example.android.bakingtime.utils;

import com.example.android.bakingtime.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WebService {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipeList();
}
