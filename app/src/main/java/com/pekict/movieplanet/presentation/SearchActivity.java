package com.pekict.movieplanet.presentation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.logic.MovieListAdapter;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_NAME = SearchActivity.class.getSimpleName();
    public static final String MOVIES = "MOVIES";

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    private SearchView mSearchBar;
    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;
    private TextView mNoNetworkText;
    private TextView mNoResultsText;

    private MovieViewModel mMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initiating the Header
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(getResources().getString(R.string.label_menu_item_search));
        setSupportActionBar(toolbar);

        // Initiating the side-menu
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(this);
        setNavigationItemChecked();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Listener to search movies when user submits a Query
        mSearchBar = findViewById(R.id.sv_search);
        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMovies(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
        mNoNetworkText = findViewById(R.id.tv_search_no_network);
        mNoResultsText = findViewById(R.id.tv_no_lists_activity);

        // Number of columns in RecyclerView holding Movies based on the devices orientation
        int recyclerViewColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        int mRecyclerViewVerticalSpacing = 100;

        mRecyclerView = findViewById(R.id.recyclerview_search);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, recyclerViewColumns));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(mRecyclerViewVerticalSpacing, false));

        // Observing the MovieViewModels LiveData<Movies[]> for changes, then displaying the new Movies
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mMovieViewModel.getSearchedMovies().observe(this, this::displayMovies);

        // Hide the SearchView and show an error message when no network is available
        if(!isNetworkAvailable()) {
            mSearchBar.setVisibility(View.GONE);
            mNoNetworkText.setVisibility(View.VISIBLE);
        }

        // Displaying the Movies from the SavedInstance when present
        if (savedInstanceState != null) {
            displayMovies((Movie[])savedInstanceState.getParcelableArray(MOVIES));
            Log.d(TAG_NAME, "Movies fetched with savedInstance.");
        }
    }

    // Function that's called when the activity resumes
    @Override
    protected void onResume() {
        super.onResume();

        // Updating the side-menus items, without this other buttons would be selected as well
        setNavigationItemChecked();
    }

    // Function that will fetch Movies based on the given search-input
    private void searchMovies(String query) {
        mMovieViewModel.searchMovies(query);
    }

    // Function that will display the given Movies in the RecyclerView
    private void displayMovies(Movie[] movies) {
        // Displaying a "No Results" TextView when there are no Movies
        mNoResultsText.setVisibility(movies.length == 0 ? View.VISIBLE : View.GONE);

        // Displaying the Movies to the RecyclerView using the MovieListAdapter
        mAdapter = new MovieListAdapter(this, movies, MainActivity.getInstance());
        mRecyclerView.setAdapter(mAdapter);
    }

    // Function that returns if the user has a internet-connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Function to start a Activity with the given Class
    private void startActivity(Class activity) {
        Intent intent = new Intent(getApplicationContext(), activity);
        startActivity(intent);
    }

    // Function that activated the side-navigation Search item
    private void setNavigationItemChecked() {
        if (mNavigationView == null) return;

        Menu sideMenu = mNavigationView.getMenu();
        sideMenu.findItem(R.id.action_home).setChecked(false);
        sideMenu.findItem(R.id.action_search).setChecked(true);
        sideMenu.findItem(R.id.action_list).setChecked(false);
    }

    // Function to handle clicks on the side-menu (menu > menu_main.xml)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(MainActivity.class);
                break;
            case R.id.action_list:
                startActivity(ListsActivity.class);
                break;
        }

        // Closing the side-menu
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Function temporary to save the current fetched Movies
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArray(MOVIES, mMovieViewModel.getSearchedMovies().getValue());
        super.onSaveInstanceState(outState);
    }
}