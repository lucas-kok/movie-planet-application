package com.pekict.movieplanet.presentation;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.logic.MovieListAdapter;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_NAME = MainActivity.class.getSimpleName();
    private static final String MEALS = "MEALS";
    private static volatile MainActivity instance;

    private DrawerLayout mDrawer;
    private TextView mMenuUserNameText;
    private TextView mMenuUserEmailText;

    private MovieViewModel mMovieViewModel;

    private int mRecyclerViewColumns;
    private int mRecyclerViewVerticalSpacing;
    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;
    private Button mLoadMoreButton;

    private Bundle mSavedInstanceState;

    public static Context getContext() {
        return instance.getApplicationContext();
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

        View headerView = navigationView.getHeaderView(0);
        mMenuUserNameText = headerView.findViewById(R.id.tv_menu_user_name);
        mMenuUserEmailText = headerView.findViewById(R.id.tv_menu_user_email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mMovieViewModel.getMovies().observe(this, movies -> {
            displayMovies(movies);
            mLoadMoreButton.setVisibility(View.VISIBLE);

            // Only saving to the Database when Movies are fetched from the API and not the same Database
            if (isNetworkAvailable()) {
                mMovieViewModel.savePopularMoviesToDatabase();
            }
        });

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //In landscape
            mRecyclerViewColumns = 4;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //In portrait
            mRecyclerViewColumns = 2;
        }
        mRecyclerViewVerticalSpacing = 100;

        mRecyclerView = findViewById(R.id.recyclerview_popular_movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, mRecyclerViewColumns));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(mRecyclerViewVerticalSpacing));

        mLoadMoreButton = findViewById(R.id.btn_load_more);
        mLoadMoreButton.setOnClickListener(view -> {
            mMovieViewModel.loadMoreMovies(isNetworkAvailable());
            // Todo: fetch new page
            // Todo: display new Movies?
        });

        mSavedInstanceState = savedInstanceState;

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

    // Function that's called when filter-menu is created
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    // Function that's called when item in side-menu is clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.e(TAG_NAME, item.getItemId() + "Clicked op menu");

        switch (item.getItemId()) {
            case R.id.action_home:
                // Todo: Start Intent MainActivity if not already open
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