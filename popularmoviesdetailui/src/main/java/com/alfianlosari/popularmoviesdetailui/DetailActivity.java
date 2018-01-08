package com.alfianlosari.popularmoviesdetailui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.alfianlosari.android.popularmovies.adapter.MovieDetailRecyclerViewAdapter;
import com.alfianlosari.android.popularmovies.data.Header;
import com.alfianlosari.android.popularmovies.data.Movie;
import com.alfianlosari.android.popularmovies.data.MovieContract;
import com.alfianlosari.android.popularmovies.data.Review;
import com.alfianlosari.android.popularmovies.data.Video;
import com.alfianlosari.android.popularmovies.utilities.MovieJsonUtils;
import com.alfianlosari.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class DetailActivity extends AppCompatActivity implements MovieDetailRecyclerViewAdapter.MovieDetailOnClickListener {

    private Movie mMovie;
    private String mMovieId;
    private RecyclerView mMovieDataList;
    private MovieDetailRecyclerViewAdapter mAdapter;

    private static final String MOVIE_SAVE_INSTANCE_KEY = "movie";
    private static final String REVIEWS_SAVE_INSTANCE_KEY = "reviews";
    private static final String VIDEOS_SAVE_INSTANCE_KEY = "videos";

    private static final int ID_FETCH_REVIEW_DETAIL_LOADER = 202;
    private static final int ID_FETCH_REVIEWS_LOADER = 203;
    private static final int ID_FETCH_VIDEOS_LOADER = 204;

    private Video[] mVideos;
    private Review[] mReviews;

    private Object[] generateMovieData() {
        int movieCount = 0;
        if (mMovie != null) {
            movieCount += 1;
        }

        int videoCount = 1;
        if (mVideos != null) {
            videoCount += mVideos.length;
        }

        int reviewCount = 1;
        if (mReviews != null) {
            reviewCount += mReviews.length;
        }

        Object[] data = new Object[movieCount + videoCount + reviewCount];
        int index = 0;
        if (mMovie != null) {
            data[0] = mMovie;
            index ++;
        }

        if (mVideos != null && mVideos.length > 0) {
            Header header = new Header(getResources().getString(com.alfianlosari.android.popularmovies.R.string.trailer_title));
            data[index] = header;
            index++;
            for (Video video: mVideos) {
                data[index] = video;
                index++;
            }
        } else {
            Header header = new Header(getResources().getString(com.alfianlosari.android.popularmovies.R.string.no_trailer_title));
            data[index] = header;
            index++;
        }

        if (mReviews != null && mReviews.length > 0) {
            Header header = new Header(getResources().getString(com.alfianlosari.android.popularmovies.R.string.review_title));
            data[index] = header;
            index++;
            for (Review review: mReviews) {
                data[index] = review;
                index++;
            }
        } else {
            Header header = new Header(getResources().getString(com.alfianlosari.android.popularmovies.R.string.no_review_title));
            data[index] = header;
            index++;
        }
        return data;
    }

    private LoaderManager.LoaderCallbacks<Movie> mMovieDetailAsyncTaskLoaderCallback = new LoaderManager.LoaderCallbacks<Movie>() {
        @Override
        public Loader<Movie> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Movie>(DetailActivity.this) {

                Movie mMovie;

                @Override
                protected void onStartLoading() {
                    if (mMovie != null) {
                        deliverResult(mMovie);
                    } else {
                        forceLoad();
                    }
                }

                @Nullable
                @Override
                public Movie loadInBackground() {
                    URL movieDetailURL = NetworkUtils.buildURLForMovieDetail(mMovieId);
                    try {
                        String movieDetailJsonResponse = NetworkUtils.getResponseFromHttpUrl(movieDetailURL);
                        Movie movie = MovieJsonUtils.getMovieFromJson(movieDetailJsonResponse);
                        return movie;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(@Nullable Movie data) {
                    this.mMovie = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Movie> loader, Movie data) {
            mMovie = data;
            mMovie.isFavorite = false;
            mAdapter.setData(generateMovieData());
        }

        @Override
        public void onLoaderReset(Loader<Movie> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Review[]> mReviewsAsyncTaskLoaderCallback = new LoaderManager.LoaderCallbacks<Review[]>() {

        @Override
        public Loader<Review[]> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<Review[]>(DetailActivity.this) {
                Review[] mReviews;

                @Override
                protected void onStartLoading() {
                    if (mMovieId == null) {
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
                    URL movieReviewsURL = NetworkUtils.buildUrlForMovieReview(mMovieId);
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


    private LoaderManager.LoaderCallbacks<Video[]> mVideosAsyncTaskLoaderCallback = new LoaderManager.LoaderCallbacks<Video[]>() {
        @Override
        public Loader<Video[]> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Video[]>(DetailActivity.this) {
                Video[] mVideos;

                @Override
                protected void onStartLoading() {
                    if (mMovieId == null) {
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
                    URL moviesVideosURL = NetworkUtils.buildUrlForMovieVideos(mMovieId);
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
        setContentView(com.alfianlosari.android.popularmovies.R.layout.activity_detail);
        mMovieDataList = findViewById(com.alfianlosari.android.popularmovies.R.id.rv_movie_data);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Uri dataURI = intent.getData();
        mMovieId = dataURI.getLastPathSegment();

        if (mMovieId != null) {
            mMovieDataList.setHasFixedSize(false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mMovieDataList.setLayoutManager(layoutManager);
            mAdapter = new MovieDetailRecyclerViewAdapter(this, this);
            mMovieDataList.setAdapter(mAdapter);

            if (savedInstanceState == null) {
                LoaderManager loaderManager = getSupportLoaderManager();
                loaderManager.restartLoader(ID_FETCH_REVIEW_DETAIL_LOADER, null, mMovieDetailAsyncTaskLoaderCallback);
                loaderManager.restartLoader(ID_FETCH_REVIEWS_LOADER, null, mReviewsAsyncTaskLoaderCallback);
                loaderManager.restartLoader(ID_FETCH_VIDEOS_LOADER, null, mVideosAsyncTaskLoaderCallback);
            } else {
                mMovie = (Movie) savedInstanceState.getSerializable(MOVIE_SAVE_INSTANCE_KEY);
                mVideos = (Video[]) savedInstanceState.getSerializable(VIDEOS_SAVE_INSTANCE_KEY);
                mReviews = (Review[]) savedInstanceState.getSerializable(REVIEWS_SAVE_INSTANCE_KEY);
            }

            Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.buildMovieUriWithId(mMovieId), null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                mMovie = new Movie(cursor);
                mMovie.isFavorite = true;
                cursor.close();
                getSupportActionBar().setTitle(mMovie.title);
            }

            mAdapter.setData(generateMovieData());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(VIDEOS_SAVE_INSTANCE_KEY, mVideos);
        outState.putSerializable(REVIEWS_SAVE_INSTANCE_KEY, mReviews);
        outState.putSerializable(MOVIE_SAVE_INSTANCE_KEY, mMovie);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == android.support.v7.appcompat.R.id.home) {
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
