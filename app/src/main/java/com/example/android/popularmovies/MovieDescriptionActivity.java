package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.favorites_data.MovieContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Utilities.ExpandableTextView;
import Utilities.MovieDBJsonUtils;
import Utilities.NetworkUtils;
import Utilities.OverviewLeadingMarginSpan2;

import static Utilities.MovieDBJsonUtils.IMAGE_BASE_URL_342;
import static Utilities.MovieDBJsonUtils.IMAGE_BASE_URL_ORIGINAL;

/**
 * Created by Domenic LaVitola on 7/31/17.
 *
 * MovieProvider Description Activity. Displays selected movie details. (i.e. Title, Release Data,
 * User Rating, MovieProvider Overview, Poster Image, Promotional Background Image)
 */

public class MovieDescriptionActivity extends AppCompatActivity implements VideoItemAdapter.ListItemClickListener{

    private static String LOG_TAG = MovieDescriptionActivity.class.getName();

    // Loader Ids for our AsyncTaskLoader
    private static final int VIDEOS_LOADER_ID = 1;
    private static final int REVIEWS_LOADER_ID = 2;

    // RecyclerView for trailers and extras YouTube videos
    RecyclerView videosRecyclerView;
    // RecyclerView for reviews
    RecyclerView reviewsRecyclerView;
    // RecyclerView.Adapter forYouTube linked video Views
    VideoItemAdapter videosItemAdapter;
    // RecyclerView.Adapter text review Views
    ReviewItemAdapter reviewItemAdapter;


