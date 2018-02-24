package com.leadinsource.popularmovies1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matt on 24/02/2018.
 */

public class MovieRepository {

    LiveData<List<String>> fetchMovies() {

        MutableLiveData<List<String>> movies = new MutableLiveData<>();

        movies.setValue(new ArrayList<String>(Arrays.asList("a", "b", "c", "d","e", "f")));

        return movies;
    }
}
