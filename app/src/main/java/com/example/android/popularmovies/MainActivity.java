package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.adapter.MovieCursorRecyclerViewAdapter;
import com.example.android.popularmovies.adapter.MovieRecyclerViewAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.SortType;
import com.example.android.popularmovies.utilities.Utils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        MovieRecyclerViewAdapter.MovieRecyclerViewAdapterOnClickListener,
        MovieCursorRecyclerViewAdapter.MovieCursorAdapterOnClickHandler{

    private RecyclerView mMoviesList;
    private MovieRecyclerViewAdapter mMovieAdapter;
    private MovieCursorRecyclerViewAdapter mFavoriteMoviewAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;

    private static final String SORT_TYPE_KEY = "SORT_TYPE_KEY";
    private static final int ID_FETCH_MOVIES_LOADER = 201;
    private static final int ID_FAVORITES_MOVIE_LOADER = 202;

    public static final String[] MAIN_MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;
    public static final int INDEX_MOVIE_ORIGINAL_TITLE = 3;
    public static final int INDEX_MOVIEW_VOTE_AVERAGE = 4;
    public static final int INDEX_MOVIE_POPULARITY = 5;
    public static final int INDEX_MOVIE_OVERVIEW = 6;
    public static final int INDEX_MOVIEW_RELEASE_DATE = 7;

    private LoaderManager.LoaderCallbacks<Movie[]> mMoviesAsyncTaskLoaderCallbacks = new LoaderManager.LoaderCallbacks<Movie[]>() {
        @Override
        public Loader<Movie[]> onCreateLoader(int id,final Bundle args) {
            switch (id) {
                case ID_FETCH_MOVIES_LOADER:
                    return new AsyncTaskLoader<Movie[]>(MainActivity.this) {
                        Movie[] mMovies;

                        @Override
                        protected void onStartLoading() {
                            if (args == null) {
                                return;
                            }
                            if (mMovies != null) {
                                deliverResult(mMovies);
                            } else {
                                mProgressBar.setVisibility(View.VISIBLE);
                                forceLoad();
                            }
                        }

                        @Nullable
                        @Override
                        public Movie[] loadInBackground() {
                            int sortNumber = args.getInt(SORT_TYPE_KEY, 0);
                            SortType sortType = SortType.valueOf(sortNumber);
                            URL movieRequestUrl = NetworkUtils.buildUrl(sortType);

                            try {
                                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                                return MovieJsonUtils.getMoviesFromJson(jsonMovieResponse);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        public void deliverResult(@Nullable Movie[] data) {
                            mMovies = data;
                            super.deliverResult(data);
                        }
                    };

                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);

            }

        }

        @Override
        public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (data != null) {
                showMovieListView();
                mMovieAdapter.setMovies(data);
                mMoviesList.setAdapter(mMovieAdapter);
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<Movie[]> loader) {
            mFavoriteMoviewAdapter.swapCursor(null);
        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> mCursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case ID_FAVORITES_MOVIE_LOADER:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mMoviesList.setAdapter(null);
                    Uri favouriteMoviesUri = MovieContract.MovieEntry.CONTENT_URI;
                    String sortOrder = MovieContract.MovieEntry.COLUMN_TITLE + " ASC";
                    return new CursorLoader(MainActivity.this,
                            favouriteMoviesUri,
                            MAIN_MOVIES_PROJECTION,
                            null,
                            null,
                            sortOrder
                            );

                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null || data.getCount() == 0) {
                showNoFavoritesMovieMessage();
            } else {
                showMovieListView();
                mFavoriteMoviewAdapter.swapCursor(data);
                mMoviesList.setAdapter(mFavoriteMoviewAdapter);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mProgressBar = findViewById(R.id.pb_loading);

        mMoviesList = findViewById(R.id.rv_movies);
        int numberOfColumns = Utils.calculateNumberOfColumns(MainActivity.this);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        mMoviesList.setLayoutManager(layoutManager);
        mMoviesList.setHasFixedSize(true);

        mMovieAdapter = new MovieRecyclerViewAdapter(MainActivity.this);
        mFavoriteMoviewAdapter = new MovieCursorRecyclerViewAdapter(MainActivity.this);

        mMoviesList.setAdapter(mMovieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        MenuItem menuItem = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) menuItem.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.sort_types, R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString().toLowerCase();
                String popularString = getResources().getString(R.string.sort_popular).toLowerCase();
                String topRatedString = getResources().getString(R.string.sort_top_rated).toLowerCase();

                LoaderManager loaderManager = getSupportLoaderManager();
                Bundle bundle = new Bundle();
                if (popularString.equals(selectedItem) || topRatedString.equals(selectedItem)) {
                    loaderManager.destroyLoader(ID_FAVORITES_MOVIE_LOADER);
                    if (popularString.equals(selectedItem)) {
                        bundle.putInt(SORT_TYPE_KEY, SortType.POPULAR.getValue());
                    } else {
                        bundle.putInt(SORT_TYPE_KEY, SortType.TOP_RATED.getValue());
                    }
                    loaderManager.restartLoader(ID_FETCH_MOVIES_LOADER, bundle, mMoviesAsyncTaskLoaderCallbacks);

                } else {
                    loaderManager.destroyLoader(ID_FETCH_MOVIES_LOADER);
                    loaderManager.restartLoader(ID_FAVORITES_MOVIE_LOADER, null, mCursorLoaderCallbacks);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        return true;
    }

    private void showMovieListView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageTextView.setText(getResources().getString(R.string.error_message));
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mMoviesList.setVisibility(View.INVISIBLE);
    }

    private void showNoFavoritesMovieMessage() {
        mErrorMessageTextView.setText(getResources().getString(R.string.no_favorites_message));
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mMoviesList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClickMovie(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(getResources().getString(R.string.movie_parcelable), movie);
        startActivity(intent);
    }

}
