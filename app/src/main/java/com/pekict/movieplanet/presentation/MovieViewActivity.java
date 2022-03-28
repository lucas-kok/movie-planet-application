package com.pekict.movieplanet.presentation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.Movie;
import com.squareup.picasso.Picasso;

public class MovieViewActivity extends AppCompatActivity {
    private static final String TAG_NAME = MovieViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

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
            String imageURL = movie.getImageURL();
            String movieTitle = movie.getTitle();
            String originalLanguage = movie.getOriginalLanguage();
            double popularity = movie.getPopularity();
            int voteCount = movie.getVoteCount();
            double voteAverage = movie.getVoteAverage();

            // Changing the UI elements to the Meals values
            actionBar.setSubtitle(movieTitle);

            Picasso.get().load(imageURL).into(movieImage);
            titleText.setText(getString(R.string.label_tv_mv_title, movieTitle));
            originalLanguageText.setText(getString(R.string.label_tv_mv_original_language, originalLanguage));
            popularityText.setText(getString(R.string.label_tv_mv_popularity, popularity));
            voteCountText.setText(getString(R.string.label_tv_mv_vote_count, voteCount));
            voteAverageText.setText(getString(R.string.label_tv_mv_vote_average, voteAverage));
        }
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
}