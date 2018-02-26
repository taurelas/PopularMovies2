package com.leadinsource.popularmovies1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.res.Resources;
import android.util.Log;

import com.leadinsource.popularmovies1.model.Movie;
import com.leadinsource.popularmovies1.repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for MainActivity
 */

class MainActivityViewModel extends ViewModel {

    private static final int MOST_POPULAR = 1;
    private static final int HIGHEST_RATED = 2;
    private int sortOrder = MOST_POPULAR;
    private MutableLiveData<String> sortOrderString;
    private MovieRepository movieRepository;
    private Resources resources;

    private LiveData<List<String>> imageUrls;

    MainActivityViewModel(Resources resources) {
        this.resources = resources;
        movieRepository = new MovieRepository();
    }

    LiveData<String> getSortOrder() {
        if (sortOrderString == null) {
            sortOrderString = new MutableLiveData<>();
        }
        updateSortOrderString();

        return sortOrderString;
    }

    private void updateSortOrderString() {
        if (sortOrder == MOST_POPULAR) {
            sortOrderString.postValue(resources.getString(R.string.sort_by_popularity));
        } else {
            sortOrderString.postValue(resources.getString(R.string.sort_by_rating));
        }
    }

     LiveData<List<String>> getImageUrls() {
        if (imageUrls == null) {
            //imageUrls = new MutableLiveData<>();
            imageUrls = Transformations.map(movieRepository.fetchPopularMovies(),
                    input -> {
                        ArrayList<String> result = new ArrayList<>();

                        for (Movie movie : input) {
                            String url = "http://image.tmdb.org/t/p/w185/" + movie.poster_path;
                            result.add(url);
                            Log.d("VM", url);
                        }

                        return result;
                    });
        }


        return imageUrls;
    }

    void switchSorting() {
        sortOrder = (sortOrder == MOST_POPULAR) ? HIGHEST_RATED : MOST_POPULAR;
        updateSortOrderString();
    }

}
