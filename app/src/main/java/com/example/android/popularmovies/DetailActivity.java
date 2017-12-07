package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mVoteAverageTextView;
    private TextView mReleaseDateTextView;
    private TextView mOverviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getResources().getString(R.string.movie_parcelable));

        mImageView = findViewById(R.id.iv_movie_detail);
        mTitleTextView = findViewById(R.id.tv_movie_detail_title);
        mVoteAverageTextView = findViewById(R.id.tv_movie_detail_user_rating);
        mReleaseDateTextView = findViewById(R.id.tv_movie_detail_release_date);
        mOverviewTextView = findViewById(R.id.tv_movie_detail_overview);

        if (movie != null) {
            getSupportActionBar().setTitle(movie.title);
            Picasso.with(DetailActivity.this).load(movie.imagePath()).into(mImageView);
            mTitleTextView.setText(movie.originalTitle);
            String vote = getResources().getString(R.string.average_vote);
            String released = getResources().getString(R.string.release_date);

            mVoteAverageTextView.setText(vote + movie.voteAverage);
            mReleaseDateTextView.setText(released + movie.releaseDate);
            mOverviewTextView.setText(movie.overview);
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
}
