package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by alfianlosari on 21/12/17.
 */

public class MovieCursorRecyclerViewAdapter extends RecyclerView.Adapter<MovieCursorRecyclerViewAdapter.MovieViewHolder> {

    final private MovieCursorAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;

    public MovieCursorRecyclerViewAdapter(MovieCursorAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.movie_item, parent, false);
        return new MovieCursorRecyclerViewAdapter.MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String movieId = mCursor.getString(MainActivity.INDEX_MOVIE_ID);
        String title = mCursor.getString(MainActivity.INDEX_MOVIE_TITLE);
        String posterPath = mCursor.getString(MainActivity.INDEX_MOVIE_POSTER_PATH);
        String originalTitle = mCursor.getString(MainActivity.INDEX_MOVIE_ORIGINAL_TITLE);
        double voteAverage = mCursor.getDouble(MainActivity.INDEX_MOVIEW_VOTE_AVERAGE);
        double popularity = mCursor.getDouble(MainActivity.INDEX_MOVIE_POPULARITY);
        String overview = mCursor.getString(MainActivity.INDEX_MOVIE_OVERVIEW);
        String releaseDate = mCursor.getString(MainActivity.INDEX_MOVIEW_RELEASE_DATE);
        String posterPathURL = Utils.posterPathURL(posterPath);

        Movie movie = new Movie(movieId, title, posterPath, overview, releaseDate, originalTitle, voteAverage, popularity);
        Picasso.with(holder.itemView.getContext()).load(posterPathURL).into(holder.mMovieImageView);
        holder.itemView.setTag(movie);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null && newCursor == null) {
            mCursor.close();
        }
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface MovieCursorAdapterOnClickHandler {
        void onClickMovie(Movie movie);
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
            Movie movie = (Movie) view.getTag();
            mClickHandler.onClickMovie(movie);

        }
    }

}
