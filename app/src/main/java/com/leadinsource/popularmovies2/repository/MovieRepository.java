package com.leadinsource.popularmovies2.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.leadinsource.popularmovies2.BuildConfig;
import com.leadinsource.popularmovies2.db.DataContract;
import com.leadinsource.popularmovies2.model.Movie;
import com.leadinsource.popularmovies2.model.Review;
import com.leadinsource.popularmovies2.model.Video;
import com.leadinsource.popularmovies2.net.MovieResponse;
import com.leadinsource.popularmovies2.net.MoviesWebService;
import com.leadinsource.popularmovies2.net.ReviewResponse;
import com.leadinsource.popularmovies2.net.VideoResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Single source of truth for the app
 * TODO optimize code
 */

public class MovieRepository {

    private static MovieRepository INSTANCE;

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String BASE_URL = "https://api.themoviedb.org";
    private static final String TAG = "Repository";
    private ContentResolver contentResolver;

    private MutableLiveData<List<Movie>> movies;
    private MutableLiveData<List<Movie>> favoriteMovies;
    private MutableLiveData<Boolean> isFavorite;
    private HashMap<Integer, Movie> movieCache;
    private MutableLiveData<List<Video>> trailers;
    private MutableLiveData<List<Review>> reviews;

    private MovieRepository(ContentResolver contentResolver)  {
        this.contentResolver = contentResolver;
    }

    public static MovieRepository getInstance(ContentResolver contentResolver) {
        if(INSTANCE==null) {
            INSTANCE = new MovieRepository(contentResolver);
        }

        return INSTANCE;
    }

    public LiveData<List<Movie>> fetchPopularMovies() {

        if(movies == null) {
            movies = new MutableLiveData<>();
        }

        Call<MovieResponse> call = getMoviesWebService().listPopularMovies(API_KEY);


        return enqueueMovies(call);
    }

    public LiveData<List<Movie>> fetchTopRatedMovies() {
        if(movies == null) {
            movies = new MutableLiveData<>();
        }

        Call<MovieResponse> call = getMoviesWebService().listTopRatedMovies(API_KEY);

        //async request
        return enqueueMovies(call);

    }

    public LiveData<List<Video>> fetchTrailers(int movieId) {
        if(trailers==null) {
            trailers = new MutableLiveData<>();
        }

        Call<VideoResponse> call = getMoviesWebService().listVideos(movieId, API_KEY);

        return enqueueVideos(call);
    }

    public LiveData<List<Review>> fetchReviews(int movieId) {
        if(reviews==null) {
            reviews = new MutableLiveData<>();
        }

        Call<ReviewResponse> call = getMoviesWebService().listReviews(movieId, API_KEY);

        return enqueueReviews(call);
    }

    private LiveData<List<Review>> enqueueReviews(Call<ReviewResponse> call) {
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                //noinspection HardCodedStringLiteral
                Log.d(TAG, "Response status code: "+ response.code());
                if(!response.isSuccessful()) {

                    try {
                        Log.d(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }
                ReviewResponse decodedResponse = response.body();
                if(decodedResponse==null) return;

                Log.d(TAG, "Successful response!");
                reviews.postValue(decodedResponse.results);
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure");
                Log.d(TAG, t.getMessage());
            }
        });

