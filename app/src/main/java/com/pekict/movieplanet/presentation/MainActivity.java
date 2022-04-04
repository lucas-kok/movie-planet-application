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
import com.pekict.movieplanet.logic.FilterOptionsManager;
import com.pekict.movieplanet.logic.MovieFilter;
import com.pekict.movieplanet.logic.MovieListAdapter;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_NAME = MainActivity.class.getSimpleName();
    public static final String MOVIES = "MOVIES";
    private static volatile MainActivity instance;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    private TextView mNoResultsText;
    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;
    private Button mLoadMoreButton;

    private MovieViewModel mMovieViewModel;

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
        toolbar.setSubtitle(getResources().getString(R.string.label_menu_item_home));
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(this);
        setNavigationItemChecked();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNoResultsText = findViewById(R.id.tv_filter_no_results);

        // Number of columns in RecyclerView holding Movies based on the devices orientation
        int recyclerViewColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        int recyclerViewVerticalSpacing = 100;

        mRecyclerView = findViewById(R.id.recyclerview_popular_movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, recyclerViewColumns));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(recyclerViewVerticalSpacing));

        mSharedPrefs = getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mSharedPrefsEditor = mSharedPrefs.edit();
        mSavedInstanceState = savedInstanceState;

        mFilterOptionsManager = new FilterOptionsManager(mSharedPrefs, mSharedPrefsEditor, this);
        mFilterOptionsManager.initFilterOptions();

        // Observing the MovieViewModels LiveData<Movies[]> for changes
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mMovieViewModel.getMovies().observe(this, movies -> {
            // Preventing displaying an Array declared as null when the Movies are cleared
            if (movies == null) { return; }

            // Displaying the new Movies
            displayMovies(filterMovies(mMovieViewModel.getMovies().getValue()));

            // Only showing the "Load More" Button when there is a network connection,
            // without it will fetch all the movies stored in the SQLite Database
            if (isNetworkAvailable()) {
                mLoadMoreButton.setVisibility(View.VISIBLE);
            }
        });

        // Listener for the "Load More" Button to fetch an extra page of Movies
        mLoadMoreButton = findViewById(R.id.btn_load_more);
        mLoadMoreButton.setOnClickListener(view -> mMovieViewModel.loadMoreMovies(isNetworkAvailable(), mFilterOptionsManager.getQuery()));

        // Listener for the FloatingButton to start the SearchActivity
        FloatingActionButton fab = findViewById(R.id.btn_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        });

        loadMovies();
    }

    // Function that's called when the activity resumes
    @Override
    protected void onResume() {
        super.onResume();

        // Updating the side-menus items, without this other buttons would be selected as well
        setNavigationItemChecked();
    }

    // Function to load the Movies depending on the Users situation
    public void loadMovies() {
        // Displaying the Movies from the SavedInstance when present
        if (mSavedInstanceState != null) {
            displayMovies((Movie[])mSavedInstanceState.getParcelableArray(MOVIES));
            Log.d(TAG_NAME, "Movies fetched with savedInstance.");

            return;
        }

        boolean hasInternet = isNetworkAvailable();
        Log.i(TAG_NAME, "User has internet is: " + hasInternet);

        mMovieViewModel.fetchMovies(hasInternet, mFilterOptionsManager.getQuery(), 1);
        Log.d(TAG_NAME, "Movies fetched with ViewModel.");
    }

    // Function that will start fetching Movies based on the selected RadioButtons text
    public void fetchMovies(String text) {
        boolean hasInternet = isNetworkAvailable();
        String query = mFilterOptionsManager.getQueryFromRadioText(text);

        mMovieViewModel.fetchMovies(hasInternet, query, 1);
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
        mAdapter = new MovieListAdapter(this, movies, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        Log.d(TAG_NAME, movies.length + " " + getResources().getString(R.string.label_toast_meals_found));
    }

    // Function that returns if the user has a internet-connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Function that returns the text of the selected sorting RadioButton
    private String getActiveRadioButtonString(View view) {
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        View radioButtonView = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(radioGroup.indexOfChild(radioButtonView));

        return radioButton.getText().toString();
    }

    // Function that creates and shows a PopupView containing the menu_sort.xml (layout > menu_sort.xml)
    private void showSortPopup() {
        // Inflating the layout of the PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.menu_sort, null);

        // Creating the PopupWindow
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // Showing the PopupWindow
        popupWindow.showAtLocation(mLoadMoreButton.getRootView(), Gravity.TOP, 250, 250);
        mFilterOptionsManager.setSortMenuUI(popupView);

        // Listener for the "Sort" Button
        popupView.findViewById(R.id.btn_action_sort_movies).setOnClickListener(view -> {

            mMovieViewModel.clearMovies();

            // Retrieving the text of the selected RadioButton
            String radioButtonString = getActiveRadioButtonString(popupView);

            // Updating the users filters and filtering + displaying fetched movies
            mFilterOptionsManager.updateSortOptions(popupView);
            fetchMovies(radioButtonString);

            // Closing the PopupWindow
            popupWindow.dismiss();
        });
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
        popupWindow.showAtLocation(mLoadMoreButton.getRootView(), Gravity.TOP, 250, 250);

        // Setting the UI values to the users filter options
        mFilterOptionsManager.setFilterMenuUI(popupView);

        popupView.findViewById(R.id.btn_action_filter_movies).setOnClickListener(view -> {
            // Updating the users filters and filtering + displaying fetched movies
            mFilterOptionsManager.updateFilterOptions(popupView);
            displayMovies(filterMovies(mMovieViewModel.getMovies().getValue()));

            // Closing the PopupWindow
            popupWindow.dismiss();
        });
    }

    // Function that activated the side-navigation Home item
    private void setNavigationItemChecked() {
        if (mNavigationView == null) return;

        Menu sideMenu = mNavigationView.getMenu();
        sideMenu.findItem(R.id.action_home).setChecked(true);
        sideMenu.findItem(R.id.action_search).setChecked(false);
        sideMenu.findItem(R.id.action_list).setChecked(false);
        sideMenu.findItem(R.id.action_share).setChecked(false);
    }

    // Function that creates the header containing menu_bar.xml (menu > menu_bar.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    // Function to handle clicks on MenuItems in the header (menu > menu_bar.xml)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btn_menu_filter) {
            showFilterPopup();
            return true;
        } else if (item.getItemId() == R.id.btn_menu_sort) {
            showSortPopup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Function to handle clicks on the side-menu (menu > menu_main.xml)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.action_list:
                // Todo: Open List Activity
                break;
        }

        // Closing the side-menu
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Function temporary to save the current fetched Movies
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArray(MOVIES, mMovieViewModel.getMovies().getValue());
        super.onSaveInstanceState(outState);
    }
}