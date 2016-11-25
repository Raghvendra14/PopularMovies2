package com.example.android.popularmovies.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.MoviePosterAdapter;
import com.example.android.popularmovies.api.FetchMovieDetailsTask;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utils.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER = 0;
    // For the poster view we're showing only a small subset of the stored data.
    // Specify the columns need.
    private static final String[] MOVIE_HOME_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    // These indices are tied to MOVIE_HOME_COLUMNS. If MOVIE_HOME_COLUMNS changes, these
    // must change.
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_URI_ID = 1;
    public static final int COL_POSTER_PATH = 2;

    private MoviePosterAdapter mPosterAdapter;

    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;

    private static int height;
    private static int width;
    
    private View rootView = null;

    private static final String SELECTED_KEY = "selected_position";



    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri idUri);
    }

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // The CursorAdapter will take data from our cursor and populate the GridView.
        mPosterAdapter = new MoviePosterAdapter(getActivity(), null, 0);

        mGridView = (GridView) rootView.findViewById(R.id.home_page_grid);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mGridView.setAdapter(mPosterAdapter);

                if (mPosition != GridView.INVALID_POSITION) {
                    mGridView.smoothScrollToPosition(mPosition);
                }

                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                        // if it cannot seek to that position.
                        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                        if (cursor != null) {
                            long movieId = cursor.getLong(COL_MOVIE_URI_ID);
                            ((Callback) getActivity())
                                    .onItemSelected(MovieContract.MovieEntry.buildMovieUriWithId(movieId));
                        }
                        mPosition = position;
                    }
                });

                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        onSortingOrderChanged();
                    }
                });
                mSwipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary);


                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                width = rootView.getMeasuredWidth();
                height = rootView.getMeasuredHeight();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String sortOrder = Utility.getPreferredSortingOrder(getActivity());
        String sortBy = null;
        if(sortOrder.equals(MovieContract.MovieEntry.TOP_RATED)) {
            sortBy = MovieContract.MovieEntry.COLUMN_USER_RATING + " DESC";
        }
        else if (sortOrder.equals(MovieContract.MovieEntry.POPULAR)) {
            sortBy = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        }
        Uri movieBySortOrder = MovieContract.MovieEntry.buildMovieUriWithSortOrder(sortOrder);

        return new CursorLoader(getActivity(),
                movieBySortOrder,
                MOVIE_HOME_COLUMNS,
                null,
                null,
                sortBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!cursor.moveToFirst() && Utility.getPreferredSortingOrder(getActivity()).equals(MovieContract.MovieEntry.FAVORITE)) {
            Snackbar.make(rootView, R.string.no_movies, Snackbar.LENGTH_LONG)
                    .show();
        }
        mPosterAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mPosterAdapter.swapCursor(null);
    }


    @Override
    public void onStart() {
        if (Utility.isNetworkAvailable(getActivity())) {
            updateMovieDetails();
        } else {
           makeSnackBar(Snackbar.LENGTH_LONG);
        }
        super.onStart();
    }

    public void onSortingOrderChanged() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (Utility.isNetworkAvailable(getActivity())) {
            updateMovieDetails();
        } else {
            makeSnackBar(Snackbar.LENGTH_INDEFINITE);
        }
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    public void makeSnackBar(int snackBarDuration) {
        mSwipeRefreshLayout.setRefreshing(false);
        Snackbar.make(rootView, R.string.no_internet, snackBarDuration)
                .setAction(getResources().getString(R.string.action_try_again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSortingOrderChanged();
                    }
                }).show();
    }

    public ProgressBar getmProgressBar() {
        return mProgressBar;
    }

    private void updateMovieDetails() {
        FetchMovieDetailsTask movieDetailsTask = new FetchMovieDetailsTask(getActivity(), getmProgressBar());
        String sorting_order = Utility.getPreferredSortingOrder(getActivity());
        movieDetailsTask.execute(sorting_order);
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

}


