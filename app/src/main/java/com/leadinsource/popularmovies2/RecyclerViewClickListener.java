package com.leadinsource.popularmovies2;

import com.leadinsource.popularmovies2.model.Movie;

/**
 * The activity that implements it handles click on the RecyclerView items
 */

public interface RecyclerViewClickListener {
    void onItemClick(Movie movie);
}
