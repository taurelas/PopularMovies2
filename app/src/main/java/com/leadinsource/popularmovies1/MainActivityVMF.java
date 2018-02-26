package com.leadinsource.popularmovies1;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.res.Resources;
import android.support.annotation.NonNull;

/**
 * Factory for ViewModel with injected Resources reference.
 */

public class MainActivityVMF implements ViewModelProvider.Factory {

    private final Resources resources;

    MainActivityVMF(Resources resources) {
        this.resources = resources;
    }

    /**
     * Required method by the interface that provides a non-default constructor
     *
     * Method based on a tutorial
     * https://proandroiddev.com/architecture-components-modelview-livedata-33d20bdcc4e9
     *
     * SimpleViewModel/app/src/main/java/com/shvartsy/archcomponents/viewmodel/LoggingClickCounterViewModelFactory.java
     *
     * @param modelClass a class whose instance is requested
     * @param <T>        The type parameter for the ViewModel.
     * @return instance of a new ViewModel object
     */

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(resources);
        }

        throw new IllegalArgumentException("Unknown view model class");
    }
}
