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

    private SortOrder sortOrder;
    private MovieRepository movieRepository;
    private Resources resources;

    MainActivityViewModel(Resources resources) {
        this.resources = resources;
        movieRepository = new MovieRepository();
        sortOrder = new SortOrder();
    }

    LiveData<String> getSortOrder() {
        return sortOrder.getCurrentText();
    }

    /**
     * Ferries data from MovieRepository, amending the url on-the-fly
     *
     * @return Observable LiveData with list of Movies from The Movie DB
     */
    LiveData<List<Movie>> getMoviesData() {

        return Transformations.switchMap(sortOrder.getOrder(), input -> {
            if (input == SortOrder.MOST_POPULAR) {
                return getPopularMovies();
            } else {
                return getTopRatedMovies();
            }

        });
    }

    /**
     * Get popular movies list from repository, amending the url on-the-fly
     * @return LiveData object that can be observed by MainActivity
     */
    private LiveData<List<Movie>> getPopularMovies() {
        return Transformations.map(movieRepository.fetchPopularMovies(),
                this::fixImageUrls);
    }

    /**
     * Get top rated movies list from repository, amending the url on-the-fly
     * @return LiveData object that can be observed by MainActivity
     */

    private LiveData<List<Movie>> getTopRatedMovies() {
        return Transformations.map(movieRepository.fetchTopRatedMovies(),
                this::fixImageUrls);
    }

    private static final String IMAGE_PATH = "http://image.tmdb.org/t/p/w185/";

    private List<Movie> fixImageUrls(List<Movie> movies) {
        for (Movie movie : movies) {
            movie.poster_path = IMAGE_PATH.concat(movie.poster_path);
        }

        return movies;
    }

    void switchSorting() {
        sortOrder.swap();
    }


    private class SortOrder {
        private static final int MOST_POPULAR = 1;
        private static final int HIGHEST_RATED = 2;

        private MutableLiveData<Integer> current;
        private MutableLiveData<String> currentText;
        private final String sortByRatingText;
        private final String sortByPopularityText;

        SortOrder() {
            current = new MutableLiveData<>();
            current.setValue(MOST_POPULAR);

            sortByRatingText = MainActivityViewModel.this.resources.getString(R.string.sort_by_rating);
            sortByPopularityText = MainActivityViewModel.this.resources.getString(R.string.sort_by_popularity);
            currentText = new MutableLiveData<>();
            currentText.setValue(sortByRatingText);
        }

        void swap() {
            if (current.getValue() == MOST_POPULAR) {
                current.setValue(HIGHEST_RATED);
            } else {
                current.setValue(MOST_POPULAR);
            }

            swapText();
        }

        private void swapText() {
            if (current.getValue() == MOST_POPULAR) {
                currentText.setValue(sortByRatingText);
            } else {
                currentText.setValue(sortByPopularityText);
            }
        }

        LiveData<String> getCurrentText() {
            return currentText;
        }

        LiveData<Integer> getOrder() {
            return current;
        }
    }
}
