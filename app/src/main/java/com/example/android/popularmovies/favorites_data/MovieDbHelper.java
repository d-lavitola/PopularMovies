package com.example.android.popularmovies.favorites_data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by domeniclavitola on 3/1/18.
 *
 * SQLiteOpenHelper for methods creating sql database.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "favoriteMovies.db";

    /** Database version */
    private static final int DATABASE_VERSION = 2;

    /** Blank MovieDbHelper constructor */
    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** SQL table onCreate method */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.MovieEntry.TABLE_NAME + "("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER, "
                + MovieContract.MovieEntry.COLUMN_VIDEO + " INTEGER, "
                + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, "
                + MovieContract.MovieEntry.COLUMN_TITLE + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL, "
                + MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_ADULT + " INTEGER, "
                + MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT);";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
