package com.leadinsource.popularmovies2.net;

import com.leadinsource.popularmovies2.model.Video;

import java.util.List;

/**
 * VideoResponse data class for GSON/JSON
 */

public class VideoResponse {
    public Integer id;
    public List<Video> results;
}
