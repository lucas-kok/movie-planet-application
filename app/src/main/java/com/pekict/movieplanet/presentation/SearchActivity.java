package com.pekict.movieplanet.presentation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.logic.MovieListAdapter;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG_NAME = SearchActivity.class.getSimpleName();

    private MovieViewModel mMovieViewModel;

    private TextView mNoNetworkText;
    private TextView mNoResultsText;
    private SearchView mSearchBar;

    private MovieListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}