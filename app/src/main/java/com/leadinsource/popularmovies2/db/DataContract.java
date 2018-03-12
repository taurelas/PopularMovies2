package com.leadinsource.popularmovies2.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Data Contract for storing Favorite Movies
 */

public class DataContract {

        public static final String AUTHORITY = "com.leadinsource.popularmovies2";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        public static final String PATH_FAVORITE_MOVIES = "favorite_movies";

        public static class FavoriteMoviesEntry implements BaseColumns {
            public static final String TABLE_NAME = "movies";
            public static final String MOVIE_ID = "movie_id";
            public static final String TITLE = "title";
            public static final String POSTER_URL = "poster_url";
            public static final String OVERVIEW = "overview";
            public static final String USER_RATING = "user_rating";
            public static final String RELEASE_DATE = "release_date";
            public static final String POPULARITY = "popularity";
            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

        }

}
