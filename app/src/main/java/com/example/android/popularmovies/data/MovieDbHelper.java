package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;


/**
 * Created by alfianlosari on 20/12/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int VERSION = 1;

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID          + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE    + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE             + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH       + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW          + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE      + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY        + " REAL NOT NULL, " +
        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
