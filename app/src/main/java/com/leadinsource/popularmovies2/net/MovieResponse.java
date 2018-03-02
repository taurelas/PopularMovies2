package com.leadinsource.popularmovies2.net;

import com.leadinsource.popularmovies2.model.Movie;

import java.util.List;

/**
 * Represents a JSON response The Movie DB
 */

public class MovieResponse {
    int page;
    int total_results;
    int total_pages;
    public List<Movie> results;
}
