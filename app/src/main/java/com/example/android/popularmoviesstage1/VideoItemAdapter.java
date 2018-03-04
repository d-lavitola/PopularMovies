package com.example.android.popularmoviesstage1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by domeniclavitola on 2/20/18.
 */

public class VideoItemAdapter extends RecyclerView.Adapter<VideoItemAdapter.CustomViewHolder> {

    // Get context, ListItemClickListener, and ArrayList<MovieProvider> variables
    private final Context mContext;
    private final ArrayList<Video> videos;
    private final ListItemClickListener mOnClickListener;

    /**
     * Apply ListItemClickListener to the DiscoverRecyclerView-s
     */
    interface ListItemClickListener {
        void onListItemClick(int i);
    }

    /**
     * Create viewholders to inflate trailer thumbnails and implement click listener
     */
    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView imageView;
        final TextView textView;

        CustomViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.video_thumbnail_iv);
            this.textView = view.findViewById(R.id.thumbnail_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            VideoItemAdapter.this.mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    /**
     * Create DiscoverRecyclerViewAdapter constructor
     */
    VideoItemAdapter(Context context, ArrayList<Video> videos, ListItemClickListener clickListener) {
        this.videos = videos;
        this.mContext = context;
        this.mOnClickListener = clickListener;
    }

    /**
     * Inflate view for each poster list item
     */
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videos_list_item, viewGroup, false);
        return new CustomViewHolder(view);
    }

    /**
     * Bind images to viewHolders from images rendered by Picasso and url address Strings.
     */
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        String labelString = (position+1)+". "+videos.get(position).getName();
        holder.textView.setText(labelString);
        Picasso.with(this.mContext).load("http://img.youtube.com/vi/"+videos.get(position).getKey()+"/0.jpg").into(holder.imageView);
    }

    /**
     * Return count of ArrayList movies
     */
    public int getItemCount() {
        return this.videos.size();
    }
}
