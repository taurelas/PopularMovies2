package com.leadinsource.popularmovies2.net;

import com.google.gson.annotations.SerializedName;
import com.leadinsource.popularmovies2.model.Review;

import java.util.List;

/**
 * Data class for GSON/JSON response when querying reviews
 */

public class ReviewResponse {

    @SerializedName("id")
    public Integer id;
    @SerializedName("page")
    public Integer page;
    @SerializedName("results")
    public List<Review> results = null;
    @SerializedName("total_pages")
    public Integer totalPages;
    @SerializedName("total_results")
    public Integer totalResults;

}
