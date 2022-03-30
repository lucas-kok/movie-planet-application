package com.pekict.movieplanet.presentation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.review.Review;
import com.pekict.movieplanet.logic.MovieListAdapter;
import com.pekict.movieplanet.logic.ReviewListAdapter;
import com.pekict.movieplanet.presentation.viewmodels.ReviewViewModel;
import com.squareup.picasso.Picasso;

public class MovieViewActivity extends AppCompatActivity {
    private static final String TAG_NAME = MovieViewActivity.class.getSimpleName();
    private static final String REVIEWS = "REVIEW";

    private ReviewViewModel mReviewViewModel;
    private Bundle mSavedInstanceState;

    private int mMovieId;

    private RecyclerView mRecyclerView;
    private ReviewListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        mReviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);
        mReviewViewModel.getReviews().observe(this, this::displayReviews);

        mRecyclerView = findViewById(R.id.recyclerview_reviews);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ActionBar actionBar = getSupportActionBar();

        ImageView movieImage = findViewById(R.id.iv_mv);
        TextView popularityText = findViewById(R.id.tv_mv_popularity);
        TextView voteCountText = findViewById(R.id.tv_mv_vote_count);
        TextView voteAverageText = findViewById(R.id.tv_mv_vote_average);
        TextView titleText = findViewById(R.id.tv_mv_title);
        TextView originalLanguageText = findViewById(R.id.tv_mv_original_language);

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Loading in the meal the user clicked
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getParcelableExtra("bundle");
            Movie movie = bundle.getParcelable("movieObj");

            if (movie == null) {
                Log.e(TAG_NAME, "Meal is empty");
                return;
            }


            Log.d(TAG_NAME, "Opening Movie: " + movie.getTitle());

            // Getting the Movies values
            mMovieId = movie.getId();
            String imageURL = movie.getSmallImageURL();
            String movieTitle = movie.getTitle();
            String originalLanguage = movie.getOriginal_language();
            double popularity = movie.getPopularity();
            int voteCount = movie.getVote_count();
            double voteAverage = movie.getVote_average();

            // Changing the UI elements to the Movies values
            actionBar.setSubtitle(movieTitle);

            Picasso.get().load(imageURL).into(movieImage);
            titleText.setText(movieTitle);
            originalLanguageText.setText(getString(R.string.label_tv_mv_original_language, originalLanguage));
            popularityText.setText(getString(R.string.label_tv_mv_popularity, popularity));
            voteCountText.setText(getString(R.string.label_tv_mv_vote_count, voteCount));
            voteAverageText.setText(getString(R.string.label_tv_mv_vote_average, voteAverage));
        }

        mSavedInstanceState = savedInstanceState;

        loadReviews(mMovieId);
    }

    public void loadReviews(int movieId) {
        if (mSavedInstanceState != null) {
            displayReviews((Review[])mSavedInstanceState.getParcelableArray(REVIEWS));
            Log.d(TAG_NAME, "Meals fetched with savedInstance.");

            return;
        }

        boolean hasInternet = isNetworkAvailable();
        Log.i(TAG_NAME, "User has internet is: " + hasInternet);

        mReviewViewModel.fetchReviews(hasInternet, movieId);
        Log.d(TAG_NAME, "Meals fetched with ViewModel.");
    }

    public void displayReviews(Review[] reviews) {
        Log.d(TAG_NAME, "Reviews found: " + reviews.length);
        mAdapter = new ReviewListAdapter(this, reviews);
        mRecyclerView.setAdapter(mAdapter);
    }

    // Function that returns if the user has a internet-connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Function that's called when back-button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArray(REVIEWS, mReviewViewModel.getReviews().getValue());
        super.onSaveInstanceState(outState);
    }
}