package com.leadinsource.popularmovies1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Single source of truth for the app
 */

class MovieRepository {

    private static final String API_KEY = BuildConfig.API_KEY;


    LiveData<List<String>> fetchMovies() {

        MutableLiveData<List<String>> movies = new MutableLiveData<>();

        movies.setValue(new ArrayList<>(Arrays.asList("a", "b", "c", "d","e", "f")));

        return movies;
    }
}
