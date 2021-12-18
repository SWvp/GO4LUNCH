package com.kardabel.go4lunch;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kardabel.go4lunch.databinding.MainActivityBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;
import com.kardabel.go4lunch.ui.autocomplete.PredictionViewState;
import com.kardabel.go4lunch.ui.autocomplete.PredictionsAdapter;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;
import com.kardabel.go4lunch.ui.setting.SettingActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        PredictionsAdapter.OnPredictionItemClickedListener {

    private MainActivityViewModel mainActivityViewModel;

    private AppBarConfiguration appBarConfiguration;
    private MainActivityBinding binding;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private PredictionsAdapter adapter;

    private String restaurantId;
    private int currentUserRestaurantChoiceStatus;

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

        // RECEIVE SINGLE LIVEDATA EVENT FROM VM TO KNOW WHICH ACTION IS REQUIRED FOR PERMISSION
        mainActivityViewModel.getActionSingleLiveEvent().observe(this, action -> {
            switch (action) {
                case PERMISSION_ASKED:
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                    Toast.makeText(
                            MainActivity.this,
                            MainActivity.this.getString(R.string.need_your_position),
                            Toast.LENGTH_SHORT).show();
                    break;
                case PERMISSION_DENIED:
                    MaterialAlertDialogBuilder alertDialogBuilder =
                            new MaterialAlertDialogBuilder(MainActivity.this);
                    alertDialogBuilder.setTitle(MainActivity.this.getString(R.string.title_alert));
                    alertDialogBuilder.setMessage(MainActivity.this.getString(R.string.rational));
                    alertDialogBuilder.show();
                    break;
            }
        });
        updateUIWhenCreating();
        configureYourLunch();
        configureRecyclerView();
    }


    // HERE WE UPDATE INTERFACE WITH USERS INFORMATION
    private void updateUIWhenCreating() {
        View header = binding.navigationView.getHeaderView(0);
        ImageView profilePicture = header.findViewById(R.id.header_picture);
        TextView profileUsername = header.findViewById(R.id.header_name);
        TextView profileUserEmail = header.findViewById(R.id.header_email);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            //Get picture URL from Firebase
            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicture);
            }

            //Get email & username from Firebase
            String email =
                    TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.info_no_email_found) : currentUser.getEmail();
            String username =
                    TextUtils.isEmpty(currentUser.getDisplayName()) ? getString(R.string.info_no_username_found) : currentUser.getDisplayName();

            //Update views with data
            profileUsername.setText(username);
            profileUserEmail.setText(email);
        }
    }

    // OBSERVE RESTAURANT ID FOR CURRENT USER RESTAURANT CHOICE
    private void configureYourLunch() {
        mainActivityViewModel.getUserRestaurantChoice();

        mainActivityViewModel
                .getCurrentUserRestaurantChoice()
                .observe(this, mainActivityYourLunchViewState -> {
            restaurantId = mainActivityYourLunchViewState.getRestaurantId();
            currentUserRestaurantChoiceStatus =
                    mainActivityYourLunchViewState.getCurrentUserRestaurantChoiceStatus();
        });

    }

    // WHEN USER CLICK ON HIS PERSONAL MENU, IN DRAWER
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.your_lunch:
                checkUserRestaurantChoice();
                break;
            case R.id.settings:
                final Intent intentSetting = new Intent(this, SettingActivity.class);
                startActivity(intentSetting);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                final Intent intentLogout = new Intent(this, AuthenticationActivity.class);
                startActivity(intentLogout);
                finish();
                break;
            default:
                break;

        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    // THE OBSERVER WILL UPDATE VAR IF USER CHOSE OR HAS CHOSEN A RESTAURANT
    private void checkUserRestaurantChoice() {
        switch (currentUserRestaurantChoiceStatus) {

            case 0:
                Toast.makeText(
                        MainActivity.this,
                        MainActivity.this.getString(R.string.no_restaurant_selected),
                        Toast.LENGTH_SHORT).show();
                break;
            case 1:
                startActivity(RestaurantDetailsActivity.navigate(
                        MainActivity.this,
                        restaurantId));
                break;
        }
    }

    private void configureToolBar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void configureNavigationView() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();

    }

    // UPDATE ITEM ADAPTER FOR SEARCHVIEW
    private void configureRecyclerView() {
        adapter = new PredictionsAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.predictions_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mainActivityViewModel
                .getPredictionsLiveData()
                .observe(this, predictions -> adapter.submitList(predictions));
    }


    // CREATE AND CONFIGURE SEARCHVIEW
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // INFLATE MENU
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.search);

        // GET SEARCHVIEW
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setBackgroundColor(Color.WHITE);
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.BLACK);
        editText.setHintTextColor(Color.GRAY);

        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainActivityViewModel.sendTextToAutocomplete(newText);
                return false;
            }
        });

        // WHEN USER LEAVES THE SEARCHVIEW, RESET AND CLOSE THE SEARCHVIEW
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mainActivityViewModel.userSearch("");
                return true;
            }
        });
        return true;

    }

    // WHEN A PREDICTION IS CHOSEN BY USER
    @Override
    public void onPredictionItemClicked(String predictionText) {
        mainActivityViewModel.userSearch(predictionText);
        initAutocomplete();

    }

    // CLEAR THE AUTOCOMPLETE ADAPTER WITH EMPTY LIST WHEN USER FINISHED HIS RESEARCH
    // TO CLEAN THE AUTOCOMPLETE RECYCLERVIEW
    private void initAutocomplete() {
        List<PredictionViewState> emptyList = new ArrayList<>();
        adapter.submitList(emptyList);
    }


    // WHEN VIEW IS ON RESUME CHECK THE PERMISSION STATE IN VIEWMODEL (AND PASSED THE ACTIVITY
    // FOR THE ALERTDIALOG EVEN IF ITS NOT THE GOOD WAY TO USE A VIEWMODEL,
    // WE DON'T HAVE OTHER CHOICE)
    @Override
    protected void onResume() {
        super.onResume();
        mainActivityViewModel.checkPermission(this);
    }
}