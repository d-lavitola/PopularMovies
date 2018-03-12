package com.example.android.popularmovies.favorites_data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by domeniclavitola on 3/1/18.
 *
 * ContentProvider for the favorites' table movie objects.
 */

public class MovieProvider extends ContentProvider {

    /** Database helper object */
    private MovieDbHelper mDbHelper;

    /** URI matcher code for the content URI for the pets table */
    public static final int FAVORITE_MOVIES = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    public static final int MOVIE_ID = 101;

    /** Tag for the log messages */
    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer to run the first time anything is called from this class.
    static {
        // Calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize with a corresponding code to return when a match is found.

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES + "/#", MOVIE_ID);
    }


    /**
     * MovieProvider's onCreate method. Sets mDBHelper to a new MovieDbHelper with getContext().
     *
     * @return boolean
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    /**
     * Method for querying the database and returning a Cursor with the selected movie(s) details.
     *
     * @param uri URI for determining if query is for a single movie or a list.
     * @param projection Determine movie attributes (columns) to return from the query.
     * @param selection What movie attributes to use for selecting which movie to return.
     * @param selectionArgs Arguments for the selection attributes.
     * @param sortOrder Sort method for returned data
     * @return Cursor
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        // Check if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {

            case FAVORITE_MOVIES:
                // For the FAVORITE_MOVIES code, query the favoriteMovies table directly with the
                // given projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the movies table.
                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIE_ID:

                // For the MOVIE_ID code, select movie from the given uri's database id
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Query table with the selection arguments and set the cursor object to
                // the cursor object it returns.
                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /** Get the type of URI passed to the MovieProvider
     *
     * @param uri URI passed to the MovieProvider
     * @return String of uri Type
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                return MovieContract.MovieEntry.CONTENT_LIST_TYPE;
            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Switch statement for determining the insert method.
     *
     * @param uri URI passed to the MovieProvider
     * @param contentValues Data of the movies to be added to the database.
     * @return return Uri of the inserted data
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                return insertMovie(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * DB method to insert movies into a table
     *
     * @param uri URI passed to the MovieProvider
     * @param values Data of the movies to be added to the database.
     * @return return Uri of the inserted data
     */
    private Uri insertMovie(Uri uri, ContentValues values) {
        Integer movieId = values.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        if (movieId == null) {
            throw new IllegalArgumentException("MovieProvider requires an id");
        }
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new movie with the given values
        long id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * DB method to delete movies from a table
     *
     * @param uri URI passed to the MovieProvider.
     * @param selection What movie attributes to use for selecting which movie to return.
     * @param selectionArgs Arguments for the selection attributes.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get a writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        // Determine if user is deleting movies or list of movies.
        switch (match) {
            case FAVORITE_MOVIES:
                // Delete all rows that match the selection and selectionArgs
                return database.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
            case MOVIE_ID:
                // Delete a single row given by the ID in the URI
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return database.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Method for updating database table data.
     * @param uri URI passed to the MovieProvider.
     * @param values Values for updating database rows
     * @param selection Selection criteria for the method.
     * @param selectionArgs Selection values for the method.
     * @return Integer for update success or failure.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                return updateFavoriteMovies(uri, values, selection, selectionArgs);
            case MOVIE_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateFavoriteMovies(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateFavoriteMovies(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Update the selected pets in the pets database table with the given ContentValues
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_ID)) {
            String movieId = values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            if (movieId == null) {
                throw new IllegalArgumentException("Movie requires an id.");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(MovieContract.MovieEntry.COLUMN_TITLE)) {
            Integer title = values.getAsInteger(MovieContract.MovieEntry.COLUMN_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Movie requires a title.");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(MovieContract.MovieEntry.COLUMN_POSTER_PATH)) {
            //Check that the weight is greater than or equal to 0kg
            Integer posterPath = values.getAsInteger(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
            if (posterPath == null) {
                throw new IllegalArgumentException("Movie requires a poster path.");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
