package com.example.android.popularmovies.favorites_data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *  Created by domeniclavitola on 3/1/18.
 *
 *  MovieContract Object for using the Favorites Database
 */

public class MovieContract {

    // Build base Uri for the database's location
    static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path for table (favorites)
    static final String PATH_FAVORITE_MOVIES = "favoriteMovies";

    // Blank construction method
    private MovieContract() { super();}

    /**
     *  Item class for a favorites database movie entry
     */
    public static class MovieEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAVORITE_MOVIES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of movies.
         */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single movie.
         */
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;

        /** Name of database table for pets */
        final static String TABLE_NAME = "favoriteMovies";


        /** Unique ID number for the movie (only for use in the database table). type = integer */
        public final static String _ID = BaseColumns._ID;

        /** Column name for unique "themoviedb.org" movie id. type = integer */
        public static final String COLUMN_MOVIE_ID = "id";

        /** Column name for the amount of review votes the movie has received. type = integer */
        public static final String COLUMN_VOTE_COUNT = "voteCount";

        /** Column name for the movie's video attribute. type = boolean */
        public static final String COLUMN_VIDEO = "video";

        /** Column name for the movie's Title. type = string */
        public static final String COLUMN_TITLE = "title";

        /** Column name for the movie's Popularity. type = integer */
        public static final String COLUMN_POPULARITY = "mPopularity";

        /** Column name for the movie's average review vote. type = integer */
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";

        /** Column name for the movie's original title. type = string */
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";

        /** Column name for the movie's original language. type = String */
        public static final String COLUMN_ORIGINAL_LANGUAGE = "originalLanguage";

        /** Column name for the movie's backdrop image url path. type = string */
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";

        /** Column name for the movie's adult attribute. type = boolean */
        public static final String COLUMN_ADULT = "adult";

        /** Column name for the movie's poster image url path. type = string */
        public static final String COLUMN_POSTER_PATH = "posterPath";

        /** Column name for the movie's overview text. type = string */
        public static final String COLUMN_OVERVIEW = "overview";

        /** Column name for the movie's release date. type = string */
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
    }
}
