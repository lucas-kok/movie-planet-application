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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.logic.FilterOptionsManager;
import com.pekict.movieplanet.logic.MovieFilter;
import com.pekict.movieplanet.logic.MovieListAdapter;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_NAME = MainActivity.class.getSimpleName();
    public static final String MEALS = "MEALS";

    private static volatile MainActivity instance;

    private DrawerLayout mDrawer;

    private MovieViewModel mMovieViewModel;

    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;
    private Button mLoadMoreButton;

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mSharedPrefsEditor;
    private FilterOptionsManager mFilterOptionsManager;
    private Bundle mSavedInstanceState;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(getResources().getString(R.string.label_app_home));
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mMovieViewModel.getMovies().observe(this, movies -> {
            displayMovies(filterMovies(mMovieViewModel.getMovies().getValue()));

            // Only showing the "Load More" Button when there is a network connection,
            // without it will fetch all the movies stored in the SQLite Database
            if (isNetworkAvailable()) {
                mLoadMoreButton.setVisibility(View.VISIBLE);
            }
        });

        int recyclerViewColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        int recyclerViewVerticalSpacing = 100;

        mRecyclerView = findViewById(R.id.recyclerview_popular_movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, recyclerViewColumns));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(recyclerViewVerticalSpacing));

        mLoadMoreButton = findViewById(R.id.btn_load_more);
        mLoadMoreButton.setOnClickListener(view -> {
            mMovieViewModel.loadMoreMovies(isNetworkAvailable());
        });

        mSharedPrefs = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mSharedPrefsEditor = mSharedPrefs.edit();
        mSavedInstanceState = savedInstanceState;

        mFilterOptionsManager = new FilterOptionsManager(mSharedPrefs, mSharedPrefsEditor, this);
        mFilterOptionsManager.initFilterOptions();

        loadMovies();
    }

    // Function to load the Movies depending on the Users situation
    public void loadMovies() {
        if (mSavedInstanceState != null) {
            displayMovies((Movie[])mSavedInstanceState.getParcelableArray(MEALS));
            Log.d(TAG_NAME, "Meals fetched with savedInstance.");

            return;
        }

        boolean hasInternet = isNetworkAvailable();
        Log.i(TAG_NAME, "User has internet is: " + hasInternet);

        mMovieViewModel.fetchMovies(hasInternet, 1);
        Log.d(TAG_NAME, "Meals fetched with ViewModel.");
    }

    public Movie[] filterMovies(Movie[] movies) {
        Map<String, String> filterOptions = mFilterOptionsManager.getFilterOptions();

        Movie[] filteredMovies = MovieFilter.getFilteredMovies(filterOptions, Objects.requireNonNull(movies));
        return filteredMovies;
    }

    public void displayMovies(Movie[] movies) {
        mAdapter = new MovieListAdapter(this, movies, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        Toast.makeText(getApplicationContext(), movies.length + " " + getResources().getString(R.string.label_toast_meals_found), Toast.LENGTH_SHORT).show();
    }

    // Function that returns if the user has a internet-connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showFilterPopup() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.menu_filter, null);

        popupView.findViewById(R.id.btn_action_filter_movies).setOnClickListener(view -> {
            mFilterOptionsManager.updateFilterOptions(popupView);
            displayMovies(filterMovies(mMovieViewModel.getMovies().getValue()));
        });

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(mLoadMoreButton.getRootView(), Gravity.TOP, 250, 250);
        mFilterOptionsManager.setFilterMenuUI(popupView);
    }

    // Function that's called when filter-menu is created
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btn_menu_filter) {
            showFilterPopup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Function that's called when item in side-menu is clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.e(TAG_NAME, item.getItemId() + "Clicked op menu");

        switch (item.getItemId()) {
            case R.id.action_home:
                // Todo: Start Intent MainActivity if not already open
                break;
            case R.id.action_search:
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.action_share:
                // Todo: Share
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArray(MEALS, mMovieViewModel.getMovies().getValue());
        super.onSaveInstanceState(outState);
    }
}