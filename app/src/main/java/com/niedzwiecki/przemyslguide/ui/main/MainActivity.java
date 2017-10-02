package com.niedzwiecki.przemyslguide.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.niedzwiecki.przemyslguide.R;
import com.niedzwiecki.przemyslguide.data.SyncService;
import com.niedzwiecki.przemyslguide.data.model.InterestPlace;
import com.niedzwiecki.przemyslguide.data.model.PlacesResponse;
import com.niedzwiecki.przemyslguide.ui.base.BaseActivity;
import com.niedzwiecki.przemyslguide.ui.base.ViewModel;
import com.niedzwiecki.przemyslguide.ui.login.email.EmailActivity;
import com.niedzwiecki.przemyslguide.ui.maps.MapsActivity;
import com.niedzwiecki.przemyslguide.ui.placeDetails.PlaceDetailsActivity;
import com.niedzwiecki.przemyslguide.util.RecyclerItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.niedzwiecki.przemyslguide.ui.login.password.PasswordActivity.EMAIL_KEY;

public class MainActivity extends BaseActivity {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "uk.co.ribot.androidboilerplate.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    public static final String INTEREST_PLACE_KEY =
            "com.niedzwiecki.przemyslGuide.PlaceDetailActivity.key";

    @Inject
    PlacesAdapter placesAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Inject
    MainViewModel mainViewModel;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private String email;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntentRibot(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    public static void start(Activity context, String email) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra(EMAIL_KEY, email);
        context.startActivity(starter);
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(EMAIL_KEY)) {
            email = getIntent().getStringExtra(EMAIL_KEY);
            View header = navigationView.getHeaderView(0);
            TextView name = (TextView) header.findViewById(R.id.emailInfo);
            name.setText(email);
        }

        init();
    }

    private void init() {
        mRecyclerView.setAdapter(placesAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        InterestPlace places = placesAdapter.getPlace(position);
                        openDetail(places);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        mainViewModel.attachNavigator(this);
        mainViewModel.loadPlaces();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }

                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.navMap:
                        startActivity(MapsActivity.class);
                        return true;
                    case R.id.navLogout:
                        mainViewModel.logout();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void openDetail(InterestPlace interestPlace) {
        startActivity(PlaceDetailsActivity.getStartIntent(this, interestPlace));
    }

    @Override
    public void beforeViews() {
        super.beforeViews();
        setDataBindingEnabled(true);
    }

    @Override
    public int contentId() {
        return R.layout.activity_main;
    }

    @Override
    public void afterViews() {
        super.afterViews();
        setViewModel(createViewModel());
        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(SyncService.getStartIntent(this));
        }
    }

    @Override
    public ViewModel createViewModel() {
        return mainViewModel;
    }

    @Override
    public MainViewModel getViewModel() {
        return (MainViewModel) super.getViewModel();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (viewModel != null) {
            viewModel.attachNavigator(this);
        }
    }

    @Override
    public void moveForward(Options options, Object... data) {
        super.moveForward(options, data);
        switch (options) {
            case SHOW_PLACES:
                PlacesResponse placesResponse = (PlacesResponse) data[0];
                showPlaces(placesResponse.interestPlaces);
                break;
            case START_EMAIL_ACTIVITY:
                EmailActivity.start(this);
                break;
        }
    }

    //MVP
    public void showPlaces(List<InterestPlace> interestPlaces) {
        placesAdapter.setPlaces(interestPlaces);
        placesAdapter.notifyDataSetChanged();
    }

}