        return reviews;
    }

    private LiveData<List<Video>> enqueueVideos(Call<VideoResponse> call) {
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                //noinspection HardCodedStringLiteral
                Log.d(TAG, "Response status code: "+ response.code());
                if(!response.isSuccessful()) {

                    try {
                        Log.d(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }

                VideoResponse decodedResponse = response.body();
                if(decodedResponse==null) return;

                Log.d(TAG, "Successful response!");

                trailers.postValue(decodedResponse.results);

            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure");
                Log.d(TAG, t.getMessage());
            }
        });

        return trailers;
    }



    private LiveData<List<Movie>> enqueueMovies(Call<MovieResponse> call) {
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                //noinspection HardCodedStringLiteral
                Log.d(TAG, "Response status code: "+ response.code());

                if(!response.isSuccessful()) {

                    try {
                        Log.d(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }

                MovieResponse decodedResponse = response.body();
                if(decodedResponse==null) return;

                Log.d(TAG, "Successful response!");

                cacheMovies(decodedResponse.results);


                movies.postValue(decodedResponse.results);



            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure");
                Log.d(TAG, t.getMessage());
            }
        });

        return movies;
    }

    private void cacheMovies(List<Movie> results) {
        movieCache = new HashMap<>();

        for(Movie movie : results) {
            movieCache.put(movie.id, movie);
        }
    }

    private MoviesWebService getMoviesWebService() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MoviesWebService.class);
    }

    public void addToFavorites(int movieId) {

        if (movieCache!=null) {
            Uri uri = ContentUris.withAppendedId(DataContract.MoviesEntry.CONTENT_URI, movieId);
            Movie movie = movieCache.get(movieId);


            ContentValues contentValues = new ContentValues();
            contentValues.put(DataContract.MoviesEntry.MOVIE_ID, movieId);
            contentValues.put(DataContract.MoviesEntry.OVERVIEW, movie.overview);
            contentValues.put(DataContract.MoviesEntry.POPULARITY, movie.popularity);
            contentValues.put(DataContract.MoviesEntry.POSTER_URL, movie.posterPath);
            contentValues.put(DataContract.MoviesEntry.RELEASE_DATE, movie.releaseDate);
            contentValues.put(DataContract.MoviesEntry.TITLE, movie.title);
            contentValues.put(DataContract.MoviesEntry.USER_RATING, movie.voteAverage);

            uri = contentResolver.insert(uri, contentValues);

            Log.d(TAG, "Added to favs: "+ uri.toString());
            isFavorite.postValue(true);
        }
    }

    public void removeFromFavorites(int movieId) {
        Uri uri = ContentUris.withAppendedId(DataContract.MoviesEntry.CONTENT_URI, movieId);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.MoviesEntry.MOVIE_ID, movieId);

        int deleted = contentResolver.delete(uri, null, null);

        Log.d(TAG, "Deleted: " + deleted);
        isFavorite.postValue(false);
    }

    private List<Movie> fetchFavoriteMovies(String sortOrder) {
        Uri uri = DataContract.MoviesEntry.CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null,null,null,sortOrder);

        List<Movie> movieList = new ArrayList<>();
        while(cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.id = cursor.getInt(cursor.getColumnIndex(DataContract.MoviesEntry.MOVIE_ID));
            movie.overview = cursor.getString(cursor.getColumnIndex(DataContract.MoviesEntry.OVERVIEW));
            movie.posterPath = cursor.getString(cursor.getColumnIndex(DataContract.MoviesEntry.POSTER_URL));
            movie.releaseDate = cursor.getString(cursor.getColumnIndex(DataContract.MoviesEntry.RELEASE_DATE));
            movie.title = cursor.getString(cursor.getColumnIndex(DataContract.MoviesEntry.TITLE));
            movie.voteAverage = cursor.getFloat(cursor.getColumnIndex(DataContract.MoviesEntry.USER_RATING));
            movie.popularity = cursor.getFloat(cursor.getColumnIndex(DataContract.MoviesEntry.POPULARITY));
            movieList.add(movie);
        }

        cursor.close();

        return movieList;
    }

    public LiveData<List<Movie>> getPopularFavorites() {
        if(favoriteMovies == null) {
            favoriteMovies = new MutableLiveData<>();
        }
        favoriteMovies.postValue(fetchFavoriteMovies(DataContract.MoviesEntry.POPULARITY + " DESC"));

        return favoriteMovies;
    }

    public LiveData<List<Movie>> getTopRatedFavorites() {
        if(favoriteMovies == null) {
            favoriteMovies = new MutableLiveData<>();
        }

        favoriteMovies.postValue(fetchFavoriteMovies(DataContract.MoviesEntry.USER_RATING + " DESC"));

        return favoriteMovies;
    }

    public LiveData<Boolean> isFavorite(Integer movieId) {

        if(isFavorite==null)
            isFavorite = new MutableLiveData<>();

        Uri uri = ContentUris.withAppendedId(DataContract.MoviesEntry.CONTENT_URI, movieId);

        Cursor cursor  = contentResolver.query(uri, null, null, null, null);
        if(cursor.getCount()>0) {
            isFavorite.postValue(true);
        } else {
            isFavorite.postValue(false);
        }

        cursor.close();

        return isFavorite;
    }
}
