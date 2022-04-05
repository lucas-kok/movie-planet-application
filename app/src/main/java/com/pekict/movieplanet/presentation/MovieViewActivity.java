package com.pekict.movieplanet.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.movie.MovieList;
import com.pekict.movieplanet.domain.review.Review;
import com.pekict.movieplanet.domain.trailer.Trailer;
import com.pekict.movieplanet.logic.adapters.ListsAdapter;
import com.pekict.movieplanet.logic.adapters.ReviewListAdapter;
import com.pekict.movieplanet.logic.TrailerFilter;
import com.pekict.movieplanet.presentation.viewmodels.MovieListViewModel;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;
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
    private static volatile MovieViewActivity instance;

    private ActionBar mActionBar;
    private boolean isFavorite = false;
    private boolean isWatched = false;

    private TextView mTitleText;
    private TextView mReleaseDateText;
    private TextView mGenreText;
    private TextView mOriginalLanguageText;
    private TextView mOverviewText;
    private TextView mPopularityText;
    private TextView mVoteCountText;
    private TextView mVoteAverageText;
    private RatingBar mRatingBar;
    private Button mAddMovieToListButton;

    private Movie mMovie;
    private MovieList[] mMovieLists;

    private View mOverlayView;
    private Button mTrailerButton;
    private ImageButton mExpandReviewsButton;
    private TextView mNoReviewsText;

    private SharedPreferences mPreferences;

    private MovieViewModel mMovieViewModel;
    private MovieListViewModel mMovieListViewModel;
    private ReviewViewModel mReviewViewModel;
    private TrailerViewModel mTrailerViewModel;

    private Bundle mSavedInstanceState;

    private String mVideoKey;
    private int mMovieId;

    private RecyclerView mReviewsRecyclerView;
    private ReviewListAdapter mAdapter;

    public static Context getContext() {
        if (instance == null) {
            return null;
        }

        return instance.getApplicationContext();
    }

    public static MovieViewActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        instance = this;

        // Observing the MovieViewModel LiveData<Movie> for changes in the SharedMovie
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mMovieViewModel.getSharedMovie().observe(this, movie ->  {
            // If present displaying the information of the Shared Movie
            mMovie = movie;
            displayMovieInformation();
        });

        // Observing the MovieListViewModel LiveData<MovieList[]> for changes
        mMovieListViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        // Declaring the local variable when Lists are present
        mMovieListViewModel.getMovieLists().observe(this, lists -> mMovieLists = lists);

        // Observing the ReviewViewModel LiveData<Review[]> for changes
        mReviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);
        // Displaying the reviews when present
        mReviewViewModel.getReviews().observe(this, reviews -> displayReviews(reviews));

        // Observing the TrailerViewModel LiveData<Trailer[]> for changes
        mTrailerViewModel = ViewModelProviders.of(this).get(TrailerViewModel.class);
        mTrailerViewModel.getTrailers().observe(this, trailers -> {
            // Only displaying when there is a quality trailer
            if (getBestTrailer(trailers) == null) return;

            // Showing the trailer button when a video is available
            mVideoKey = getBestTrailer(trailers).getKey();
            showTrailerButton();
        });

        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // Listener for the "Trailer" Button to open the trailer popup
        mTrailerButton = findViewById(R.id.btn_trailer);
        mTrailerButton.setOnClickListener(view -> showTrailerPopup());

        mTitleText = findViewById(R.id.tv_mv_title);
        mReleaseDateText = findViewById(R.id.tv_mv_release_date);
        mGenreText = findViewById(R.id.tv_mv_genres);
        mOriginalLanguageText = findViewById(R.id.tv_mv_original_language);
        mOverviewText = findViewById(R.id.tv_mv_overview);
        mPopularityText = findViewById(R.id.tv_mv_popularity);
        mVoteCountText = findViewById(R.id.tv_mv_vote_count);
        mVoteAverageText = findViewById(R.id.tv_mv_vote_average);
        mRatingBar = findViewById(R.id.tv_mv_ratingbar_details);
        mAddMovieToListButton = findViewById(R.id.btn_mv_add_to_list);
        mAddMovieToListButton.setOnClickListener(event -> showListsPopup());

        ViewGroup movieViewLayout = findViewById(R.id.ll_mv);

        // Listener for the Reviews Button to collapse or expand the RecyclerView
        mExpandReviewsButton = findViewById(R.id.btn_expand_reviews);
        mExpandReviewsButton.setOnClickListener(view -> {
            if (mReviewsRecyclerView.getVisibility() == View.VISIBLE) {
                // TransitionManager will smooth out the showing/disappearing of the Reviews
                TransitionManager.beginDelayedTransition(movieViewLayout,
                        new AutoTransition());
                mReviewsRecyclerView.setVisibility(View.GONE);
                mExpandReviewsButton.setImageResource(R.drawable.ic_expand);
            } else {
                TransitionManager.beginDelayedTransition(movieViewLayout,
                        new AutoTransition());
                mReviewsRecyclerView.setVisibility(View.VISIBLE);
                mExpandReviewsButton.setImageResource(R.drawable.ic_collapse);
            }
        });

        mReviewsRecyclerView = findViewById(R.id.recyclerview_reviews);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mOverlayView = findViewById(R.id.mv_ov);
        mNoReviewsText = findViewById(R.id.tv_mv_no_reviews);

        mPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mSavedInstanceState = savedInstanceState;

        // Loading in the meal the user clicked
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getParcelableExtra("bundle");

            if (bundle != null) {
                mMovie = bundle.getParcelable("movieObj");
            }

            // mMovie will be null when Activity is opened from a link
            if (mMovie == null) {
                // Fetching the Uri from the Intent containing a shared Movie
                Uri uri = intent.getData();

                // Returning when no uri is present
                if (uri == null) { return; }

                // Fetching the shared Movie by his id
                int id = Integer.parseInt(uri.toString().replace("http://movieplanet.moviedetails.com/", ""));
                mMovieViewModel.fetchMovieById(id);

                return;
            }

            // When mMovie is present displaying the selected Movies information
            Log.d(TAG_NAME, "Opening Movie: " + mMovie.getTitle());
            mMovieId = mMovie.getId();
            displayMovieInformation();
        }

        // Start fetching the Movies attached components
        loadLists();
        loadTrailers(mMovieId);
        loadReviews(mMovieId);
    }

    // Function to display the declared Movies information in the Activity
    private void displayMovieInformation() {
        ImageView movieImage = findViewById(R.id.iv_mv);

        String imageURL = mMovie.getSmallImageURL();
        String title = mMovie.getTitle();
        String releaseDate = mMovie.getRelease_date();
        String originalLanguage = mMovie.getOriginal_language();
        String overview = mMovie.getOverview();
        double popularity = mMovie.getPopularity();
        int voteCount = mMovie.getVote_count();
        double voteAverage = mMovie.getVote_average();
        String genresString = mMovie.getGenresAsString();

        // Changing the UI elements to the Movies values
        mActionBar.setSubtitle(title);

        // Displaying a placeholder image when the Movie has no poster
        if (imageURL.equals("https://image.tmdb.org/t/p/w500null")) {
            movieImage.setImageResource(R.drawable.placeholder);
        } else {
            Picasso.get().load(imageURL).into(movieImage);
        }
        mTitleText.setText(title);
        mReleaseDateText.setText(releaseDate);
        mGenreText.setText(genresString);
        if (genresString.isEmpty()) {
            mGenreText.setVisibility(View.GONE);
        }
        mOriginalLanguageText.setText(getString(R.string.label_tv_mv_original_language, originalLanguage));
        mOverviewText.setText(overview);
        mPopularityText.setText(getString(R.string.label_tv_mv_popularity, popularity));
        mVoteCountText.setText(getString(R.string.label_tv_mv_vote_count, voteCount));
        mVoteAverageText.setText(getString(R.string.label_tv_mv_vote_average, voteAverage));

        // Review stars will be saved in the SharedPreferences, default is 0 stars
        float amountOfStars = mPreferences.getFloat(String.valueOf(mMovieId), 0);

        // Setting a Listener so a new rating will be saved in the SharedPreferences
        mRatingBar.setOnRatingBarChangeListener((ratingBar1, v, b) -> saveStarPreferences(ratingBar1, String.valueOf(mMovieId)));
        mRatingBar.setRating(amountOfStars);
    }

    // Function to make the trailer Button visible
    private void showTrailerButton() {
        mTrailerButton.setVisibility(View.VISIBLE);
    }

    // Function to fetch the users MovieLists
    private void loadLists() {
        mMovieListViewModel.fetchMovieLists();
    }

    // Function to fetch the Movies trailers
    private void loadTrailers(int movieId) {
        // Retrieving the trailers key is SavedInstance is present
        if (mSavedInstanceState != null) {
            mVideoKey = mSavedInstanceState.getString(VIDEOKEY);
            showTrailerButton();

            Log.d(TAG_NAME, "Trailer fetched with savedInstance.");
            return;
        }

        // Only fetch trailers when a network is available
        boolean hasInternet = isNetworkAvailable();
        if (!hasInternet) return;

        mTrailerViewModel.fetchTrailers(movieId);
        Log.d(TAG_NAME, "Trailer fetched with ViewModel.");
    }

    // Function to fetch the Movies Reviews
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

    // Function that returns the best quality trailer
    private Trailer getBestTrailer(Trailer[] trailers) {
        // Returning null when no best trailer is present
        if (TrailerFilter.getBestTrailer(trailers) == null) return null;

        Log.d(TAG_NAME, "Best video key: " + TrailerFilter.getBestTrailer(trailers).getKey());
        return TrailerFilter.getBestTrailer(trailers);
    }

    // Function that creates and shows a PopupWindow containing the trailer.xml (layout > trailer.xml)
    private void showTrailerPopup() {
        // Inflating the layout of the PopupWindow
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

        // Creating the PopupWindow
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setOnDismissListener(() -> mOverlayView.setVisibility(View.GONE));

        // Showing the PopupWindow
        popupWindow.showAtLocation(mTrailerButton.getRootView(), Gravity.CENTER, 0, 0);
        mOverlayView.setVisibility(View.VISIBLE);
    }

    // Function that creates and shows a PopupWindow containing the choose_list.xml (layout > choose.list.xml)
    private void showListsPopup() {
        // Returning when the user has no MovieLists
        if  (mMovieLists == null) {
            return;
        }

        // Inflating the layout of the PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.choose_list, null);

        int recyclerViewVerticalSpacing = 100;

        RecyclerView listRecyclerView = popupView.findViewById(R.id.rv_movie_lists);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listRecyclerView.addItemDecoration(new SpaceItemDecoration(recyclerViewVerticalSpacing, false));

        TextView noListsErrorTextPopup = popupView.findViewById(R.id.tv_no_lists_popup);
        if (mMovieLists.length != 0) {
            noListsErrorTextPopup.setVisibility(View.GONE);
        }

        // Creating the PopupWindow
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setOnDismissListener(() -> mOverlayView.setVisibility(View.GONE));

        // Displaying the Movies to the RecyclerView using the MovieListAdapter
        ListsAdapter listsAdapter = new ListsAdapter(this, mMovieLists, mMovie, popupWindow, true);
        listRecyclerView.setAdapter(listsAdapter);

        // Showing the PopupWindow
        popupWindow.showAtLocation(mTrailerButton.getRootView(), Gravity.CENTER, 0, 0);
        mOverlayView.setVisibility(View.VISIBLE);
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
        mReviewsRecyclerView.setAdapter(mAdapter);
    }

    // Function that returns if the user has a internet-connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Function to handle clicks on MenuItems in the header (menu > menu_bar.xml)
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
        } else if (id == R.id.btn_print) {
            doPhotoPrint();
        }

        return super.onOptionsItemSelected(item);
    }

    // Function that creates the header containing menu_bar.xml (menu > menu_bar.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_detailpage, menu);
        MenuItem favoriteButton = menu.findItem(R.id.btn_unfavorite);
        MenuItem watchedButton = menu.findItem(R.id.btn_watch);

        // Setting the menu_bar icons based on the user values, currently always false
        favoriteButton.setIcon(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_unfavorite);
        watchedButton.setIcon(isWatched ? R.drawable.ic_watched : R.drawable.ic_notwatched);

        return true;
    }

    // Function temporary to save the current Movie
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArray(REVIEWS, mReviewViewModel.getReviews().getValue());
        outState.putString(VIDEOKEY, mVideoKey);
        super.onSaveInstanceState(outState);
    }

    // Function that lets the user share the current Movie
    public void shareMovie() {
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

    // Saves amount of stars from the ratingbar to the SharedPreferences
    public void saveStarPreferences(RatingBar ratingBar, String movieId) {
        float amountOfStars = ratingBar.getRating();
        SharedPreferences.Editor mSharedPrefsEditor = mPreferences.edit();
        mSharedPrefsEditor.putFloat(movieId, amountOfStars);
        mSharedPrefsEditor.apply();
        Log.d(TAG_NAME, "Saved amount of stars to SharedPreferences: " + amountOfStars);
    }

    // Function that will send a print request containing a screenshot of the MovieViewActivity
    private void doPhotoPrint() {
        // Creating an image of the screen
        View v = findViewById(R.id.layoutdetail);
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        v.draw(c);

        // Start a print request
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap("layoutdetail.png", bmp);
    }
}