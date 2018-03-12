package com.leadinsource.popularmovies2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.leadinsource.popularmovies2.model.Movie;
import com.leadinsource.popularmovies2.model.Review;
import com.leadinsource.popularmovies2.model.Video;
import com.leadinsource.popularmovies2.repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for Detail Activity
 */

public class DetailActivityViewModel extends AndroidViewModel {

    private static final String MOVIE_EXTRA = "movie_extra";
    public static final String TRAILER_EXTRA = "trailer_extra";
    private static final String REVIEWS_EXTRA = "reviews_extra";
    private static final String FAVORITE_EXTRA = "favorite_extra";
    private static final String TAG = DetailActivityViewModel.class.getSimpleName();
    private LiveData<Boolean> isFavorite;
    private MutableLiveData<Boolean> isFavoriteCache;
    private LiveData<List<Video>> trailers;
    private MutableLiveData<Integer> movieId;
    private LiveData<List<Review>> reviews;
    private MovieRepository movieRepository;
    private LiveData<Movie> movie;

    public DetailActivityViewModel(Application application) {
        super(application);

        if (movieId == null) {
            movieId = new MutableLiveData<>();
        }

        movieRepository = MovieRepository.getInstance(application.getContentResolver());

    }

    /**
     * Initializes view model and restores settings if anything of value in the bundle
     *
     * @param savedInstanceState
     */
    void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring state");

            movieRepository.setTrailers(savedInstanceState.getParcelableArrayList(TRAILER_EXTRA));
            movieRepository.setReviews(savedInstanceState.getParcelableArrayList(REVIEWS_EXTRA));
            movieRepository.setMovie(savedInstanceState.getParcelable(MOVIE_EXTRA));
            movieRepository.setFavorite(savedInstanceState.getBoolean(FAVORITE_EXTRA));

        }
    }

    LiveData<Movie> getMovie() {
        if (movie == null) {
            movie = Transformations.switchMap(movieId, input -> movieRepository.getCachedMovie(input));
        }

        return movie;
    }

    /**
     * When MovieId changes, it will check whether the movie is favorite or not
     *
     * @return boolean to be observed
     */
    LiveData<Boolean> isFavorite() {
        if (isFavorite == null) {
            isFavorite = Transformations.switchMap(movieId, input ->
                    movieRepository.isFavorite(input));
        }

        return isFavorite;
    }

    /**
     * Advises the repo to change data regarding the movie being favorite.
     */
    void switchFavorite() {

        if (isFavorite.getValue()) {
            movieRepository.removeFromFavorites(movieId.getValue());
        } else {
            movieRepository.addToFavorites(movieId.getValue());
        }

    }

    /**
     * Fixes Urls in trailers and make the trailers accessible to clients
     *
     * @return list of trailers wrapped in LiveData
     */
    LiveData<List<Video>> getTrailers() {
        if (trailers == null) {

            trailers = Transformations.switchMap(movieId,
                    input -> movieRepository.fetchTrailers(input));
        }

        return trailers;

    }

    void setMovieId(int movieId) {
        if (this.movieId == null) {
            this.movieId = new MutableLiveData<>();
        }

        this.movieId.postValue(movieId);
    }

    public LiveData<List<Review>> getReviews() {
        if (reviews == null) {
            reviews = Transformations.switchMap(movieId,
                    input -> movieRepository.fetchReviews(input));
        }

        return reviews;
    }

    public void saveState(Bundle outState) {
        outState.putParcelable(MOVIE_EXTRA, movie.getValue());
        outState.putParcelableArrayList(TRAILER_EXTRA, (ArrayList<Video>) trailers.getValue());
        outState.putParcelableArrayList(REVIEWS_EXTRA, (ArrayList<Review>) reviews.getValue());
        outState.putBoolean(FAVORITE_EXTRA, isFavorite.getValue());
    }
}
