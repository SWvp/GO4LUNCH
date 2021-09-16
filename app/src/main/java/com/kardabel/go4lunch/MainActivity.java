package com.kardabel.go4lunch;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.kardabel.go4lunch.databinding.MainActivityBinding;
import com.kardabel.go4lunch.di.ListViewViewModelFactory;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private MainActivityBinding binding;

    private UserActivityViewModel userActivityViewModel;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private static final int LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ListViewViewModelFactory listViewModelFactory = ListViewViewModelFactory.getInstance();

        userActivityViewModel =
                new ViewModelProvider(this, listViewModelFactory).get(UserActivityViewModel.class);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);

        // 6 - Configure all views
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        DrawerLayout drawer = binding.drawerLayout;

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mapview, R.id.navigation_listview, R.id.navigation_workmates)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

        } else {
            userActivityViewModel.PermissionAlreadyGranted();
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        userActivityViewModel.CheckPermission(permissions, requestCode, grantResults);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.your_lunch:
                break;
            case R.id.settings:
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                final Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // 1 - Configure Toolbar
    private void configureToolBar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    // 3 - Configure NavigationView

    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // INFLATE MENU
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // GET SEARCHVIEW AND CONFIGURE SEARCHMANAGER
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setBackgroundColor(Color.WHITE);
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.BLACK);
        editText.setHintTextColor(Color.GRAY);
        // ASSUMES CURRENT ACTIVITY IS THE SEARCHABLE ACTIVITY
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}