package com.pekict.movieplanet.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.movie.MovieList;
import com.pekict.movieplanet.logic.FilterOptionsManager;
import com.pekict.movieplanet.logic.MovieFilter;
import com.pekict.movieplanet.logic.adapters.MovieListAdapter;
import com.pekict.movieplanet.presentation.viewmodels.MovieListViewModel;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;

import java.util.Map;
import java.util.Objects;

public class MovieListViewActivity extends AppCompatActivity {
    private static final String TAG_NAME = MovieListViewActivity.class.getSimpleName();
    public static final String MOVIES = "MOVIES";
    private static volatile MovieListViewActivity instance;

    private TextView mMovieListTitleText;
    private TextView mNoResultsText;
    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;

    private MovieList mMovieList;
    private MovieListViewModel mMovieViewModel;

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mSharedPrefsEditor;
    private FilterOptionsManager mFilterOptionsManager;
    private Bundle mSavedInstanceState;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static MovieListViewActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list_view);

        instance = this;

        // Retrieving the passed MovieList from the Intent if present
        Intent intent = getIntent();
        if (intent.getExtras() == null) { return; }
        Bundle bundle = intent.getParcelableExtra("bundle");

        if (bundle == null) { return; }
        mMovieList = bundle.getParcelable(ListsActivity.MOVIELISTS);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mMovieListTitleText = findViewById(R.id.tv_mlv_title);
        mMovieListTitleText.setText(getString(R.string.label_tv_mlv_title, mMovieList.getTitle()));

        mNoResultsText = findViewById(R.id.tv_no_lists_activity);

        // Number of columns in RecyclerView holding Movies based on the devices orientation
        int recyclerViewColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        int recyclerViewVerticalSpacing = 100;

        mRecyclerView = findViewById(R.id.recyclerview_movie_lists);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, recyclerViewColumns));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(recyclerViewVerticalSpacing, false));

        mMovieViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);

        mSharedPrefs = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mSharedPrefsEditor = mSharedPrefs.edit();
        mSavedInstanceState = savedInstanceState;

        mFilterOptionsManager = new FilterOptionsManager(mSharedPrefs, mSharedPrefsEditor, this);
        mFilterOptionsManager.initFilterOptions();

        loadMovies();
    }

    // Function to load the Movies from the passed MovieList
    public void loadMovies() {
        displayMovies(mMovieList.getMoviesAsArray());
    }

    // Function that will return the Movies matching the users filter options
    public Movie[] filterMovies(Movie[] movies) {
        Map<String, String> filterOptions = mFilterOptionsManager.getFilterOptions();
        return MovieFilter.getFilteredMovies(filterOptions, Objects.requireNonNull(movies));
    }

    // Function that will display the given Movies in the RecyclerView
    public void displayMovies(Movie[] movies) {
        // Displaying a "No Results" TextView when there are no Movies
        mNoResultsText.setVisibility(movies.length == 0 ? View.VISIBLE : View.GONE);

        // Displaying the Movies to the RecyclerView using the MovieListAdapter
        mAdapter = new MovieListAdapter(this, movies, MainActivity.getInstance());
        mRecyclerView.setAdapter(mAdapter);

        Log.d(TAG_NAME, movies.length + " " + getResources().getString(R.string.label_toast_meals_found));
    }

    // Function that creates and shows a PopupView containing the menu_filter.xml (layout > menu_filter.xml)
    private void showFilterPopup() {
        // Inflating the layout of the PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.menu_filter, null);

        // Creating the PopupWindow
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // Showing the PopupWindow
        popupWindow.showAtLocation(mRecyclerView.getRootView(), Gravity.TOP, 250, 250);

        // Setting the UI values to the users filter options
        mFilterOptionsManager.setFilterMenuUI(popupView);

        popupView.findViewById(R.id.btn_action_filter_movies).setOnClickListener(view -> {
            // Updating the users filters and filtering + displaying fetched movies
            mFilterOptionsManager.updateFilterOptions(popupView);
            displayMovies(filterMovies(mMovieList.getMoviesAsArray()));

            // Closing the PopupWindow
            popupWindow.dismiss();
        });
    }

    // Function that creates the header containing menu_bar.xml (menu > menu_bar.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);

        // Disabling the sorting button
        MenuItem sortButton = menu.findItem(R.id.btn_menu_sort);
        sortButton.setVisible(false);

        return true;
    }

    // Function to handle clicks on MenuItems in the header (menu > menu_bar.xml)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.btn_menu_filter) {
            showFilterPopup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Function temporary to save the current fetched Movies
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ListsActivity.MOVIELISTS, mMovieList);
        super.onSaveInstanceState(outState);
    }
}