package com.example.android.popularmovies.adapter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.popularmovies.fragment.DescriptionTab;
import com.example.android.popularmovies.fragment.ReviewTab;
import com.example.android.popularmovies.fragment.TrailerTab;

/**
 * Created by Raghvendra on 22-07-2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Uri mUri;

    public PagerAdapter(FragmentManager fm, int numOfTabs, Uri data) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
        this.mUri = data;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        switch (position) {
            case 0: {
                arguments.putParcelable(DescriptionTab.DETAIL_URI,  mUri);
                DescriptionTab descTab = new DescriptionTab();
                descTab.setArguments(arguments);
                return descTab;
            }
            case 1: {
                arguments.putParcelable(TrailerTab.TRAILER_URI, mUri);
                TrailerTab trailerTab = new TrailerTab();
                trailerTab.setArguments(arguments);
                return trailerTab;
            }
            case 2: {
                arguments.putParcelable(ReviewTab.REVIEW_URI, mUri);
                ReviewTab reviewTab = new ReviewTab();
                reviewTab.setArguments(arguments);
                return reviewTab;
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
