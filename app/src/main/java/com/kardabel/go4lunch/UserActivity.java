package com.kardabel.go4lunch;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.kardabel.go4lunch.databinding.UserInterfaceBinding;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private UserInterfaceBinding binding;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_LOCATION_PERMS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = UserInterfaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 6 - Configure all views
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        DrawerLayout drawer = binding.drawerLayout;

        //GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        //if (signInAccount != null){
        //}

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

        // CHECK FOR PERMISSIONS
        locationPermission();

    }

    @AfterPermissionGranted(RC_LOCATION_PERMS)
    private void locationPermission(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_LOCATION_PERMS, PERMS);
            return;
        }
        Toast.makeText(this, "Permission was already granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.your_lunch:
                break;
            case R.id.settings:
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                final Intent intent = new Intent(this, MainActivity.class);
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
    private void configureToolBar(){
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    // 3 - Configure NavigationView

    private void configureNavigationView(){
        this.navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}