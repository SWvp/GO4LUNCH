package com.kardabel.go4lunch;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.kardabel.go4lunch.databinding.MainActivityBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;


public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private MainActivityViewModel mainActivityViewModel;

    private AppBarConfiguration appBarConfiguration;
    private MainActivityBinding binding;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private static final int LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BIND THE LAYOUT
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // CONFIGURE VIEWMODEL
        ViewModelFactory listViewModelFactory = ViewModelFactory.getInstance();
        mainActivityViewModel =
                new ViewModelProvider(this, listViewModelFactory)
                        .get(MainActivityViewModel.class);

        // CONFIGURE ALL VIEWS
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        DrawerLayout drawer = binding.drawerLayout;

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mapview, R.id.navigation_listview, R.id.navigation_workmates)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController,
                appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // RECEIVE SINGLE LIVEDATA EVENT FROM VM TO KNOW WHICH ACTION IS REQUIRED
        mainActivityViewModel.getActionSingleLiveEvent().observe(this, action -> {
            switch (action){
                case PERMISSION_GRANTED:
                    Toast.makeText(MainActivity.this, "Welcome, take a seat, have a lunch!", Toast.LENGTH_LONG).show();
                    break;
                case PERMISSION_ASKED:
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                    Toast.makeText(MainActivity.this, "Needed for retrieve your position, thank you", Toast.LENGTH_SHORT).show();
                    break;
//             case PERMISSION_DENIED:
//                 MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
//                 alertDialogBuilder.setTitle("WARNING");
//                 alertDialogBuilder.setMessage("if you want to take full advantage of GO4LUNCH , please reload GO4LUNCH and agree to share your position, thank you !");
//                 alertDialogBuilder.show();


  //              break;


            }
        });
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();

    }

    // WHEN VIEW IS ON RESUME CHECK THE PERMISSION STATE IN VIEWMODEL (AND PASSED THE ACTIVITY
    // FOR THE ALERTDIALOG EVEN IF ITS NOT THE GOOD WAY TO USE A VIEWMODEL!)
    @Override
    protected void onResume() {
        super.onResume();
        mainActivityViewModel.checkPermission(this);
    }

}