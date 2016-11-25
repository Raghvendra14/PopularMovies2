package com.example.android.popularmovies.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.ReviewDetail;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.data.MovieContract;

/**
 * Created by Raghvendra on 22-07-2016.
 */
public class ReviewTab extends Fragment implements LoaderCallbacks<Cursor> {

    public static final String REVIEW_URI = "REVIEW_URI";

    ListView mReviews;
    private ReviewAdapter mReviewAdapter;
    private static final int REVIEW_LOADER = 0;
    private Uri mUri;

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.MovieReviewEntry.TABLE_NAME + "." + MovieContract.MovieReviewEntry._ID,
            MovieContract.MovieReviewEntry.TABLE_NAME + "." + MovieContract.MovieReviewEntry.COLUMN_ID,
            MovieContract.MovieReviewEntry.TABLE_NAME + "." + MovieContract.MovieReviewEntry.COLUMN_AUTHOR
    };

    static final int COL_ID = 0;
    public static final int COL_REVIEW_ID = 1;
    public static final int COL_AUTHOR = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ReviewTab.REVIEW_URI);
        }
        View rootView = inflater.inflate(R.layout.list_reviews, container, false);

        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);

        mReviews = (ListView) rootView.findViewById(R.id.review_list);
        mReviews.setAdapter(mReviewAdapter);

        mReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String reviewId = cursor.getString(COL_REVIEW_ID);
                    if (reviewId != null) {
                        Intent intent = new Intent(getActivity(), ReviewDetail.class)
                                .setData(MovieContract.MovieReviewEntry.buildReviewUriById(reviewId));
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "No Review Available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mReviews.setOnTouchListener(new View.OnTouchListener() {
            int dragThreshold = 30;
            int downX;
            int downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        int distanceX = Math.abs((int) event.getRawX() - downX);
                        int distanceY = Math.abs((int) event.getRawY() - downY);

                        if (distanceY > distanceX) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        } else if (distanceX > distanceY && distanceX > dragThreshold) {
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    }
                }
                return false;
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mUri != null) {
            long movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getContext(),
                    MovieContract.MovieReviewEntry.CONTENT_URI,
                    REVIEW_COLUMNS,
                    MovieContract.MovieReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                    new String[]{Long.toString(movieId)},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mReviewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mReviewAdapter.swapCursor(null);
    }


}
