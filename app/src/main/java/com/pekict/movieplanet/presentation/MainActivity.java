package com.pekict.movieplanet.presentation;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.pekict.movieplanet.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_NAME = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawer;
    private TextView mMenuUserNameText;
    private TextView mMenuUserEmailText;

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