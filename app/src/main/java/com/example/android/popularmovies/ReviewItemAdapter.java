package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import Utilities.ExpandableTextView;

/**
 * Created by domeniclavitola on 2/20/18.
 */

public class ReviewItemAdapter extends RecyclerView.Adapter<ReviewItemAdapter.CustomViewHolder> {

    public interface ReviewsListClickListener {

        void onClick(View view, int position);
    }

    // Get context, ListItemClickListener, and ArrayList<MovieProvider> variables
    private static final String LOG_TAG = ReviewItemAdapter.class.getName();
    private final Context mContext;
    private final ArrayList<Review> reviews;
    private final ReviewsListClickListener listener;

    /**
     *
     * Create viewholders to inflate trailer thumbnails and implement click reviewsListClickListener
     */
    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView usernameTextView;
        private ReviewsListClickListener mListener;
        final ExpandableTextView contentTextView;

        CustomViewHolder(View view, ReviewsListClickListener listener) {
            super(view);
            this.usernameTextView = view.findViewById(R.id.review_username_tv);
            this.contentTextView = view.findViewById(R.id.review_content_tv);
            this.mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
             mListener.onClick(view, getAdapterPosition());
        }
    }

    /**
     * Create ReviewItemAdapter constructor
     */
    ReviewItemAdapter(Context context, ArrayList<Review> reviews, ReviewsListClickListener listener) {
        this.reviews = reviews;
        this.mContext = context;
        this.listener = listener;
    }

    /**
     * Inflate view for each review list item
     */
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_list_item, viewGroup, false);
        return new CustomViewHolder(view, listener);
    }

    /**
     * Bind TextViews for Username and Content
     */
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.usernameTextView.setText(reviews.get(position).getAuthor());
        holder.contentTextView.setText(reviews.get(position).getContent());
    }

    /**
     * Return count of ArrayList movies
     */
    public int getItemCount() {
        return this.reviews.size();
    }
}
