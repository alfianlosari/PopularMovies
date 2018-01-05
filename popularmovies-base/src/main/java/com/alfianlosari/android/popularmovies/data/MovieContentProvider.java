package com.alfianlosari.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by alfianlosari on 20/12/17.
 */

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_MOVIE_ID = 101;
    private MovieDbHelper mMovieDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MovieEntry.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MovieEntry.PATH_MOVIES + "/#", MOVIES_WITH_MOVIE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Long.parseLong(contentValues.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor cursor;
        switch (match) {
            case MOVIES:
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1
                );
                break;

            case MOVIES_WITH_MOVIE_ID:
                String id = uri.getLastPathSegment();
                String[] args = new String[]{id};
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        strings,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        args,
                        null,
                        null,
                        s1

                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int moviesDeleted = 0;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES_WITH_MOVIE_ID:
                String movieId = uri.getLastPathSegment();
                moviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
