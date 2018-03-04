package com.example.android.popularmoviesstage1;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.popularmoviesstage1.favorites_data.MovieContract;

import java.io.IOException;
import java.util.ArrayList;

import Utilities.MovieDBJsonUtils;
import Utilities.NetworkUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by domeniclavitola on 7/31/17.
 *
 * MoviesLoader runs a network request and json parsing in the background to load an ArrayList<MovieProvider>
 * Object
 */

class MoviesLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private String jsonResponse;
    private Context mContext;
    /** Tag for log messages */
    private static final String LOG_TAG = MoviesLoader.class.getName();

    /** Query URL */
    private final String mUrl;

    MoviesLoader(Context context, String url) {
        super(context);
        mContext = context;
        mUrl = url;
    }

    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading is called");
        forceLoad();
    }

    /**
     * Background networking and jsonParsing tasks. Returns an ArrayList<MovieProvider> movies
     */
    @Override
    public ArrayList<Movie> loadInBackground() {
        Log.i(TAG, "loadInBackground: loadInBackground has been called");
        ArrayList<Movie> movies;
        if (mUrl == null) {
            return null;
        }
        if (mUrl == "favorites") {
            movies = new ArrayList<>();
            String[] projection = {"*"};
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,     // The table to query
                    projection,               // The columns to return
                    null,                     // Selection criteria
                    null,                     // Selection criteria
                    null);                    // The sort order for the returned rows
            try {
                while (cursor.moveToNext()) {
                    try {
                        int voteCount = cursor.getInt(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
                        int movieId = cursor.getInt(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_MOVIE_ID));;
                        boolean video = (cursor.getInt(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_VIDEO)) == 1);
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_TITLE));;
                        double popularity = cursor.getInt(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_VOTE_COUNT));;
                        double voteAverage = cursor.getInt(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                        String originalTitle = cursor.getString(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
                        String originalLanguage = cursor.getString(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE));
                        int[] genreIds = null;
                        String backdropPath = cursor.getString(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH));
                        boolean adult = (cursor.getInt(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_ADULT)) == 1);
                        String posterPath = cursor.getString(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                        String overview = cursor.getString(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_OVERVIEW));
                        String releaseDate = cursor.getString(cursor.getColumnIndexOrThrow(
                                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                        movies.add(new Movie(voteCount, movieId, video, title, popularity, voteAverage,
                                originalTitle, originalLanguage, null, backdropPath, adult, posterPath,
                                overview, releaseDate));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                cursor.close();
            }
            Log.i(TAG, "loadInBackground: title = " + (cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        } else {
            try {
                jsonResponse = NetworkUtils.makeHttpRequest(mUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "loadInBackground: jsonResponse = " + jsonResponse);

            movies = MovieDBJsonUtils.extractMovies(jsonResponse);
        }
        return movies;
    }
}
