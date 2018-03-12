package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;

import java.util.ArrayList;

/**
 * Created by Domenic LaVitola on 7/23/17.
 *
 * Main Activity. Displays scrolling grid of poster from The MovieProvider DB's top 20 most popular movies.
 * Allows users to sort movies by user rating and selecting movies to get more details.
 */

public class DiscoverActivity extends AppCompatActivity implements DiscoverRecyclerViewAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String LOG_TAG = DiscoverActivity.class.getName();

    // URL for movie data from The MovieDB
    private static final String POPULAR_MOVIES_REQUEST_URL =
            "http://api.themoviedb.org/3/movie/popular?api_key=101415efb001431da44d27585bfea0bc";

    // URL for movie data from The MovieDB
    private static final String TOP_RATED_MOVIES_REQUEST_URL =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=101415efb001431da44d27585bfea0bc";

    // Loader ID for Movies AsyncTaskLoader
    private static final int MOVIE_LOADER_ID = 1;

    // Set initial sorting values and setting to Popularity
    private static final int POPULARITY = 0;
    private static final int TOP_RATED = 1;
    private static final int FAVORITES = 2;
    private static int sort = POPULARITY;

    // ArrayList<MovieProvider> to be loaded with movie data
    private final ArrayList<Movie> movies = new ArrayList<>();
    // RecyclerViewHeader to Title movie sort setting.
    RecyclerViewHeader header;
    // RecyclerView for the list of movies
    private RecyclerView mRecyclerView;
    // TextView for setting Activity page title
    private TextView title;
    // TextView for setting Activity page subtitle
    private TextView subtitle;
    // ImageView for setting Loading Circle ImageView.
    private ImageView mLoadingCircle;
    // Adapter for the list of movies.
    private DiscoverRecyclerViewAdapter mAdapter;

    /**
     * Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST:Activity  onCreate() called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        this.getSupportActionBar();


        // Get display metrics loaded into "metrics"
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Get connectivity status of isConnected = (false|true)
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Assign RecyclerView and loadingTextView layout views
        mRecyclerView = findViewById(R.id.rv_movie_posters);
        mLoadingCircle = findViewById(R.id.discover_loading_icon);
        title = findViewById(R.id.title_tv);
        subtitle = findViewById(R.id.subtitle_tv);
        header = findViewById(R.id.recycler_view_header);

        /*
        * TextView for displaying network status messages
        */
        TextView loadingTextView = findViewById(R.id.tv_loading);
        // Check orientation to adjust grid columns and poster view sizes
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        header.attachTo(mRecyclerView);

        // Create and assign an adapter to send information through the RecyclerView
        mAdapter = new DiscoverRecyclerViewAdapter(this, movies, this);
        mRecyclerView.setAdapter(mAdapter);
        setHeader(sort);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Check if movie array list has already been obtained and there is an active network
        // connection before fetching data and loading a LoaderManager
        if (movies.size() == 0 & isConnected) {
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            // Internet connectivity issues are occurring. Notifies user to check internet connection
            loadingTextView.setText(R.string.check_internet);}
            findViewById(R.id.retry_fetch_button).setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the sort option selected
        if (sort == POPULARITY) {
            return new MoviesLoader(this, POPULAR_MOVIES_REQUEST_URL);}
        if (sort == TOP_RATED) {return new MoviesLoader(this, TOP_RATED_MOVIES_REQUEST_URL);}
        if (sort == FAVORITES) {return new MoviesLoader(this, "favorites");}
        setHeader(sort);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> moreMovies) {
        // Refreshes ArrayList movies with newly obtained movie data
        movies.clear();
        movies.addAll(moreMovies);
        // Remove loading and network alert views while showing poster RecyclerView
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingCircle.setVisibility(View.GONE);
        findViewById(R.id.tv_loading).setVisibility(View.GONE);
        findViewById(R.id.retry_fetch_button).setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        // Loader reset, so we can clear out our existing data
        Log.i(LOG_TAG, "TEST: onLoaderReset called");
        movies.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(int clickedItemIndex, ImageView sharedImageView) {
        // Start MovieDescriptionActivity for clickedItem when movie item in movies list is clicked
        Context context = this;
        Movie selectedMovie = movies.get(clickedItemIndex);
        Intent intentToStartDescriptionActivity = new Intent(context, MovieDescriptionActivity.class);
        intentToStartDescriptionActivity.putExtra("EXTRA_MOVIE_OBJECT", selectedMovie);
        Log.i(LOG_TAG, "onListItemClick: selected movie = " + selectedMovie.getmTitle());
        intentToStartDescriptionActivity.putExtra("EXTRA_POSTER_TRANSITION_NAME", sharedImageView.getTransitionName());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(DiscoverActivity.this,
                        sharedImageView,
                        ViewCompat.getTransitionName(sharedImageView));
        startActivity(intentToStartDescriptionActivity, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate discover_menu for sorting setting button
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.discover_menu, menu);
        return true;
    }

    /**
     * Inflate popup menu to use with sort menu item
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuItemView = findViewById(R.id.menu_item_sort_by);
        PopupMenu popupMenu = new PopupMenu(this, menuItemView);
        popupMenu.inflate(R.menu.actions);
        popupMenu.show();
        return true;
    }

    /**
     * Method to sort ArrayList of movies from Best User Rating to Worst  +
     * Notifies RecyclerView adapter of data set change to update UI.
     */
    public void sortTopRated(MenuItem item) {
        if (sort != TOP_RATED) {
            sort = TOP_RATED;
            refresh(findViewById(R.id.retry_fetch_button));
        }
    }

    /**
     * Method to get top 20 most popular movie from database
     *                 +
     * Notifies RecyclerView adapter of data set change to update UI.
     */
    public void sortPopular(MenuItem item) {
        if (sort != POPULARITY) {
            sort = POPULARITY;
            refresh(findViewById(R.id.retry_fetch_button));
        }
    }

    public void sortFavorites(MenuItem item) {
        if (sort != FAVORITES) {
            sort = FAVORITES;
            refresh(findViewById(R.id.retry_fetch_button));
        }
    }

    /**
     * Method to refresh DiscoverActivity UI
     */
    public void refresh(View view) {
        Intent refresh = new Intent(this, DiscoverActivity.class);
        startActivity(refresh);
        this.finish();
    }

    public void setHeader(int sort) {
        switch(sort) {
            case POPULARITY:
                title.setText(R.string.popular_movies_title);
                subtitle.setText(R.string.top_twenty);
                subtitle.setVisibility(View.VISIBLE);
                break;
            case TOP_RATED:
                title.setText(R.string.top_rated_title);
                subtitle.setText(R.string.top_twenty);
                subtitle.setVisibility(View.VISIBLE);
                break;
            case FAVORITES:
                title.setText(R.string.favorites_title);
                subtitle.setVisibility(View.GONE);
        }
    }
}

