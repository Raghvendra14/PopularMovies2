package com.example.android.popularmovies.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReviewDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final int REVIEW_DETAIL_LOADER = 0;

    private static final String[] REVIEW_DETAIL_COLUMNS = {
            MovieContract.MovieReviewEntry.TABLE_NAME + "." + MovieContract.MovieReviewEntry._ID,
            MovieContract.MovieReviewEntry.TABLE_NAME + "." + MovieContract.MovieReviewEntry.COLUMN_AUTHOR,
            MovieContract.MovieReviewEntry.TABLE_NAME + "." + MovieContract.MovieReviewEntry.COLUMN_CONTENT
    };

    private static final int COL_ID = 0;
    private static final int COL_AUTHOR = 1;
    private static final int COL_CONTENT = 2;

    private TextView mAuthorName;
    private TextView mContent;

    public ReviewDetailFragment() { setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review_detail, container, false);

        mAuthorName = (TextView) rootView.findViewById(R.id.review_detail_author);
        mContent = (TextView) rootView.findViewById(R.id.review_detail_content);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEW_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if(intent == null) {
            return null;
        }
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                REVIEW_DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(!data.moveToFirst()) {
            return;
        }

        String authorName = "Author: " + data.getString(COL_AUTHOR);
        mAuthorName.setText(authorName);

        String content = data.getString(COL_CONTENT);
        mContent.setText(content);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
