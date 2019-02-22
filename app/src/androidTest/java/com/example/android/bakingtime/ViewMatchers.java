package com.example.android.bakingtime;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

public class ViewMatchers {
    @SuppressWarnings("unchecked")
    public static Matcher<View> withRecyclerView(@IdRes int viewId) {
        return allOf(isAssignableFrom(RecyclerView.class), withId(viewId));
    }
}