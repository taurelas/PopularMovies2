package com.leadinsource.popularmovies1.net;

import com.leadinsource.popularmovies1.model.Movie;

import java.util.List;

/**
 * Represents a JSON response The Movie DB
 */

public class MovieDbResponse {
    int page;
    int total_results;
    int total_pages;
    public List<Movie> results;
}
