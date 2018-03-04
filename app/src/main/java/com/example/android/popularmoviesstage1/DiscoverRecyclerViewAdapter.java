package com.example.android.popularmoviesstage1;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Utilities.MovieDBJsonUtils;

/**
 * Created by Domenic LaVitola on 7/23/17.
 *
 * Main Activity. Displays scrolling grid of poster from The MovieProvider DB's top 20 most popular movies.
 * Allows users to sort movies by user rating and selecting movies to get more details.
 */

class DiscoverRecyclerViewAdapter extends RecyclerView.Adapter<DiscoverRecyclerViewAdapter.CustomViewHolder> {

    // Get context, ListItemClickListener, and ArrayList<MovieProvider> variables
    private final Context mContext;
    private final ListItemClickListener mOnClickListener;
    private final ArrayList<Movie> movies;
    RecyclerView mRecyclerView;
    private int lastPosition = -1;


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    /**
     * Apply ListItemClickListener to the DiscoverRecyclerView-s
     */
    interface ListItemClickListener {
        void onListItemClick(int i, ImageView sharedImageView);
    }

    /**
     * Create viewholders to inflate for movie poster images implement onClickListeners
     */
    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;

        CustomViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.iv_movie_poster);
            view.setOnClickListener(this);
        }

        public void onClick(View v) {
            DiscoverRecyclerViewAdapter.this.mOnClickListener.onListItemClick(getAdapterPosition(), imageView);
        }
    }

    /**
     * Create DiscoverRecyclerViewAdapter constructor
     */
    DiscoverRecyclerViewAdapter(Context context, ArrayList<Movie> movies, ListItemClickListener listener) {
        this.movies = movies;
        this.mContext = context;
        this.mOnClickListener = listener;
    }
    /**
     * Inflate view for each poster list item
     */
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_list_item, viewGroup, false);
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int viewWidth = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewWidth,
                    viewWidth * 513 / 342);
            view.setLayoutParams(params);
        } else {
            int viewWidth = Resources.getSystem().getDisplayMetrics().widthPixels / 3;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewWidth,
                    viewWidth * 513 / 342);
            view.setLayoutParams(params);
        }
        return new CustomViewHolder(view);
    }

    /**
     * Bind images to viewHolders from images rendered by Picasso and url address Strings.
     */
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Picasso.with(this.mContext).load(MovieDBJsonUtils.IMAGE_BASE_URL_342 + this.movies.get(position).getmPosterPath()).into(holder.imageView);
        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);
        ViewCompat.setTransitionName(holder.imageView, String.valueOf(Movie.mId));    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition && position > 5)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(CustomViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.imageView.clearAnimation();
    }

    /**
     * Return count of ArrayList movies
     */
    public int getItemCount() {
        return this.movies.size();
    }

}