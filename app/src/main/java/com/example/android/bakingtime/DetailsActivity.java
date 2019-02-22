package com.example.android.bakingtime;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.utils.PaneUtils;
import com.example.android.bakingtime.viewModel.DetailsViewModel;

public class DetailsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    DetailsViewModel viewDetailsViewModel =
        ViewModelProviders.of(this).get(DetailsViewModel.class);
    viewDetailsViewModel
        .getSelectedStepId()
        .observe(
            this,
            new Observer<Integer>() {
              @Override
              public void onChanged(@Nullable Integer stepId) {
                if (!PaneUtils.hasTwoPanes(DetailsActivity.this)) {
                  viewStepDetails(stepId);
                }
              }
            });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  private void viewStepDetails(int stepId) {
    Recipe recipe = getIntent().getParcelableExtra(getString(R.string.key_to_recipe));

    Intent intent = new Intent(DetailsActivity.this, StepDetailsActivity.class);
    intent.putExtra(getString(R.string.key_to_recipe), recipe);
    intent.putExtra(getString(R.string.key_to_step_id), stepId);

    startActivity(intent);
  }
}
