package com.pekict.movieplanet.presentation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
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

import com.google.android.material.snackbar.Snackbar;
import com.pekict.movieplanet.R;
import com.pekict.movieplanet.presentation.viewmodels.MovieViewModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_NAME = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawer;
    private TextView mMenuUserNameText;
    private TextView mMenuUserEmailText;

    private MovieViewModel mMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            Toast.makeText(getApplicationContext(), mMovieViewModel.getMovies().getValue().length + " Movies loaded", Toast.LENGTH_SHORT).show();
        });

        loadMovies();
    }

    public void loadMovies() {
//        if (mSavedInstanceState != null) {
//            mMeals = (Meal[]) mSavedInstanceState.getParcelableArray(MEALS);
//            mMealViewModel.setMeals(mMeals);
//            Log.d(TAG_NAME, "Meals fetched with savedInstance.");
//        } else {
            boolean hasInternet = isNetworkAvailable();
            if (hasInternet) {
                Log.i(TAG_NAME, "User has an internet connection");
            } else {
                Log.i(TAG_NAME, "User has no internet connection");
            }

            mMovieViewModel.fetchMovies(hasInternet);
            Log.d(TAG_NAME, "Meals fetched with ViewModel.");
        }
//    }

    // Function that returns if the user has a internet-connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

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
}