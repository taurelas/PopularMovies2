package com.leadinsource.popularmovies2.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.leadinsource.popularmovies2.BuildConfig;
import com.leadinsource.popularmovies2.model.Movie;
import com.leadinsource.popularmovies2.model.Video;
import com.leadinsource.popularmovies2.net.MovieResponse;
import com.leadinsource.popularmovies2.net.MoviesWebService;
import com.leadinsource.popularmovies2.net.VideoResponse;

import java.io.IOException;
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
    private static final String TAG = "Retrofit";

    private MutableLiveData<List<Movie>> movies;
    private MutableLiveData<List<Video>> trailers;


    private MovieRepository() {
        // private constructor
    }

    public static MovieRepository getInstance() {
        if(INSTANCE==null) {
            INSTANCE = new MovieRepository();
        }

        return INSTANCE;
    }


    public LiveData<List<Movie>> fetchPopularMovies() {

        if(movies == null) {
            movies = new MutableLiveData<>();
        }

        Call<MovieResponse> call = getMoviesWebService().listPopularMovies(API_KEY);

        //async request
        return enqueue(call);
    }

    public LiveData<List<Movie>> fetchTopRatedMovies() {
        if(movies == null) {
            movies = new MutableLiveData<>();
        }

        Call<MovieResponse> call = getMoviesWebService().listTopRatedMovies(API_KEY);

        //async request
        return enqueue(call);

    }

    public LiveData<List<Video>> fetchTrailers(int movieId) {
        if(trailers==null) {
            trailers = new MutableLiveData<>();
        }

        Call<VideoResponse> call = getMoviesWebService().listVideos(movieId, API_KEY);

        return enqueue2(call);
    }

    private LiveData<List<Video>> enqueue2(Call<VideoResponse> call) {
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



    private LiveData<List<Movie>> enqueue(Call<MovieResponse> call) {
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

}
