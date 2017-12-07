package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.example.android.popularmovies.adapter.MovieRecyclerViewAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.SortType;
import com.example.android.popularmovies.utilities.Utils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.MovieRecyclerViewAdapterOnClickListener {

    private RecyclerView mMoviesList;
    private MovieRecyclerViewAdapter mMovieAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;


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
                if (popularString.equals(selectedItem)) {
                    new FetchMovieTask().execute(SortType.POPULAR);
                } else {
                    new FetchMovieTask().execute(SortType.TOP_RATED);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        return true;
    }

    @Override
    public void movieClicked(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(getResources().getString(R.string.movie_parcelable), movie);
        startActivity(intent);
    }

    private void showMovieListView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mMoviesList.setVisibility(View.INVISIBLE);
    }

    public class FetchMovieTask extends AsyncTask<SortType, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(SortType... params) {
            if (params.length == 0) {
                return null;
            }

            SortType sortType = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortType);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                return MovieJsonUtils.getSimpleWeatherStringsFromJson(jsonMovieResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMovieListView();
                mMovieAdapter.setMovies(movies);
            } else {
                showErrorMessage();
            }
        }
    }

}
