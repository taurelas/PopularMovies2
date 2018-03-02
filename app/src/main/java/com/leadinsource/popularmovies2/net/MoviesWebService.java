package com.leadinsource.popularmovies2.net;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The Movie DB service definition
 */

public interface MoviesWebService {
    String POPULAR_ENDPOINT = "3/movie/popular";
    String TOP_RATED_ENDPOINT = "3/movie/top_rated";

    String API_ARG = "api_key";

    @GET(POPULAR_ENDPOINT)
    Call<MovieResponse> listPopularMovies(
            @Query(API_ARG) String arg
    );

    @GET(TOP_RATED_ENDPOINT)
    Call<MovieResponse> listTopRatedMovies(
            @Query(API_ARG) String arg
    );

    @GET("3/movie/{movie_id}/videos")
    Call<VideoResponse> listVideos(
        @Path("movie_id") int movieId, @Query(API_ARG) String apiKey);

}
