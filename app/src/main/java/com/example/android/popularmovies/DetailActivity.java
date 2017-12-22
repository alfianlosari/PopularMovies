package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.adapter.MovieDetailRecyclerViewAdapter;
import com.example.android.popularmovies.data.Header;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity implements MovieDetailRecyclerViewAdapter.MovieDetailOnClickListener {


    private Movie mMovie;
    private RecyclerView mMovieDataList;
    private MovieDetailRecyclerViewAdapter mAdapter;


    private static final int ID_FETCH_REVIEWS_LOADER = 203;
    private static final int ID_FETCH_VIDEOS_LOADER = 204;

    private Video[] mVideos;
    private Review[] mReviews;

    private Object[] generateMovieData() {
        int videoCount = 1;
        if (mVideos != null) {
            videoCount += mVideos.length;
        }

        int reviewCount = 1;
        if (mReviews != null) {
            reviewCount += mReviews.length;
        }

        Object[] data = new Object[1 + videoCount + reviewCount];
        data[0] = mMovie;
        int index = 1;

        if (mVideos != null && mVideos.length > 0) {
            Header header = new Header(getResources().getString(R.string.trailer_title));
            data[index] = header;
            index++;
            for (Video video: mVideos) {
                data[index] = video;
                index++;
            }
        } else {
            Header header = new Header(getResources().getString(R.string.no_trailer_title));
            data[index] = header;
            index++;
        }

        if (mReviews != null && mReviews.length > 0) {
            Header header = new Header(getResources().getString(R.string.review_title));
            data[index] = header;
            index++;
            for (Review review: mReviews) {
                data[index] = review;
                index++;
            }
        } else {
            Header header = new Header(getResources().getString(R.string.no_review_title));
            data[index] = header;
            index++;
        }

        return data;
    }

    private LoaderManager.LoaderCallbacks<Review[]> mReviewsAsyncTaskLoaderCallback = new LoaderManager.LoaderCallbacks<Review[]>() {

        @Override
        public Loader<Review[]> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<Review[]>(DetailActivity.this) {
                Review[] mReviews;

                @Override
                protected void onStartLoading() {
                    if (mMovie == null) {
                        return;
                    }

                    if (mReviews != null) {
                        deliverResult(mReviews);
                    } else {
                        forceLoad();
                    }
                }

                @Nullable
                @Override
                public Review[] loadInBackground() {
                    URL movieReviewsURL = NetworkUtils.buildUrlForMovieReview(mMovie.id);
                    try {
                        String reviewsMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieReviewsURL);
                        return MovieJsonUtils.getReviewsFromJSON(reviewsMovieResponse);
                    } catch(Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(@Nullable Review[] data) {
                    mReviews = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Review[]> loader, Review[] data) {
            mReviews = data;
            mAdapter.setData(generateMovieData());
        }

        @Override
        public void onLoaderReset(Loader<Review[]> loader) {

        }
    };


    private LoaderManager.LoaderCallbacks<Video[]> mVideosAsynTaskLoaderCallback = new LoaderManager.LoaderCallbacks<Video[]>() {
        @Override
        public Loader<Video[]> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Video[]>(DetailActivity.this) {
                Video[] mVideos;

                @Override
                protected void onStartLoading() {
                    if (mMovie == null) {
                        return;
                    }

                    if (mVideos != null) {
                         deliverResult(mVideos);
                    } else {
                        forceLoad();
                    }
                }

                @Nullable
                @Override
                public Video[] loadInBackground() {
                    URL moviesVideosURL = NetworkUtils.buildUrlForMovieVideos(mMovie.id);
                    try {
                        String videosMovieResponse = NetworkUtils.getResponseFromHttpUrl(moviesVideosURL);
                        return MovieJsonUtils.getVideosFromJSON(videosMovieResponse);
                    } catch(Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(@Nullable Video[] data) {
                    mVideos = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Video[]> loader, Video[] data) {
            mVideos = data;
            mAdapter.setData(generateMovieData());
        }

        @Override
        public void onLoaderReset(Loader<Video[]> loader) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getResources().getString(R.string.movie_parcelable));
        mMovieDataList = findViewById(R.id.rv_movie_data);


        if (movie != null) {
            mMovie = movie;
            Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.buildMovieUriWithId(movie.id), null, null, null, null);
            if (cursor != null && cursor.getCount() == 1) {
                mMovie.isFavorite = true;
            } else {
                mMovie.isFavorite = false;
            }
            cursor.close();

            getSupportActionBar().setTitle(movie.title);

            mMovieDataList.setHasFixedSize(false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mMovieDataList.setLayoutManager(layoutManager);
            mAdapter = new MovieDetailRecyclerViewAdapter(this, this);
            mMovieDataList.setAdapter(mAdapter);
            mAdapter.setData(generateMovieData());

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.restartLoader(ID_FETCH_REVIEWS_LOADER, null, mReviewsAsyncTaskLoaderCallback);
            loaderManager.restartLoader(ID_FETCH_VIDEOS_LOADER, null, mVideosAsynTaskLoaderCallback);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void toggleFavoriteMovieClicked() {
        if (mMovie.isFavorite) {
            getContentResolver().delete(MovieContract.MovieEntry.buildMovieUriWithId(mMovie.id), null, null);
        } else {
            ContentValues contentValues = mMovie.contentValues();
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        }

        mMovie.isFavorite = !mMovie.isFavorite;
        mAdapter.notifyItemChanged(0);
    }

    @Override
    public void onClickVideo(Video video) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.youtubeURL()));
        startActivity(intent);

    }

    @Override
    public void onClickReview(Review review) {

    }
}
