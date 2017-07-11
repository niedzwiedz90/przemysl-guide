package com.niedzwiecki.przemyslguide.ui.placeDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.niedzwiecki.przemyslguide.R;
import com.niedzwiecki.przemyslguide.data.model.Ribot;
import com.niedzwiecki.przemyslguide.ui.base.BaseActivity;

import static com.niedzwiecki.przemyslguide.ui.main.MainActivity.RIBOT_KEY;

/**
 * Created by niedzwiedz on 10.07.17.
 */

public class PlaceDetailsActivity extends BaseActivity {
    /*
    @BindView(R.id.textOfRibot)
    TextView textView;
*/

    private Ribot ribot;

    public static Intent getStartIntentRibot(Context context, Ribot event) {
        Intent intent = new Intent(context, PlaceDetailsActivity.class);
        intent.putExtra(RIBOT_KEY, event);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_place_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      /*  setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
*/
        if (getIntent().hasExtra(RIBOT_KEY)) {
            ribot = (Ribot) getIntent().getSerializableExtra(RIBOT_KEY);

            /*textView.setText(String.format("%s %s",
                    ribot.profile().name().first(), ribot.profile().name().last()));*/
        }

    }

    @Override
    public int contentId() {
        return 0;
    }
}
