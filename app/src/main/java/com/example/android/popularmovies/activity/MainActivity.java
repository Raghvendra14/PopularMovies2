package com.example.android.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.DescriptionTab;
import com.example.android.popularmovies.fragment.MainActivityFragment;
import com.example.android.popularmovies.utils.Utility;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private static final String MAINACTIVITYFRAGMENT_TAG = "MAFTAG";

    private static boolean sTwoPane;
    private String mSortOrder;

    protected void onCreate(Bundle savedInstanceState) {
        mSortOrder = Utility.getPreferredSortingOrder(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            sTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DescriptionTab(), MAINACTIVITYFRAGMENT_TAG)
                        .commit();
            }

        } else {
            sTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        String sortOrder = Utility.getPreferredSortingOrder(this);
        //update the sorting order in our second pane using the fragment manager
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            MainActivityFragment maf = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if ( null != maf ) {
                maf.onSortingOrderChanged();
            }
            DescriptionTab df = (DescriptionTab)getSupportFragmentManager().findFragmentByTag(MAINACTIVITYFRAGMENT_TAG);
            if ( null != df ) {
                df.onSortingOrderChanged();
            }
        }
        mSortOrder = sortOrder;
        super.onResume();
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (sTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DescriptionTab.DETAIL_URI, contentUri);

            DescriptionTab fragment = new DescriptionTab();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MAINACTIVITYFRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
       if (getFragmentManager().getBackStackEntryCount() > 0) {
           getFragmentManager().popBackStackImmediate();
       } else {
           super.onBackPressed();
       }
    }
    public static boolean getPaneMode() {
        return sTwoPane;
    }
}
