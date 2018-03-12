package com.leadinsource.popularmovies2.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.leadinsource.popularmovies2.model.Movie;
import com.leadinsource.popularmovies2.model.Review;
import com.leadinsource.popularmovies2.model.Video;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Matt on 08/03/2018.
 */

public class Cache {

    private List<Movie> cachedList;
    private MutableLiveData<List<Movie>> cachedMovies;
    private MutableLiveData<List<Movie>> cachedPopularMovies;
    private MutableLiveData<List<Movie>> cachedTopRatedMovies;
    private HashMap<Integer, Movie> movieCacheMap;
    private HashMap<Integer, List<Video>> trailersCacheMap;
    private HashMap<Integer, List<Review>> reviewsCacheMap;

    private MutableLiveData<Movie> movieCached;

    private MutableLiveData<List<Video>> trailersCached;

    private MutableLiveData<List<Review>> reviewsCached;
    private MutableLiveData<Integer> currentMovieId;

    void put(LiveData<List<Movie>> listToStore) {
        cachedList = listToStore.getValue();
    }

    void put(Movie movie) {
        if (movieCached == null) {
            movieCached = new MutableLiveData<>();
        }
        if (movieCacheMap == null) {
            movieCacheMap = new HashMap<>();
        }
        movieCacheMap.put(movie.id, movie);

        movieCached.postValue(movie);

    }

    LiveData<List<Movie>> getCachedMovies() {
        return cachedMovies;
    }

    LiveData<List<Movie>> getCachedPopularMovies() {
        return cachedPopularMovies;
    }

    LiveData<List<Movie>> getCachedTopRatedMovies() {
        return cachedTopRatedMovies;
    }


    LiveData<Movie> getCachedMovie() {
        return movieCached;
    }

    void put(List<Movie> movies) {
        cachedList = movies;
        if (cachedMovies == null) {
            cachedMovies = new MutableLiveData<>();
        }
        cachedMovies.postValue(movies);

        putToMap(movies);
    }

    void putPopular(List<Movie> movies) {
        if (cachedPopularMovies == null) {
            cachedPopularMovies = new MutableLiveData<>();
        }
        cachedPopularMovies.postValue(movies);
        putToMap(movies);
    }

    void putTopRated(List<Movie> movies) {
        if (cachedTopRatedMovies == null) {
            cachedTopRatedMovies = new MutableLiveData<>();
        }
        cachedTopRatedMovies.postValue(movies);
        putToMap(movies);
    }


    private void putToMap(List<Movie> movies) {
        if (movieCacheMap == null) {
            movieCacheMap = new HashMap<>();
        }

        for (Movie movie : movies) {
            movieCacheMap.put(movie.id, movie);
        }
    }

    public Movie get(Integer key) {
        return movieCacheMap.get(key);
    }

    public void putTrailers(List<Video> trailers) {

        if (trailersCacheMap == null) {
            trailersCacheMap = new HashMap<>();
        }

        if (trailersCached == null) {
            trailersCached = new MutableLiveData<>();
        }

        trailersCacheMap.put(currentMovieId.getValue(), trailers);

        trailersCached.postValue(trailers);

    }


    public LiveData<List<Video>> getTrailers() {

        if (trailersCacheMap == null) {
            trailersCached = null;

        } else {
            if (trailersCacheMap.get(currentMovieId.getValue()) == null) {
                trailersCached = null;
            } else {
                if(trailersCached!=null) {
                    trailersCached.setValue(trailersCacheMap.get(currentMovieId.getValue()));
                }
            }
        }

        return trailersCached;
    }

    public void putReviews(List<Review> reviews) {
        if(reviewsCacheMap == null) {
            reviewsCacheMap = new HashMap<>();
        }

        if (reviewsCached == null) {
            reviewsCached = new MutableLiveData<>();
        }
        //adding reviews to the hashmap
        reviewsCacheMap.put(currentMovieId.getValue(), reviews);

        reviewsCached.postValue(reviews);
    }

    LiveData<List<Review>> getReviews() {
        if(reviewsCacheMap == null) {
            reviewsCached = null;
        } else {
            if(reviewsCacheMap.get(currentMovieId.getValue()) == null) {
                reviewsCached = null;
            } else {
                if(reviewsCached!=null) {
                    reviewsCached.setValue(reviewsCacheMap.get(currentMovieId.getValue()));
                }
            }
        }

        return reviewsCached;
    }

    public LiveData<Movie> getCachedMovie(Integer id) {

        put(get(id));

        return movieCached;
    }

    public void clearMovieDetails() {
        trailersCached = null;
        reviewsCached = null;
    }

    public void setMovieId(int movieId) {
        if (currentMovieId == null) {
            currentMovieId = new MutableLiveData<>();
        }

        currentMovieId.setValue(movieId);
    }
}
