package com.pekict.movieplanet.presentation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    private MovieViewModel mMovieViewModel;

    private DrawerLayout mDrawer;

    private TextView mNoNetworkText;
    private TextView mNoResultsText;
    private SearchView mSearchBar;

    private MovieListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(getResources().getString(R.string.label_app_home));
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.action_search).setChecked(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mMovieViewModel.getSearchedMovies().observe(this, movies -> {
            Log.d(TAG_NAME, "Movies found: " + movies.length);
            displayMovies(movies);
        });

        mNoNetworkText = findViewById(R.id.tv_search_no_network);
        mNoResultsText = findViewById(R.id.tv_search_no_results);
        mSearchBar = findViewById(R.id.sv_search);

        if(!isNetworkAvailable()) {
            mSearchBar.setVisibility(View.GONE);
            mNoNetworkText.setVisibility(View.VISIBLE);
        }

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

        int recyclerViewColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        int mRecyclerViewVerticalSpacing = 100;

        mRecyclerView = findViewById(R.id.recyclerview_search);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, recyclerViewColumns));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(mRecyclerViewVerticalSpacing));
    }

    private void searchMovies(String query) {
        mMovieViewModel.searchMovies(query);
    }

    private void displayMovies(Movie[] movies) {
        if (movies.length == 0) {
            mNoResultsText.setVisibility(View.VISIBLE);
            return;
        }

        mNoResultsText.setVisibility(View.GONE);

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

    // Function that's called when item in side-menu is clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.action_share:
                // Todo: Share
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}