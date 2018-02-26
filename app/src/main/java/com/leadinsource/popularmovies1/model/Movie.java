package com.leadinsource.popularmovies1.model;

import java.util.List;

/**
 * Data class based on JSON structure of the Movie DB API
 */

public class Movie {
    int vote_count;
    int id;
    boolean video;
    float vote_average;
    public String title;
    float popularity;
    public String poster_path;
    String original_language;
    String original_title;
    List<Integer> genre_ids;
    String backdrop_path;
    boolean adult;
    String overview;
    String release_date;
}
