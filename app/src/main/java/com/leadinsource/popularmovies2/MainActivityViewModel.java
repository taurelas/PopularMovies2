package com.leadinsource.popularmovies2;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.res.Resources;

import com.leadinsource.popularmovies2.model.Movie;
import com.leadinsource.popularmovies2.repository.MovieRepository;

import java.util.List;

/**
 * ViewModel for MainActivity
 */

class MainActivityViewModel extends ViewModel {

    private final SortOrder sortOrder;
    private final ListType movieListType;
    private final MovieRepository movieRepository;
    private final Resources resources;

    MainActivityViewModel(Resources resources) {
        this.resources = resources;
        movieRepository = new MovieRepository();
        sortOrder = new SortOrder();
        movieListType = new ListType();
    }

    LiveData<String> getSortOrder() {
        return sortOrder.getCurrentText();
    }

    /**
     * This is a single access point for MainActivity, point where last Transformations
     * take place. At this stage it includes provision of Top Movies or Favorite Movies based
     * on the current user choice
     *
     * @return Observable LiveData with list of Movies either from local DB or The Movie DB
     */
    LiveData<List<Movie>> getMoviesData() {
        return Transformations.switchMap(movieListType.getCurrent(), input -> {
            if (input == ListType.TOP_MOVIES) {
                return getTopMovies();
            } else {
                return getFavoriteMovies();
            }
        });
    }

    /**
     * Provides either a list of most popular movies or best rated from The Movie DB
     * @return LiveData object with List of Movies
     */
    private LiveData<List<Movie>> getTopMovies() {
        return Transformations.switchMap(sortOrder.getCurrent(), input -> {
            if (input == SortOrder.MOST_POPULAR) {
                return getPopularMovies();
            } else {
                return getTopRatedMovies();
            }

        });
    }

    /**
     * Provides list of favorite movies from the db
     * TODO implement with actual transformation and getting stuff from DB
     * @return LiveData object with List of Movies
     */
    private LiveData<List<Movie>> getFavoriteMovies() {
        return getPopularMovies();
    }

    /**
     * Get popular movies list from repository, amending the url in the process
     *
     * @return LiveData object to be transformed accordingly before passing to MainActivity
     */
    private LiveData<List<Movie>> getPopularMovies() {
        return Transformations.map(movieRepository.fetchPopularMovies(),
                this::fixImageUrls);
    }

    /**
     * Get top rated movies list from repository, amending the url in the process
     *
     * @return LiveData object that can be observed by MainActivity
     */

    private LiveData<List<Movie>> getTopRatedMovies() {
        return Transformations.map(movieRepository.fetchTopRatedMovies(),
                this::fixImageUrls);
    }

    private static final String IMAGE_PATH = "http://image.tmdb.org/t/p/w185/";

    /**
     * Completing image urls in data received from the API
     *
     * @param movies list of movies obtained from the API
     * @return list of movies with amended URLs
     */
    private List<Movie> fixImageUrls(List<Movie> movies) {
        for (Movie movie : movies) {
            movie.posterPath = IMAGE_PATH.concat(movie.posterPath);
        }

        return movies;
    }

    void switchSorting() {
        sortOrder.swap();
    }

    LiveData<String> getMovieListType() {
        return movieListType.getCurrentText();
    }

    void switchMovieListType() {
        movieListType.swap();
    }


    private class SortOrder {
        private static final int MOST_POPULAR = 1;
        private static final int HIGHEST_RATED = 2;

        private final MutableLiveData<Integer> current;
        private final MutableLiveData<String> currentText;
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

        LiveData<Integer> getCurrent() {
            return current;
        }
    }

    private class ListType {
        static final int TOP_MOVIES = 1;
        static final int FAVORITE_MOVIES = 2;

        private final MutableLiveData<Integer> current;
        private final MutableLiveData<String> currentText;
        private final String showTopMoviesText;
        private final String showFavoriteMoviesText;

        ListType() {
            current = new MutableLiveData<>();
            current.setValue(TOP_MOVIES);

            showTopMoviesText = resources.getString(R.string.show_top_movies);
            showFavoriteMoviesText = resources.getString(R.string.show_favorites_movies);

            currentText = new MutableLiveData<>();
            currentText.setValue(showFavoriteMoviesText);
        }

        void swap() {
            if (current.getValue() == TOP_MOVIES) {
                current.setValue(FAVORITE_MOVIES);
            } else {
                current.setValue(TOP_MOVIES);
            }

            swapText();
        }

        private void swapText() {
            if (current.getValue() == TOP_MOVIES) {
                currentText.setValue(showFavoriteMoviesText);
            } else {
                currentText.setValue(showTopMoviesText);
            }
        }

        LiveData<String> getCurrentText() {
            return currentText;
        }

        LiveData<Integer> getCurrent() {
            return current;
        }

    }
}
