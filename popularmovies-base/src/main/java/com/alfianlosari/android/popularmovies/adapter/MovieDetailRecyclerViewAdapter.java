package com.alfianlosari.android.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfianlosari.android.popularmovies.R;
import com.alfianlosari.android.popularmovies.data.Header;
import com.alfianlosari.android.popularmovies.data.Movie;
import com.alfianlosari.android.popularmovies.data.Review;
import com.alfianlosari.android.popularmovies.data.Video;
import com.squareup.picasso.Picasso;

/**
 * Created by alfianlosari on 21/12/17.
 */

public class MovieDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Object[] mMovieData;
    private Context mContext;

    private static final int VIEW_TYPE_OVERVIEW = 0;
    private static final int VIEW_TYPE_VIDEO = 1;
    private static final int VIEW_TYPE_REVIEW = 2;
    private static final int VIEW_TYPE_HEADER = 3;

    private MovieDetailOnClickListener mListener;

    public MovieDetailRecyclerViewAdapter(@NonNull Context context, MovieDetailOnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setData(Object[] data) {
        mMovieData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;

        switch (viewType) {

            case VIEW_TYPE_OVERVIEW: {
                layoutId = R.layout.movie_overview_item;
                break;
            }

            case VIEW_TYPE_VIDEO: {
                layoutId = R.layout.movie_video_item;
                break;
            }

            case VIEW_TYPE_REVIEW: {
                layoutId = R.layout.movie_review_item;
                break;
            }

            case VIEW_TYPE_HEADER: {
                layoutId = R.layout.header_item;
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);

        switch (viewType) {

            case VIEW_TYPE_OVERVIEW: {
                return new MovieOverviewViewHolder(view);
            }

            case VIEW_TYPE_VIDEO: {
                return new MovieVideoViewHolder(view);
            }

            case VIEW_TYPE_REVIEW: {
                return new MovieReviewViewHolder(view);
            }

            case VIEW_TYPE_HEADER: {
                return new HeaderViewHolder(view);
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_OVERVIEW:
                MovieOverviewViewHolder overviewViewHolder = (MovieOverviewViewHolder) holder;
                Movie movie = (Movie) mMovieData[position];
                Picasso.with(mContext).load(movie.imagePath()).into(overviewViewHolder.mImageView);
                overviewViewHolder.mTitleTextView.setText(movie.originalTitle);
                String vote = mContext.getResources().getString(R.string.average_vote);
                String released = mContext.getResources().getString(R.string.release_date);

                overviewViewHolder.mVoteAverageTextView.setText(vote + " " + movie.voteAverage);
                overviewViewHolder.mReleaseDateTextView.setText(released + " " + movie.releaseDate);
                overviewViewHolder.mOverviewTextView.setText(movie.overview);
                if (movie.isFavorite) {
                    overviewViewHolder.mFavoriteButton.setText(mContext.getResources().getString(R.string.unset_favorite));
                } else {
                    overviewViewHolder.mFavoriteButton.setText(mContext.getResources().getString(R.string.set_favorite));
                }
                break;

            case VIEW_TYPE_REVIEW:
                MovieReviewViewHolder reviewViewHolder = (MovieReviewViewHolder) holder;
                Review review = (Review) mMovieData[position];
                reviewViewHolder.mAuthorTextView.setText(review.author);
                reviewViewHolder.mContentTextView.setText(review.content);
                break;

            case VIEW_TYPE_VIDEO:
                MovieVideoViewHolder videoViewHolder = (MovieVideoViewHolder) holder;
                Video video = (Video) mMovieData[position];
                videoViewHolder.mTitleTextView.setText(video.name);
                break;

            case VIEW_TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                Header header = (Header) mMovieData[position];
                headerViewHolder.mTitleTextView.setText(header.title);
                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Object object = mMovieData[position];
        if (object instanceof Movie) {
            return VIEW_TYPE_OVERVIEW;
        } else if (object instanceof Video) {
            return VIEW_TYPE_VIDEO;
        } else if (object instanceof Header){
            return VIEW_TYPE_HEADER;
        } else if (object instanceof Review) {
            return VIEW_TYPE_REVIEW;
        } else {
            throw new IllegalArgumentException("Invalid Object");
        }
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) {
            return 0;
        }
        return mMovieData.length;
    }


    public class MovieOverviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mTitleTextView;
        private TextView mVoteAverageTextView;
        private TextView mReleaseDateTextView;
        private TextView mOverviewTextView;
        private Button mFavoriteButton;

        public MovieOverviewViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_movie_detail);
            mTitleTextView = itemView.findViewById(R.id.tv_movie_detail_title);
            mVoteAverageTextView = itemView.findViewById(R.id.tv_movie_detail_user_rating);
            mReleaseDateTextView = itemView.findViewById(R.id.tv_movie_detail_release_date);
            mOverviewTextView = itemView.findViewById(R.id.tv_movie_detail_overview);
            mFavoriteButton = itemView.findViewById(R.id.button_favorite);
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.toggleFavoriteMovieClicked();
                }
            });
        }
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView mAuthorTextView;
        private TextView mContentTextView;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = itemView.findViewById(R.id.reviewAuthorTextView);
            mContentTextView = itemView.findViewById(R.id.reviewTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Review review = (Review) mMovieData[position];
                    mListener.onClickReview(review);
                }
            });
        }
    }

    public class MovieVideoViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;

        public MovieVideoViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.videoTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Video video = (Video) mMovieData[position];
                    mListener.onClickVideo(video);
                }
            });

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }

    public interface MovieDetailOnClickListener {
        void toggleFavoriteMovieClicked();
        void onClickVideo(Video video);
        void onClickReview(Review review);
    }


}
