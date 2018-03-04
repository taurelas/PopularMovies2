package com.leadinsource.popularmovies2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leadinsource.popularmovies2.db.DataContract.MoviesEntry;

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
        final String SQL_CREATE_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesEntry.MOVIE_ID + " INTEGER, " +
                MoviesEntry.TITLE + " TEXT, " +
                MoviesEntry.POSTER_URL + " TEXT, " +
                MoviesEntry.OVERVIEW + " TEXT, " +
                MoviesEntry.USER_RATING + " FLOAT, " +
                MoviesEntry.RELEASE_DATE + " TEXT," +
                MoviesEntry.POPULARITY + " FLOAT" +
                ");";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
