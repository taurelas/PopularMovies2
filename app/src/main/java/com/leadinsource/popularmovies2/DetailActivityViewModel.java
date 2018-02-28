package com.leadinsource.popularmovies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * ViewModel for Detail Activity
 */

class DetailActivityViewModel extends ViewModel {


    private MutableLiveData<Boolean> isFavorite;

    DetailActivityViewModel() {
        isFavorite = new MutableLiveData<>();
        //TODO change to observe data from the db
        isFavorite.setValue(false);


    }


    LiveData<Boolean> isFavorite() {
        return isFavorite;
    }

    void switchFavorite() {
        isFavorite.setValue(!isFavorite.getValue());
    }
}
