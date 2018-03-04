package com.example.android.popularmoviesstage1.favorites_data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by domeniclavitola on 3/1/18.
 */

public class MovieContract {
    //Set up content authority to help identify the Content Provider
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviesstage1";

    // Make a usable URI (a BASE_CONTENT_URI) by concatonating the CONTENT_AUTHORITY constant
    // with the scheme "content://"
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Store the path for each of the tables which will be appended to the base content uri
    public static final String PATH_FAVORITE_MOVIES = "favoriteMovies";

    private MovieContract() {}

    /* Inner class that defines the table contents */
    public static class MovieEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAVORITE_MOVIES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;

        /** Name of database table for pets */
        public final static String TABLE_NAME = "favoriteMovies";


        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY = "mPopularity";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "originalLanguage";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
    }
}
