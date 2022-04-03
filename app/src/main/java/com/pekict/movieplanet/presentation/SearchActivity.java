package com.pekict.movieplanet.presentation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.logic.MovieListAdapter;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG_NAME = SearchActivity.class.getSimpleName();

    private MovieViewModel mMovieViewModel;

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



        int recyclerViewColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        int mRecyclerViewVerticalSpacing = 100;

        mRecyclerView = findViewById(R.id.recyclerview_search);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, recyclerViewColumns));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(mRecyclerViewVerticalSpacing));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void searchMovies(String query) {
        mMovieViewModel.searchMovies(query);
    }

    private void displayMovies(Movie[] movies) {
        mAdapter = new MovieListAdapter(this, movies, MainActivity.getInstance());
        mRecyclerView.setAdapter(mAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}