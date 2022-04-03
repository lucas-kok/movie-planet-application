package com.pekict.movieplanet.presentation;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
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
    private boolean isFavorite = false;
    private boolean isWatched = false;
    // TODO: Begin te implementeren dat deze ook gwn opgeslagen wordt
    // TODO: Begin share button ook te laten werken
    // TODO: Begin print button te laten werken
    private Movie mMovie;

    private View mOverlayView;
    private Button mTrailerButton;
    private ImageButton mExpandReviewsButton;
    private TextView mNoReviewsText;

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
        mReviewViewModel.getReviews().observe(this, reviews -> {
            displayReviews(reviews);
        });

        mTrailerViewModel = ViewModelProviders.of(this).get(TrailerViewModel.class);
        mTrailerViewModel.getTrailers().observe(this, trailers -> {
            if (getBestTrailer(trailers) == null) return;

            mVideoKey = getBestTrailer(trailers).getKey();
            showTrailerButton();
        });

        mRecyclerView = findViewById(R.id.recyclerview_reviews);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
        TextView genreText = findViewById(R.id.tv_mv_genre);

        mExpandReviewsButton = findViewById(R.id.btn_expand_reviews);
        mExpandReviewsButton.setOnClickListener(view -> {
            if (mRecyclerView.getVisibility() == View.VISIBLE) {
                // TransitionManager will smooth out the showing/disappearing of the Reviews
                TransitionManager.beginDelayedTransition(movieViewLayout,
                        new AutoTransition());
                mRecyclerView.setVisibility(View.GONE);
                mExpandReviewsButton.setImageResource(R.drawable.ic_expand);
            } else {
                TransitionManager.beginDelayedTransition(movieViewLayout,
                        new AutoTransition());
                mRecyclerView.setVisibility(View.VISIBLE);
                mExpandReviewsButton.setImageResource(R.drawable.ic_collapse);
            }
        });

        mNoReviewsText = findViewById(R.id.tv_mv_no_reviews);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Loading in the meal the user clicked
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getParcelableExtra("bundle");
            mMovie = bundle.getParcelable("movieObj");

            if (mMovie == null) {
                Log.e(TAG_NAME, "Meal is empty");
                return;
            }

            Log.d(TAG_NAME, "Opening Movie: " + mMovie.getTitle());

            // Getting the Movies values
            mMovieId = mMovie.getId();
            String imageURL = mMovie.getSmallImageURL();
            String movieTitle = mMovie.getTitle();
            String originalLanguage = mMovie.getOriginal_language();
            String overview = mMovie.getOverview();
            double popularity = mMovie.getPopularity();
            int voteCount = mMovie.getVote_count();
            double voteAverage = mMovie.getVote_average();
            String genres = mMovie.getGenres(mMovie.getGenre_ids());

            // Changing the UI elements to the Movies values
            actionBar.setSubtitle(movieTitle);

            if (imageURL.equals("https://image.tmdb.org/t/p/w500null")) {
                movieImage.setImageResource(R.drawable.placeholder);
            } else {
                Picasso.get().load(imageURL).into(movieImage);
            }
            titleText.setText(movieTitle);
            originalLanguageText.setText(getString(R.string.label_tv_mv_original_language, originalLanguage));
            overviewText.setText(overview);
            popularityText.setText(getString(R.string.label_tv_mv_popularity, popularity));
            voteCountText.setText(getString(R.string.label_tv_mv_vote_count, voteCount));
            voteAverageText.setText(getString(R.string.label_tv_mv_vote_average, voteAverage));
            genreText.setText(genres);
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

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setOnDismissListener(() -> mOverlayView.setVisibility(View.GONE));

        // show the popup window
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

    private void setUIHasReviews(boolean hasReviews) {
        mExpandReviewsButton.setVisibility(hasReviews ? View.VISIBLE : View.GONE);
        mNoReviewsText.setVisibility(hasReviews ? View.GONE : View.VISIBLE);
    }

    public void displayReviews(Review[] reviews) {
        Log.d(TAG_NAME, "Reviews found: " + reviews.length);

        if (reviews.length == 0) {
            setUIHasReviews(false);
            return;
        }

        setUIHasReviews(true);
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
        } else if (id == R.id.btn_unfavorite) {
            item.setIcon(isFavorite ? R.drawable.ic_unfavorite : R.drawable.ic_favorite);
            isFavorite = !isFavorite;
        } else if (id == R.id.btn_watch) {
            item.setIcon(isWatched ? R.drawable.ic_notwatched : R.drawable.ic_watched);
            isWatched = !isWatched;
        } else if (id == R.id.btn_share) {
            shareMovie();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_detailpage, menu);
        MenuItem favoriteButton = menu.findItem(R.id.btn_unfavorite);
        MenuItem watchedButton = menu.findItem(R.id.btn_watch);

        favoriteButton.setIcon(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_unfavorite);
        watchedButton.setIcon(isWatched ? R.drawable.ic_watched : R.drawable.ic_notwatched);

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArray(REVIEWS, mReviewViewModel.getReviews().getValue());
        outState.putString(VIDEOKEY, mVideoKey);
        super.onSaveInstanceState(outState);
    }

    public void shareMovie(){
        String url = "http://movieplanet.moviedetails.com/" + mMovie.getId();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this movie called " + mMovie.getTitle() + ", on " + url);
        intent.putExtra(Intent.EXTRA_STREAM, mMovie.getSmallImageURL());
        intent.setType("text/plain");

        if (intent.resolveActivity(getPackageManager()) != null) {
            Intent shareIntent = Intent.createChooser(intent, null);
            startActivity(shareIntent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this shit!");
        }
    }
}