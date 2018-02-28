package com.leadinsource.popularmovies2.net;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leadinsource.popularmovies2.model.Video;

import java.util.List;

/**
 * VideoResponse data class for GSON/JSON
 */

public class VideoResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Video> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

}
