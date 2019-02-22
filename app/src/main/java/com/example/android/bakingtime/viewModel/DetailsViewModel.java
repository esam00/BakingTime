package com.example.android.bakingtime.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

public class DetailsViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> selectedStepId = new MutableLiveData<>();

    public void select(int stepId) {
        selectedStepId.setValue(stepId);
    }

    public LiveData<Integer> getSelectedStepId() {
        return selectedStepId;
    }

    public DetailsViewModel(@NonNull Application application) {
        super(application);
    }
}
