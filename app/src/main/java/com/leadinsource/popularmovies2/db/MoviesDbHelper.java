package com.leadinsource.popularmovies2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leadinsource.popularmovies2.db.DataContract.FavoriteMoviesEntry;

/**
 * Helper for SQLite DB to store favorite movies
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;


    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + FavoriteMoviesEntry.TABLE_NAME + " (" +
                FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMoviesEntry.MOVIE_ID + " INTEGER, " +
                FavoriteMoviesEntry.TITLE + " TEXT, " +
                FavoriteMoviesEntry.POSTER_URL + " TEXT, " +
                FavoriteMoviesEntry.OVERVIEW + " TEXT, " +
                FavoriteMoviesEntry.USER_RATING + " FLOAT, " +
                FavoriteMoviesEntry.RELEASE_DATE + " TEXT," +
                FavoriteMoviesEntry.POPULARITY + " FLOAT" +
                ");";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
