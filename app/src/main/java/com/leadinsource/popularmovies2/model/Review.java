package com.leadinsource.popularmovies2.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model data class for user reviews of particular
 */

public class Review {
    @SerializedName("id")
    public String id;
    @SerializedName("author")
    public String author;
    @SerializedName("content")
    public String content;
    @SerializedName("url")
    public String url;

}
