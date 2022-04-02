package com.pekict.movieplanet.presentation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.review.Review;
import com.pekict.movieplanet.domain.trailer.Trailer;
import com.pekict.movieplanet.logic.ReviewListAdapter;
import com.pekict.movieplanet.logic.TrailerFilter;
import com.pekict.movieplanet.presentation.viewmodels.ReviewViewModel;
import com.pekict.movieplanet.presentation.viewmodels.TrailerViewModel;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

public class MovieViewActivity extends AppCompatActivity {
    private static final String TAG_NAME = MovieViewActivity.class.getSimpleName();
    private static final String VIDEOKEY = "VIDEOKEY";
    private static final String REVIEWS = "REVIEW";

    private View mOverlayView;
    private Button mTrailerButton;

    private ReviewViewModel mReviewViewModel;
    private TrailerViewModel mTrailerViewModel;
    private Bundle mSavedInstanceState;

    private String mVideoKey;
    private int mMovieId;

    private RecyclerView mRecyclerView;
    private ReviewListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        mReviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);
        mReviewViewModel.getReviews().observe(this, this::displayReviews);

        mTrailerViewModel = ViewModelProviders.of(this).get(TrailerViewModel.class);
        mTrailerViewModel.getTrailers().observe(this, trailers -> {
            if (getBestTrailer(trailers) == null) return;

            mVideoKey = getBestTrailer(trailers).getKey();
            showTrailerButton();
        });

        mRecyclerView = findViewById(R.id.recyclerview_reviews);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ActionBar actionBar = getSupportActionBar();

        LinearLayout movieViewLayout = findViewById(R.id.ll_mv);
        mOverlayView = findViewById(R.id.overlay_view);

        ImageView movieImage = findViewById(R.id.iv_mv);
        mTrailerButton = findViewById(R.id.btn_trailer);
        mTrailerButton.setOnClickListener(view -> showTrailerPopup());

        TextView titleText = findViewById(R.id.tv_mv_title);
        TextView originalLanguageText = findViewById(R.id.tv_mv_original_language);
        TextView overviewText = findViewById(R.id.tv_mv_overview);
        TextView popularityText = findViewById(R.id.tv_mv_popularity);
        TextView voteCountText = findViewById(R.id.tv_mv_vote_count);
        TextView voteAverageText = findViewById(R.id.tv_mv_vote_average);

        ImageButton expandReviewsButton = findViewById(R.id.btn_expand_reviews);
        expandReviewsButton.setOnClickListener(view -> {
            if (mRecyclerView.getVisibility() == View.VISIBLE) {
                // TransitionManager will smooth out the showing/disappearing of the Reviews
                TransitionManager.beginDelayedTransition(movieViewLayout,
                        new AutoTransition());
                mRecyclerView.setVisibility(View.GONE);
                expandReviewsButton.setImageResource(R.drawable.ic_expand);
            } else {
                TransitionManager.beginDelayedTransition(movieViewLayout,
                        new AutoTransition());
                mRecyclerView.setVisibility(View.VISIBLE);
                expandReviewsButton.setImageResource(R.drawable.ic_collapse);
            }
        });

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
            String overview = movie.getOverview();
            double popularity = movie.getPopularity();
            int voteCount = movie.getVote_count();
            double voteAverage = movie.getVote_average();

            // Changing the UI elements to the Movies values
            actionBar.setSubtitle(movieTitle);

            Picasso.get().load(imageURL).into(movieImage);
            titleText.setText(movieTitle);
            originalLanguageText.setText(getString(R.string.label_tv_mv_original_language, originalLanguage));
            overviewText.setText(overview);
            popularityText.setText(getString(R.string.label_tv_mv_popularity, popularity));
            voteCountText.setText(getString(R.string.label_tv_mv_vote_count, voteCount));
            voteAverageText.setText(getString(R.string.label_tv_mv_vote_average, voteAverage));
        }

        mSavedInstanceState = savedInstanceState;

        loadTrailers(mMovieId);
        loadReviews(mMovieId);
    }

    private void showTrailerButton() {
        mTrailerButton.setVisibility(View.VISIBLE);
    }

    private void loadTrailers(int movieId) {
        if (mSavedInstanceState != null) {
            mVideoKey = mSavedInstanceState.getString(VIDEOKEY);
            showTrailerButton();

            Log.d(TAG_NAME, "Trailer fetched with savedInstance.");
            return;
        }

        boolean hasInternet = isNetworkAvailable();
        if (!hasInternet) return;

        mTrailerViewModel.fetchTrailers(movieId);
        Log.d(TAG_NAME, "Trailer fetched with ViewModel.");
    }

    private Trailer getBestTrailer(Trailer[] trailers) {
        if (TrailerFilter.getBestTrailer(trailers) == null) return null;

        Log.d(TAG_NAME, "Best video key: " + TrailerFilter.getBestTrailer(trailers).getKey());
        return TrailerFilter.getBestTrailer(trailers);
    }

    private void showTrailerPopup() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.trailer, null);
        YouTubePlayerView playerView = popupView.findViewById(R.id.youtube_player_view);
        playerView.addYouTubePlayerListener(new YouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(mVideoKey, 0);
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState playerState) {

            }

            @Override
            public void onPlaybackQualityChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError playerError) {
                youTubePlayer.cueVideo(mVideoKey, 0);
            }

            @Override
            public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoLoadedFraction(@NonNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(@NonNull YouTubePlayer youTubePlayer, @NonNull String s) {

            }

            @Override
            public void onApiChange(@NonNull YouTubePlayer youTubePlayer) {

            }
        });

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setOnDismissListener(() -> mOverlayView.setVisibility(View.GONE));

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(mTrailerButton.getRootView(), Gravity.CENTER, 0, 0);
        mOverlayView.setVisibility(View.VISIBLE);
    }

    public void loadReviews(int movieId) {
        if (mSavedInstanceState != null) {
            displayReviews((Review[]) mSavedInstanceState.getParcelableArray(REVIEWS));
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
        outState.putString(VIDEOKEY, mVideoKey);
        super.onSaveInstanceState(outState);
    }
}