    RelativeLayout overviewTextRelativeLayout;
    ExpandableTextView expandableOverviewTextView;// "overview" view to be referenced for expanding
    TextView overviewTextViewLand;      // "overview" view to be referenced for non-expanding modes
    ReviewItemAdapter.ReviewsListClickListener reviewsListClickListener;// ClickListener for reviews
     View emptyTrailersLayout;
    View emptyReviewsLayout;
    TextView showMoreText;
    private final ArrayList<Review> reviews = new ArrayList<>();
    private final ArrayList<Video> videos = new ArrayList<>();
    Movie movie;
    int orientation;
    ArrayList<Integer> favoriteMovieIds;
    ImageView favoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_description);
        movie = (Movie) getIntent().getSerializableExtra("EXTRA_MOVIE_OBJECT");
        orientation = getResources().getConfiguration().orientation;
        supportPostponeEnterTransition();

        showMoreText = findViewById(R.id.show_more_button_tv);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            overviewTextViewLand = findViewById(R.id.etv_overview_land);
        } else {
            expandableOverviewTextView = findViewById(R.id.etv_overview);
            overviewTextRelativeLayout = findViewById(R.id.overview_text_rl);
            expandableOverviewTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandableOverviewTextView.onClick();
                    alternateShowMoreText();
                }
            });
        }
        /// Loads main movie data:
        //      Images, Title, Synopsis, & Rating
        loadMovieData(movie, orientation);

        // Get Support Action Bar and show UP button
        //noinspection ConstantConditions
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        emptyReviewsLayout = findViewById(R.id.empty_reviews_l);
        emptyTrailersLayout = findViewById(R.id.empty_trailer_l);


        // Load trailers
        videosRecyclerView = findViewById(R.id.trailers_rv);
        videosItemAdapter = new VideoItemAdapter(this, videos, this);
        videosRecyclerView.setAdapter(videosItemAdapter);
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        reviewsRecyclerView = findViewById(R.id.reviews_rv);
        reviewsListClickListener = new ReviewItemAdapter.ReviewsListClickListener() {
            @Override
            public void onClick(View view, int position) {
                ExpandableTextView textView = view.findViewById(R.id.review_content_tv);
                textView.onClick();
                reviewItemAdapter.notifyDataSetChanged();
            }
        };
        reviewItemAdapter = new ReviewItemAdapter(this, reviews, reviewsListClickListener);
        reviewsRecyclerView.setAdapter(reviewItemAdapter);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Initialize trailer and review loaders
        getSupportLoaderManager().initLoader(VIDEOS_LOADER_ID, null, videosLoaderListener);
        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, reviewsLoaderListener);
    }

    /**
     * For interacting with UP button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's UP button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                assert upIntent != null;
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Assign view with appropriate text, images, or rating
    private void loadMovieData(Movie movie, int orientation) {

        // Set TextView for movie title
        TextView movieTitleTextView = findViewById(R.id.tv_movie_title);
        movieTitleTextView.setText(movie.getmTitle());

        // Set ImageView for the poster image and set transition name to
        ImageView posterImageView = findViewById(R.id.iv_description_poster);
        String posterImagePath = IMAGE_BASE_URL_342 + movie.getmPosterPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            posterImageView.setTransitionName(getIntent().getStringExtra("EXTRA_POSTER_TRANSITION_NAME"));
        }
        Picasso.with(this)
                .load(posterImagePath)
                .into(posterImageView,  new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SpannableString overviewSS = new SpannableString("\t\t\t\t" + movie.getmOverview());
            int leftMargin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 253, getResources().getDisplayMetrics()));
            overviewSS.setSpan(new OverviewLeadingMarginSpan2(7, leftMargin), 0, overviewSS.length(), 0);
            overviewTextViewLand.setText(overviewSS);
        } else {
            expandableOverviewTextView.setText(movie.getmOverview());
        }
        ImageView backdropImageView = findViewById(R.id.iv_background);
        String backdropImagePath = IMAGE_BASE_URL_ORIGINAL + movie.getmBackdropPath();
        Picasso.with(this)
                .load(backdropImagePath)
                .into(backdropImageView);
        Log.i(LOG_TAG, "loadMovieData: image path = " + backdropImagePath);
        TextView mReleaseDate = findViewById(R.id.tv_release_date);
        mReleaseDate.setText(MovieDBJsonUtils.formatDate(movie.getmReleaseDate()));
        RatingBar mRating = findViewById(R.id.rb_rating);
        mRating.setRating(MovieDBJsonUtils.formatStars(movie.getmVoteAverage()));
    }


    private android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Video>> videosLoaderListener
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Video>>() {
        @Override
        public Loader<ArrayList<Video>> onCreateLoader(int id, Bundle args) {
            return new VideosLoader(MovieDescriptionActivity.this, NetworkUtils.constructVideosUrl(movie.getmId()));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Video>> loader, ArrayList<Video> moreVideos) {
            videos.clear();
            videos.addAll(moreVideos);
            if (videos.size() > 0 ) {
                emptyTrailersLayout.setVisibility(View.GONE);
            }
            // Remove loading and network alert views while showing poster RecyclerView
            videosRecyclerView.setVisibility(View.VISIBLE);
            videosItemAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Video>> loader) {
            videos.clear();
            videosItemAdapter.notifyDataSetChanged();
        }
    };

    private android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Review>> reviewsLoaderListener
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Review>>() {
        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
            return new ReviewsLoader(MovieDescriptionActivity.this, NetworkUtils.constructReviewUrl(movie.getmId()));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> moreReviews) {
            reviews.clear();
            reviews.addAll(moreReviews);
            if (reviews.size() > 0 ) {
                emptyReviewsLayout.setVisibility(View.GONE);
            }
            reviewsRecyclerView.setVisibility(View.VISIBLE);
            reviewItemAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {
            reviews.clear();
            reviewItemAdapter.notifyDataSetChanged();
        }
    };

    public void alternateShowMoreText() {
        if (expandableOverviewTextView.isTrimmed()) {
            showMoreText.setText(R.string.show_more);
            Log.i(LOG_TAG, "alternateShowMoreText: show less");
        } else {
            showMoreText.setText(R.string.show_less);
            Log.i(LOG_TAG, "alternateShowMoreText: show more");
        }
    }

    @Override
    public void onListItemClick(int i) {
        Log.i(LOG_TAG, "onListItemClick: trailer clicked");
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videos.get(i).getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videos.get(i).getKey()));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }

    public void toggleFavorite(MenuItem item) {
        boolean isChecked = item.isChecked();
        if (isChecked) {
            int deleteResult = removeMovieFromFavorites(movie, item);
            if (deleteResult != 0) {
                favoritesButton.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_menu_icon);
                favoritesButton.startAnimation(rotation);
                item.setActionView(favoritesButton);
                item.setChecked(!item.isChecked());
            }
        } else {
            addMovieToFavorites(movie, item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate discover_menu for sorting setting button
        super.onCreateOptionsMenu(menu);
        favoriteMovieIds = refreshFavoriteMovieIds();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_page_actions, menu);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        favoritesButton = (ImageView) layoutInflater.inflate(R.layout.iv_favorites, null);
        menu.getItem(0).setActionView(favoritesButton);
        Log.i(LOG_TAG, "onCreateOptionsMenu: is favorite = " + isFavorite(movie));
        if (isFavorite(movie)) {
            favoritesButton.setImageResource(R.drawable.ic_favorite_fill_white_48dp);
            menu.getItem(0).setChecked(true);
        } else {
            favoritesButton.setImageResource(R.drawable.ic_favorite_border_white_48dp);
            menu.getItem(0).setChecked(false);
        }
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite(menu.getItem(0));
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    /**
     * Get movie details and save movie to favorites database.
     */
    private void addMovieToFavorites(Movie movie, MenuItem item) {
        if (!isFavorite(movie)) {
            int vote_count_int = movie.getmVoteCount();
            int id_int = movie.getmId();
            int video_bool = movie.ismVideo() ? 1 : 0;
            double vote_average_double = movie.getmVoteAverage();
            String title_string = movie.getmTitle();
            double popularity_double = movie.getmPopularity();
            String poster_path_string = movie.getmPosterPath();
            String original_language_string = movie.getmOriginalLanguage();
            String original_title_string = movie.getmOriginalTitle();
            String backdropPath_string = movie.getmBackdropPath();
            int adult_bool = movie.ismAdult() ? 1 : 0;
            String overview_string = movie.getmOverview();
            String release_date_string = movie.getmReleaseDate();

            // Create a ContentValues object where column names are the keys,
            // and Toto's pet attributes are the values.
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, vote_count_int);
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id_int);
            values.put(MovieContract.MovieEntry.COLUMN_VIDEO, video_bool);
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average_double);
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, title_string);
            values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity_double);
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path_string);
            values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, original_language_string);
            values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, original_title_string);
            values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath_string);
            values.put(MovieContract.MovieEntry.COLUMN_ADULT, adult_bool);
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview_string);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date_string);

            // Insert a new pet into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.favorites_add_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.favorites_add_success),
                        Toast.LENGTH_SHORT).show();
                favoritesButton.setImageResource(R.drawable.ic_favorite_fill_white_48dp);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_menu_icon);
                favoritesButton.startAnimation(rotation);
                item.setActionView(favoritesButton);
                item.setChecked(!item.isChecked());
            }
        }
        favoriteMovieIds = refreshFavoriteMovieIds();

    }

    private int removeMovieFromFavorites(Movie movie, MenuItem item) {
        int uri = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + movie.getmId(), null);
        Log.i(LOG_TAG, "removeMovieFromFavorites: uri = " + uri);
        return uri;
    }

    private ArrayList<Integer> refreshFavoriteMovieIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        String[] projection = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
        };
        Cursor cursor = getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,     // The table to query
                projection,               // The columns to return
                null,                     // Selection criteria
                null,                     // Selection criteria
                null);
        while(cursor != null && cursor.moveToNext()){
            int movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            ids.add(movieId);
            Log.i(LOG_TAG, "refreshFavoriteMovieIds: ids = " + ids);
        }
        if (cursor != null) {
            cursor.close();
        }
        Log.i(LOG_TAG, "refreshFavoriteMovieIds: ids = " + ids);
        return ids;
    }

    private boolean isFavorite(Movie movie) {
        for (int i = 0; i < favoriteMovieIds.size(); i++) {
            if (movie.getmId() == favoriteMovieIds.get(i)) {
                Log.i(LOG_TAG, "isFavorite: Movie = " + movie.getmTitle() + ". ID = " + movie.getmId() + ". matching id = " + favoriteMovieIds.get(i));
                return true;
            }
        }
        return false;
    }
}
