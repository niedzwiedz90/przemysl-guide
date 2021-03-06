package com.niedzwiecki.przemyslguide.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.niedzwiecki.przemyslguide.R;
import com.niedzwiecki.przemyslguide.data.DataManager;
import com.niedzwiecki.przemyslguide.util.DialogFactory;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import timber.log.Timber;

/**
 * Abstract activity that every other Activity in this application must implement. It handles
 * creation of Dagger components and makes sure that instances of ConfigPersistentComponent survive
 * across configuration changes.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        Navigator,
        ViewModelIntegration {

    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);

    private long mActivityId;
    private ViewDataBinding viewDataBinding;
    private boolean dataBindingEnabled;
    public ViewModel viewModel;
    public DataManager dataManager;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = ApplicationController.getInstance().getDataModule().provideDataManager();

        beforeViews();
        if (contentId() != 0 && !dataBindingEnabled) {
            setContentView(contentId());
        } else if (contentId() != 0 && dataBindingEnabled) {
            viewDataBinding = DataBindingUtil.setContentView(this, contentId());
        } else {
            Timber.d("You didn't setup content layout at: %s", getClass().getName());
        }

        if (dataBindingEnabled) {
            viewModel = viewModel != null ? viewModel : createViewModel();
            toolbar = findViewById(R.id.toolbar);
            setupToolbarWithDataBinding(toolbar);
        } else {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }

        if (savedInstanceState == null) {
            afterViews();
        } else {
            if (getViewModel() != null) {
                getViewModel().restoreInstanceState(savedInstanceState);
            }

            afterViews(savedInstanceState);
        }

    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    private void setupToolbarWithDataBinding(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() == null) {
                return;
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else {
            return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ACTIVITY_ID, mActivityId);
    }

    @Override
    protected void onDestroy() {
        if (!isChangingConfigurations()) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", mActivityId);
        }
        super.onDestroy();
    }

    @Override
    public ViewModel createViewModel() {
        return null;
    }

    @Override
    public void moveForward(Options options, Object... data) {

    }

    @Override
    public void goBack() {

    }

    @Override
    public void showProgress(@NonNull String title) {
        if (isFinishing() || progressDialog != null) {
            return;
        }
        progressDialog = DialogFactory.createProgressDialog(this, title);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (progressDialog == null) {
            return;
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
        progressDialog = null;
    }

    @Override
    public void showError(@NonNull String content) {
        boolean showed = DialogFactory.safeShowDialog(this, DialogFactory.createGenericErrorDialog(this, content));
        if (!showed) {
            dataManager.showToast(content, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public void startActivity(Class<? extends Activity> activityClass, Serializable serializable) {

    }

    @Override
    public void startActivity(Class<? extends Activity> activityClass, String key, Serializable serializable) {

    }

    @Override
    public void startActivity(Class<? extends Activity> activityClass, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public void startActivityForResult(Class<? extends Activity> activityClass, int requestCode) {

    }

    @Override
    public void startActivityForResult(Class<? extends Activity> activityClass, Serializable serializable, int requestCode) {

    }

    @Override
    public void startActivityForResult(Class<? extends Activity> activityClass, String key, Serializable serializable, int requestCode) {

    }

    @Override
    public void startActivityForResult(Class<? extends Activity> activityClass, Bundle bundle, int requestCode) {

    }

    protected void afterViews(Bundle savedInstanceState) {

    }

    public void beforeViews() {

    }

    public void afterViews() {

    }

    public abstract int contentId();

    public void setDataBindingEnabled(boolean dataBindingEnabled) {
        this.dataBindingEnabled = dataBindingEnabled;
    }

    public ViewDataBinding getViewDataBinding() {
        return viewDataBinding;
    }

    private void setupToolbarWithDataBinding(Toolbar toolbar, boolean showHomeAsUp) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() == null) {
                return;
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            if (showHomeAsUp) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_google_signin_btn_icon_light);
            }
        } else {
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewModel != null) {
            viewModel.attachNavigator(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (viewModel != null) {
            viewModel.detachNavigator();
        }
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

}
