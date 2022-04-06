package com.pekict.movieplanet.presentation;

import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.MovieList;
import com.pekict.movieplanet.logic.adapters.ListsAdapter;
import com.pekict.movieplanet.presentation.viewmodels.MovieListViewModel;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_NAME = ListsActivity.class.getSimpleName();
    public static final String MOVIELISTS = "MOVIELISTS";
    private static volatile ListsActivity instance;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    private TextView mNoResultsText;
    private RecyclerView mRecyclerView;
    private ListsAdapter mAdapter;
    private FloatingActionButton mFabButton;

    private MovieListViewModel mMovieListVieModel;

    private Bundle mSavedInstanceState;

    public static Context getContext() {
        if (instance == null) {
            return null;
        }

        return instance.getApplicationContext();
    }

    public static ListsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        instance = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(getResources().getString(R.string.label_menu_item_lists));
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

        mNoResultsText = findViewById(R.id.tv_no_lists_activity);

        // Number of columns in RecyclerView holding Movies based on the devices orientation
        int recyclerViewVerticalSpacing = 100;

        mRecyclerView = findViewById(R.id.recyclerview_movie_lists);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(recyclerViewVerticalSpacing, false));

        mSavedInstanceState = savedInstanceState;

        mMovieListVieModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        mMovieListVieModel.getMovieLists().observe(this, movieLists -> {
            displayMovieLists(movieLists);
        });

        // Listener for the FloatingButton to start the SearchActivity
        mFabButton = findViewById(R.id.btn_fab);
        mFabButton.setOnClickListener(view -> {
            showAddListPopup();
        });

        loadLists();
    }

    // Function that's called when the activity resumes
    @Override
    protected void onResume() {
        super.onResume();

        // Updating the side-menus items, without this other buttons would be selected as well
        setNavigationItemChecked();
    }

    private void addNewList(String title) {
        MovieList newMovieList = new MovieList(title, new ArrayList<>());
        mMovieListVieModel.addNewList(newMovieList);
    }

    // Function to load the Movies depending on the Users situation
    public void loadLists() {
        // Displaying the Movies from the SavedInstance when present
        if (mSavedInstanceState != null) {
            displayMovieLists((MovieList[])mSavedInstanceState.getParcelableArray(MOVIELISTS));
            Log.d(TAG_NAME, "Movies fetched with savedInstance.");

            return;
        }

        mMovieListVieModel.fetchMovieLists();
    }

    // Function that will start fetching Movies based on the selected RadioButtons text
    public void fetchMovieLists() {
        boolean hasInternet = isNetworkAvailable();
        // Todo: Fetch MovieLists
    }

    // Function that will return the Movies matching the users filter options
    public MovieList[] filterMovieLists(MovieList[] movieLists) {
        // Todo: Filter MovieLists
        return null;
    }

    // Function that will display the given Movies in the RecyclerView
    public void displayMovieLists(MovieList[] movieLists) {
        if (movieLists == null) {
            return;
        }

        // Displaying a "No Results" TextView when there are no Movies
        mNoResultsText.setVisibility(movieLists.length == 0 ? View.VISIBLE : View.GONE);

        // Displaying the Movies to the RecyclerView using the MovieListAdapter
        mAdapter = new ListsAdapter(this, movieLists, null, null, false);
        mRecyclerView.setAdapter(mAdapter);

        Log.d(TAG_NAME, movieLists.length + " " + getResources().getString(R.string.label_toast_meals_found));
    }

    // Function that returns if the user has a internet-connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Function that creates and shows a PopupView containing the menu_filter.xml (layout > menu_filter.xml)
//    private void showFilterPopup() {
//        // Inflating the layout of the PopupWindow
//        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        View popupView = inflater.inflate(R.layout.menu_filter, null);
//
//        // Creating the PopupWindow
//        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
//
//        // Showing the PopupWindow
//        popupWindow.showAtLocation(mLoadMoreButton.getRootView(), Gravity.TOP, 250, 250);
//
//        // Setting the UI values to the users filter options
//        mFilterOptionsManager.setFilterMenuUI(popupView);
//
//        popupView.findViewById(R.id.btn_action_filter_movies).setOnClickListener(view -> {
//            // Updating the users filters and filtering + displaying fetched movies
//            mFilterOptionsManager.updateFilterOptions(popupView);
//            displayMovies(filterMovies(mMovieViewModel.getMovies().getValue()));
//
//            // Closing the PopupWindow
//            popupWindow.dismiss();
//        });
//    }

    private void showAddListPopup() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_list, null);
        View listsOverlayView = findViewById(R.id.lists_ov);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setOnDismissListener(() -> listsOverlayView.setVisibility(View.GONE));

        EditText listNameInput = popupView.findViewById(R.id.et_add_list_name_input);
        Button addListButton = popupView.findViewById(R.id.btn_add_list);
        addListButton.setOnClickListener(event -> {
            String newListTitle = listNameInput.getText().toString();

            // Showing an error Toast when the new List has no name
            if (newListTitle.isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_tv_error_name_list_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            addNewList(listNameInput.getText().toString());
            popupWindow.dismiss();
        });

        // show the popup window
        popupWindow.showAtLocation(mFabButton.getRootView(), Gravity.CENTER, 0, 0);
        listsOverlayView.setVisibility(View.VISIBLE);
    }

    // Function to start a Activity with the given Class
    private void startActivity(Class activity) {
        Intent intent = new Intent(getApplicationContext(), activity);
        startActivity(intent);
    }

    // Function that activated the side-navigation Home item
    private void setNavigationItemChecked() {
        if (mNavigationView == null) return;

        Menu sideMenu = mNavigationView.getMenu();
        sideMenu.findItem(R.id.action_home).setChecked(false);
        sideMenu.findItem(R.id.action_search).setChecked(false);
        sideMenu.findItem(R.id.action_list).setChecked(true);
    }

    // Function that creates the header containing menu_bar.xml (menu > menu_bar.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        MenuItem sortButton = menu.findItem(R.id.btn_menu_sort);
        sortButton.setVisible(false);

        return true;
    }

    // Function to handle clicks on MenuItems in the header (menu > menu_bar.xml)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btn_menu_filter) {
            // Todo: Show FilterPopup()
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Function to handle clicks on the side-menu (menu > menu_main.xml)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(MainActivity.class);
                break;
            case R.id.action_search:
                startActivity(SearchActivity.class);
                break;
        }

        // Closing the side-menu
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Function temporary to save the current fetched Movies
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}