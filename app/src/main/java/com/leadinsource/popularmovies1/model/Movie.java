package com.leadinsource.popularmovies1.model;

import java.util.List;

/**
 * Data class based on JSON structure of the Movie DB API
 */

public class Movie {
    public int vote_count;
    int id;
    boolean video;
    public float vote_average;
    public String title;
    float popularity;
    public String poster_path;
    String original_language;
    String original_title;
    List<Integer> genre_ids;
    String backdrop_path;
    boolean adult;
    public String overview;
    public String release_date;
}
