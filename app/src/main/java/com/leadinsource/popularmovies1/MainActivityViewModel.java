package com.leadinsource.popularmovies1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.res.Resources;

import com.leadinsource.popularmovies1.model.Movie;
import com.leadinsource.popularmovies1.repository.MovieRepository;

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

    private LiveData<List<Movie>> movies;

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

    private static final String IMAGE_PATH = "http://image.tmdb.org/t/p/w185/";
    /**
     * Ferries data from MovieRepository, amending the url on-the-fly
     *
     * @return Observable LiveData with list of Movies from The Movie DB
     */
     LiveData<List<Movie>> getMovies() {
        if (movies == null) {
            movies = Transformations.map(movieRepository.fetchPopularMovies(),
                    input -> {
                        for (Movie movie : input) {
                            movie.poster_path = IMAGE_PATH.concat(movie.poster_path);

                        }

                        return input;
                    });
        }

        return movies;
    }

    /**
     * Switching current sorting state and triggers LiveData update accordingly
     */
    void switchSorting() {
        sortOrder = (sortOrder == MOST_POPULAR) ? HIGHEST_RATED : MOST_POPULAR;
        updateSortOrderString();
    }
}
