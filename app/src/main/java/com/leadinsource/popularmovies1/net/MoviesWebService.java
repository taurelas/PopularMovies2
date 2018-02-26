package com.leadinsource.popularmovies1.net;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * The Movie DB service definition
 */

public interface MoviesWebService {
    String POPULAR_ENDPOINT = "3/movie/popular";
    String API_ARG = "api_key";

    @GET(POPULAR_ENDPOINT)
    Call<MovieDbResponse> listPopularMovies(
            @Query(API_ARG) String arg
    );
}
