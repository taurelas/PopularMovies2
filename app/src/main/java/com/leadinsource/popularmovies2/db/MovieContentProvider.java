package com.leadinsource.popularmovies2.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.leadinsource.popularmovies2.db.DataContract.FavoriteMoviesEntry;

/**
 * Content Provider for Favorite Movies saved to the device
 */

public class MovieContentProvider extends ContentProvider {

    MoviesDbHelper moviesDbHelper;

    public static final int FAVORITE_MOVIES = 100;
    public static final int FAVORITE_MOVIES_WITH_ID = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_FAVORITE_MOVIES + "/#", FAVORITE_MOVIES_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();

        Cursor retCursor;

        int match = uriMatcher.match(uri);

        switch (match) {
            case FAVORITE_MOVIES:
                retCursor = db.query(FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();
                String idSelection = FavoriteMoviesEntry.MOVIE_ID + "=?";
                String[] idSelectionArgs = new String[]{id};
                retCursor = db.query(FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        idSelection,
                        idSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        Uri returnUri;

        int match = uriMatcher.match(uri);

        if (match == FAVORITE_MOVIES_WITH_ID) {
            long id = db.insert(FavoriteMoviesEntry.TABLE_NAME, null, values);
            if (id > 0) {
                returnUri = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, id);
            } else {
                throw new android.database.SQLException("Failed to insert row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();

        int deleted;


        int match = uriMatcher.match(uri);

        switch (match) {
            case FAVORITE_MOVIES_WITH_ID:

                String id = uri.getLastPathSegment();
                String idSelection = FavoriteMoviesEntry.MOVIE_ID + "=?";
                String[] idSelectionArgs = new String[]{id};

                deleted = db.delete(FavoriteMoviesEntry.TABLE_NAME, idSelection, idSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (deleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }

    /**
     * Unused
     *
     * @param uri    determines what action (if any) the provider will execute
     * @param values Array of ContentValues to be inserted into database
     * @return number of inserted rows
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        throw new UnsupportedOperationException("Unknown uri " + uri);

    }
}
