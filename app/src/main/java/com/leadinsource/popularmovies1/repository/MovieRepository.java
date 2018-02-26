package com.leadinsource.popularmovies1.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.leadinsource.popularmovies1.BuildConfig;
import com.leadinsource.popularmovies1.model.Movie;
import com.leadinsource.popularmovies1.net.MovieDbResponse;
import com.leadinsource.popularmovies1.net.MoviesWebService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Single source of truth for the app
 */

public class MovieRepository {

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String BASE_URL = "https://api.themoviedb.org";
    private static final String TAG = "Retrofit";

    private MutableLiveData<List<Movie>> movies;

    public LiveData<List<Movie>> fetchPopularMovies() {

        if(movies == null) {
            movies = new MutableLiveData<>();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesWebService service = retrofit.create(MoviesWebService.class);

        Call<MovieDbResponse> call = service.listPopularMovies(API_KEY);

        //async request

        call.enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                Log.d(TAG, "Response status code: "+ response.code());

                if(!response.isSuccessful()) {

                    try {
                        Log.d(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }

                MovieDbResponse decodedResponse = response.body();
                if(decodedResponse==null) return;

                Log.d(TAG, "Successful response!");
                for( Movie movie : decodedResponse.results) {
                    Log.d("TAG", movie.title);
                }

                movies.postValue(decodedResponse.results);

            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                Log.d(TAG, "onFailure");
                Log.d(TAG, t.getMessage());
            }
        });

        return movies;
    }
}
