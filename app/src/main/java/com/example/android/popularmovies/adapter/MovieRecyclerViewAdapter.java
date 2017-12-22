package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by alfianlosari on 04/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MovieViewHolder> {

    private Movie[] mMovies;
    private final MovieRecyclerViewAdapterOnClickListener mListener;

    public MovieRecyclerViewAdapter(MovieRecyclerViewAdapterOnClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Picasso.with(holder.itemView.getContext()).load(mMovies[position].imagePath()).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.length;
    }

    public void setMovies(Movie[] movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMovieImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mMovieImageView = itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movie = mMovies[position];
            mListener.onClickMovie(movie);
        }
    }

    public interface MovieRecyclerViewAdapterOnClickListener {
        void onClickMovie(Movie movie);

    }

}
