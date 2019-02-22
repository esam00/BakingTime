package com.example.android.bakingtime;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewRecipesActivityTest {

  @Rule
  public ActivityTestRule<RecipesActivity> mActivityTestRule =
      new ActivityTestRule<>(RecipesActivity.class);

  private IdlingResource mIdlingResource;

  @Before
  public void registerIdlingResource() {
    mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
    // To prove that the test fails, omit this call:
    Espresso.registerIdlingResources(mIdlingResource);
  }

  @Test
  public void navigatesToViewDetailsActivityWhenClicked() {
    onView(withId(R.id.recipes_recycler_view))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    onView(withId(R.id.recipe_name_textview)).check(matches(withText("Nutella Pie")));
  }

  @After
  public void unregisterIdlingResource() {
    if (mIdlingResource != null) {
      Espresso.unregisterIdlingResources(mIdlingResource);
    }
  }
}
