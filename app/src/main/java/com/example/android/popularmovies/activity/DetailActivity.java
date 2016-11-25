package com.example.android.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.fragment.DescriptionTab;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.PagerAdapter;
import com.example.android.popularmovies.data.MovieContract;

public class DetailActivity extends AppCompatActivity {

    static final String[] tabTitle = new String[] {
            "Description",
            "Trailers",
            "Reviews"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(tabTitle[0]);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(tabTitle[0]));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitle[1]));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitle[2]));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), getIntent().getData());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setTitle(tabTitle[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        Uri uri = getIntent().getData();
        if (uri != null) {
            long id = MovieContract.MovieEntry.getMovieIdFromUri(uri);
            int isFavorite = DescriptionTab.getState(id, this);
            if (isFavorite == 1) {
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_36dp));
            }
            else {
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white_36dp));
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
