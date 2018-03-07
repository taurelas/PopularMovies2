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
import com.leadinsource.popularmovies2.db.DataContract.TopMoviesEntry;
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
 * TODO optimize code: maybe DBOps.class that handles operations with database and network helper
 * that handles network tasks
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
    private MutableLiveData<Movie> movie;

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

            List<Movie> moviesFromDB = getTopMoviesFromDB();

            if(moviesFromDB!=null) {
                movies = new MutableLiveData<>();
                movies.setValue(moviesFromDB);
                mapMovies(moviesFromDB);
                return movies;
            }
        }

        movies = new MutableLiveData<>();

        Call<MovieResponse> call = getMoviesWebService().listPopularMovies(API_KEY);
        //async request
        enqueueMovies(call);

        return movies;
    }

    private List<Movie> getTopMoviesFromDB() {

        Uri uri = TopMoviesEntry.CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null,null);

        if(cursor.getCount()<=0) {
            return null;
        }

        List<Movie> result = getMoviesFromCursor(cursor);

        mapMovies(result);

        return result;
    }

    public LiveData<List<Movie>> fetchTopRatedMovies() {
        if(movies == null) {

           List<Movie> moviesFromDB = getTopMoviesFromDB();

           if(moviesFromDB!=null) {
               movies = new MutableLiveData<>();
               movies.setValue(moviesFromDB);
               Log.d(TAG, "Returning from DB " +movies.getValue().get(0).title);
                return movies;
           }
        }

        Log.d(TAG, "Returning from Network");
        movies = new MutableLiveData<>();

        Call<MovieResponse> call = getMoviesWebService().listTopRatedMovies(API_KEY);

        //async request
        enqueueMovies(call);

        return movies;

    }

    public LiveData<List<Video>> fetchTrailers(int movieId) {
        if(trailers==null) {
            trailers = new MutableLiveData<>();
        }

        Call<VideoResponse> call = getMoviesWebService().listVideos(movieId, API_KEY);

        return enqueueTrailers(call);
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

                Log.d("EnqueueReviews", "Successful response!");
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

    private LiveData<List<Video>> enqueueTrailers(Call<VideoResponse> call) {
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

                Log.d("EnqueueTrailers", "Successful response!");

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

    private void enqueueMovies(Call<MovieResponse> call) {
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

                Log.d("EnqueueMovies", "Successful response!");
                saveMovieCacheToDisk(decodedResponse.results);
                mapMovies(decodedResponse.results);
                movies.postValue(decodedResponse.results);
            }


            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                movies.postValue(new ArrayList<>());
                Log.d(TAG, "provides empty list");
                Log.d(TAG, t.getMessage());
            }
        });

    }

    private void saveMovieCacheToDisk(List<Movie> results) {

        deleteOldCache();

        ContentValues[] cv = new ContentValues[results.size()];
        int index = 0;
        for(Movie movie: results) {
            ContentValues contentValues = getMovieContentValues(movie);
            cv[index++] = contentValues;
        }

        int inserted = contentResolver.bulkInsert(TopMoviesEntry.CONTENT_URI, cv);

        Log.d(TAG, "Inserted "+ inserted + " rows into DiskCache");
    }

    private void deleteOldCache() {
        int deleted;

        deleted = contentResolver.delete(TopMoviesEntry.CONTENT_URI, null, null);

        Log.d(TAG, "Deleted: "+deleted + " from DiskCache");
    }

    private void mapMovies(List<Movie> results) {
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
            Uri uri = ContentUris.withAppendedId(DataContract.FavoriteMoviesEntry.CONTENT_URI, movieId);
            Movie movie = movieCache.get(movieId);

            ContentValues contentValues = getMovieContentValues(movie);

            uri = contentResolver.insert(uri, contentValues);

            Log.d(TAG, "Added to favs: "+ uri.toString());
            isFavorite.postValue(true);
        }
    }

    ContentValues getMovieContentValues(Movie movie) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.FavoriteMoviesEntry.MOVIE_ID, movie.id);
        contentValues.put(DataContract.FavoriteMoviesEntry.OVERVIEW, movie.overview);
        contentValues.put(DataContract.FavoriteMoviesEntry.POPULARITY, movie.popularity);
        contentValues.put(DataContract.FavoriteMoviesEntry.POSTER_URL, movie.posterPath);
        contentValues.put(DataContract.FavoriteMoviesEntry.RELEASE_DATE, movie.releaseDate);
        contentValues.put(DataContract.FavoriteMoviesEntry.TITLE, movie.title);
        contentValues.put(DataContract.FavoriteMoviesEntry.USER_RATING, movie.voteAverage);

        return contentValues;
    }

    public void removeFromFavorites(int movieId) {
        Uri uri = ContentUris.withAppendedId(DataContract.FavoriteMoviesEntry.CONTENT_URI, movieId);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.FavoriteMoviesEntry.MOVIE_ID, movieId);

        int deleted = contentResolver.delete(uri, null, null);

        Log.d(TAG, "Deleted: " + deleted);
        isFavorite.postValue(false);
    }

    private List<Movie> fetchFavoriteMovies(String sortOrder) {
        Uri uri = DataContract.FavoriteMoviesEntry.CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null,null,null,sortOrder);

        List<Movie> result = getMoviesFromCursor(cursor);
        mapMovies(result);

        return result;
    }

    private List<Movie> getMoviesFromCursor(Cursor cursor) {
        List<Movie> movieList = new ArrayList<>();
        while(cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.id = cursor.getInt(cursor.getColumnIndex(DataContract.FavoriteMoviesEntry.MOVIE_ID));
            movie.overview = cursor.getString(cursor.getColumnIndex(DataContract.FavoriteMoviesEntry.OVERVIEW));
            movie.posterPath = cursor.getString(cursor.getColumnIndex(DataContract.FavoriteMoviesEntry.POSTER_URL));
            movie.releaseDate = cursor.getString(cursor.getColumnIndex(DataContract.FavoriteMoviesEntry.RELEASE_DATE));
            movie.title = cursor.getString(cursor.getColumnIndex(DataContract.FavoriteMoviesEntry.TITLE));
            movie.voteAverage = cursor.getFloat(cursor.getColumnIndex(DataContract.FavoriteMoviesEntry.USER_RATING));
            movie.popularity = cursor.getFloat(cursor.getColumnIndex(DataContract.FavoriteMoviesEntry.POPULARITY));
            movieList.add(movie);
        }

        cursor.close();

        return movieList;
    }


    public LiveData<List<Movie>> getPopularFavorites() {
        if(favoriteMovies == null) {
            favoriteMovies = new MutableLiveData<>();
        }
        favoriteMovies.postValue(fetchFavoriteMovies(DataContract.FavoriteMoviesEntry.POPULARITY + " DESC"));

        return favoriteMovies;
    }

    public LiveData<List<Movie>> getTopRatedFavorites() {
        if(favoriteMovies == null) {
            favoriteMovies = new MutableLiveData<>();
        }

        favoriteMovies.postValue(fetchFavoriteMovies(DataContract.FavoriteMoviesEntry.USER_RATING + " DESC"));

        return favoriteMovies;
    }

    @NonNull
    public LiveData<Boolean> isFavorite(Integer movieId) {

        if(isFavorite==null)
            isFavorite = new MutableLiveData<>();

        Uri uri = ContentUris.withAppendedId(DataContract.FavoriteMoviesEntry.CONTENT_URI, movieId);

        Cursor cursor  = contentResolver.query(uri, null, null, null, null);
        if(cursor.getCount()>0) {
            isFavorite.postValue(true);
        } else {
            isFavorite.postValue(false);
        }

        cursor.close();

        return isFavorite;
    }

    public LiveData<Movie> getMovie(Integer input) {

        if(movie == null) {
            movie = new MutableLiveData<>();
        }

        movie.setValue(movieCache.get(input));

        return movie;
    }

    public void clearDiskCache() {
        deleteOldCache();
    }
}