package com.leadinsource.popularmovies2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.leadinsource.popularmovies2.model.Movie;
import com.leadinsource.popularmovies2.repository.MovieRepository;

import java.util.List;

/**
 * ViewModel for MainActivity
 */

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = MainActivityViewModel.class.getSimpleName();
    private static final String EXTRA_SORT_ORDER = "SORT_ORDER";
    private static final String EXTRA_LIST_TYPE = "LIST_TYPE";
    private SortOrder sortOrder;
    private ListType movieListType;
    private final MovieRepository movieRepository;
    private final Resources resources;
    private LiveData<List<Movie>> movies;

    public MainActivityViewModel(Application application) {
        super(application);
        this.resources = application.getResources();
        movieRepository = MovieRepository.getInstance(application.getContentResolver());


    }

    public void init(Bundle savedInstanceState) {

        if(savedInstanceState!=null) {
            sortOrder = new SortOrder(savedInstanceState.getInt(EXTRA_SORT_ORDER, SortOrder.MOST_POPULAR));
            movieListType = new ListType(savedInstanceState.getInt(EXTRA_LIST_TYPE, ListType.TOP_MOVIES));
        } else {
            sortOrder = new SortOrder(SortOrder.MOST_POPULAR);
            movieListType = new ListType(SortOrder.MOST_POPULAR);
        }
    }


    /**
     * Provides text to display in relation to Sort Order
     *
     * @return Observable String to display
     */
    LiveData<String> getSortOrderText() {
        return sortOrder.getCurrentText();
    }

    /**
     * This is a single access point for MainActivity for movie data, point where last Transformations
     * take place. At this stage it includes provision of Top Movies or Favorite Movies based
     * on the current user choice
     *
     * @return Observable LiveData with list of Movies either from local DB or The Movie DB
     */
    LiveData<List<Movie>> getMoviesData() {
        if (movies != null && movies.getValue() != null && movies.getValue().size() > 0) {
            return movies;
        }

        movies = Transformations.switchMap(movieListType.getCurrent(), input -> {
            if (input == ListType.TOP_MOVIES) {
                Log.d(TAG, "fetching TopMovies");
                return getTopMovies();
            } else {
                Log.d(TAG, "fetching Favorite Movies");
                return getFavoriteMovies();
            }
        });

        return movies;
    }

    /**
     * Provides either a list of most popular movies or best rated from The Movie DB
     *
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
     *
     * @return LiveData object with List of Movies
     */
    private LiveData<List<Movie>> getFavoriteMovies() {
        return Transformations.switchMap(sortOrder.getCurrent(), input -> {
            if (input == SortOrder.MOST_POPULAR) {
                return movieRepository.getPopularFavorites();
            } else {
                return movieRepository.getTopRatedFavorites();
            }
        });

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
            Log.d(TAG, "Fixing url "+ movie.posterPath);
            movie.posterPath = IMAGE_PATH.concat(movie.posterPath);
            Log.d(TAG, "Fixed url "+ movie.posterPath);
        }

        return movies;
    }

    /**
     * Used by Activity to notify that the sort has switched, the Activity doesn't know what sort
     * it displays
     */
    void switchSorting() {
        sortOrder.swap();
    }

    /**
     * Provides text to display in relation to currently displayed list
     *
     * @return LiveData String that can be observed by MainActivity
     */
    LiveData<String> getMovieListType() {
        return movieListType.getCurrentText();
    }

    /**
     * Used by Activity to notify that the movie list has switched, the Activity doesn't know what
     * list it displays
     */
    void switchMovieListType() {
        movieListType.swap();
    }

    void restoringState(Bundle inState) {
        //sortOrder.current.postValue(inState.getInt("SORT_ORDER"));
        //movieListType.current.postValue(inState.getInt("LIST_TYPE"));
        Log.d(TAG, "restored sort ");
    }

    void saveState(Bundle outState) {
        outState.putInt(EXTRA_SORT_ORDER, sortOrder.getCurrent().getValue());

        Log.d(TAG, "Saving sort order " + (sortOrder.getCurrent().getValue()==SortOrder.HIGHEST_RATED ?
                "highest rated" : "most popular"));

        outState.putInt("LIST_TYPE", movieListType.getCurrent().getValue());

    }

    public void clearState() {
        movieRepository.clearDiskCache();
    }

    /**
     * Handles all sort-related issues
     */
    private class SortOrder {
        static final int MOST_POPULAR = 1;
        private static final int HIGHEST_RATED = 2;

        private final MutableLiveData<Integer> current;
        private LiveData<String> currentText;
        private final String sortByRatingText;
        private final String sortByPopularityText;

        SortOrder(int sort) {
            current = new MutableLiveData<>();
            current.setValue(sort);

            sortByRatingText = MainActivityViewModel.this.resources.getString(R.string.sort_by_rating);
            sortByPopularityText = MainActivityViewModel.this.resources.getString(R.string.sort_by_popularity);


        }

        void swap() {
            if (current.getValue() == MOST_POPULAR) {
                current.setValue(HIGHEST_RATED);
            } else {
                current.setValue(MOST_POPULAR);
            }

        }

        LiveData<String> getCurrentText() {
            if(currentText==null) {
                currentText = Transformations.map(current, input -> {
                    if(input==MOST_POPULAR) {
                        return sortByRatingText;
                    } else {
                        return sortByPopularityText;
                    }
                });
            }

            return currentText;
        }

        LiveData<Integer> getCurrent() {
            return current;
        }
    }

    /**
     * Handles movie list info and behaviour
     */
    private class ListType {
        static final int TOP_MOVIES = 1;
        static final int FAVORITE_MOVIES = 2;

        private final MutableLiveData<Integer> current;
        private LiveData<String> currentText;
        private final String showTopMoviesText;
        private final String showFavoriteMoviesText;

        ListType(int type) {
            current = new MutableLiveData<>();
            current.setValue(type);

            showTopMoviesText = resources.getString(R.string.show_top_movies);
            showFavoriteMoviesText = resources.getString(R.string.show_favorites_movies);
        }

        void swap() {
            if (current.getValue() == TOP_MOVIES) {
                current.setValue(FAVORITE_MOVIES);
            } else {
                current.setValue(TOP_MOVIES);
            }
                }

        LiveData<String> getCurrentText() {
            if(currentText==null) {
                currentText = Transformations.map(current, input -> {
                    if(input==TOP_MOVIES) {
                        return showFavoriteMoviesText;
                    } else {
                        return showTopMoviesText;
                    }
                });
            }


            return currentText;
        }

        LiveData<Integer> getCurrent() {
            return current;
        }

    }
}
