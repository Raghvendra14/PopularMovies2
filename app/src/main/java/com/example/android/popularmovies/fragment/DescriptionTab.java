package com.example.android.popularmovies.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.MainActivity;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DescriptionTab extends Fragment implements LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String DETAIL_URI = "DETAIL_URI";

    private Uri mUri;


    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_ID,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_TITLE,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_USER_RATING,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_IS_FAVORITE
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_ID = 0;
    private static final int COL_MOVIE_URI_ID = 1;
    private static final int COL_MOVIE_TITLE = 2;
    private static final int COL_MOVIE_POSTER_PATH = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_USER_RATING = 5;
    private static final int COL_MOVIE_OVERVIEW = 6;
    private static final int COL_IS_FAVORITE = 7;


    private TextView mOriginalTitle;
    private TextView mUserReview;
    private TextView mOverview;
    private TextView mReleaseDate;
    private ImageView mThumbnail;
    private ImageButton mImageButton;
    private TextView mHeader;
    private View mDivider;
    private Button mTrailerButton;
    private Button mReviewButton;

    private int mIsFavorite;
    private long movieId;



    public DescriptionTab() {
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favorite_button: {
                changeFavorites(MainActivity.getPaneMode());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DescriptionTab.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mOriginalTitle = (TextView) rootView.findViewById(R.id.original_title);
        mThumbnail = (ImageView) rootView.findViewById(R.id.movie_detail_thumbnail);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mUserReview = (TextView) rootView.findViewById(R.id.user_reviews);
        mOverview = (TextView) rootView.findViewById(R.id.overview);

        NestedScrollView scrollView = (NestedScrollView) rootView.findViewById(R.id.detail_scrollview);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
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

        if (MainActivity.getPaneMode()) {
            mImageButton = (ImageButton) rootView.findViewById(R.id.favorite_button_tab);
            mImageButton.setOnClickListener(this);

            mDivider = rootView.findViewById(R.id.divider);

            mHeader = (TextView) rootView.findViewById(R.id.header);

            mTrailerButton = (Button) rootView.findViewById(R.id.trailer_button);
            mTrailerButton.setOnClickListener(this);

            mReviewButton = (Button) rootView.findViewById(R.id.review_button);
            mReviewButton.setOnClickListener(this);

        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.trailer_button: {
                args.putParcelable(TrailerTab.TRAILER_URI, mUri);
                fragment = new TrailerTab();
                break;
            }
            case R.id.review_button: {
                args.putParcelable(ReviewTab.REVIEW_URI, mUri);
                fragment = new ReviewTab();
                break;
            }
            case R.id.favorite_button_tab: {
                changeFavorites(MainActivity.getPaneMode());
            }
        }
        if (fragment != null) {
            fragment.setArguments(args);
            replaceFragment(fragment);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.movie_detail_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void onSortingOrderChanged() {
        // replace the uri, since the sorting order has changed
        Uri uri = mUri;
        if (null != uri) {
            long id = MovieEntry.getMovieIdFromUri(uri);
            mUri = MovieEntry.buildMovieUriWithId(id);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String baseUrl = "http://image.tmdb.org/t/p/w185/";

        if(!data.moveToFirst()) {return;}


        String originalTitle = data.getString(COL_MOVIE_TITLE);
        mOriginalTitle.setText(originalTitle);
        mOriginalTitle.setBackgroundColor(getResources().getColor(R.color.colorTitleBackground));

        String moviePosterPath = data.getString(COL_MOVIE_POSTER_PATH);
        Picasso.with(getContext()).load(baseUrl + moviePosterPath)
                .placeholder(R.drawable.android_placeholder).error(R.drawable.android_placeholder).into(mThumbnail);

        String releaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
        mReleaseDate.setText(releaseDate);

        double userRating = data.getDouble(COL_MOVIE_USER_RATING);
        String userReviews = String.format("%.1f", userRating) + "/10 ";
        mUserReview.setText(userReviews);

        String overView = data.getString(COL_MOVIE_OVERVIEW);
        mOverview.setText(overView);

        mIsFavorite = data.getInt(COL_IS_FAVORITE);

        movieId = data.getLong(COL_MOVIE_URI_ID);

        if (MainActivity.getPaneMode()) {
            int iconType = (getState(movieId, getContext()) == 1) ? R.drawable.ic_favorite_black_48dp : R.drawable.ic_favorite_border_black_48dp;
            mImageButton.setImageResource(iconType);

            int[] attr = new int[] {android.R.attr.listDivider};
            TypedArray ta = getContext().obtainStyledAttributes(attr);
            mDivider.setBackground(ta.getDrawable(0));
            ta.recycle();

            mHeader.setText(getResources().getText(R.string.trailers_reviews));

            mTrailerButton.setText(getResources().getText(R.string.trailers));
            mTrailerButton.setVisibility(View.VISIBLE);

            mReviewButton.setText(getResources().getText(R.string.reviews));
            mReviewButton.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void changeFavorites(boolean paneType) {
        ActionMenuItemView item = (ActionMenuItemView) getActivity().findViewById(R.id.favorite_button);
        ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.favorite_button_tab);
        ContentValues contentValues = new ContentValues();
        if (mIsFavorite == 1) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
            getContext().getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI,
                    contentValues,
                    MovieContract.MovieEntry.COLUMN_ID + " = ?",
                    new String[]{Long.toString(movieId)}
            );
            if(paneType) {
                imageButton.setImageResource(R.drawable.ic_favorite_border_black_48dp);
            } else {
                item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white_36dp));
            }
            setState(0, movieId);
            Toast.makeText(getContext(), "Remove from Favorite", Toast.LENGTH_SHORT).show();
        }
        else {
            contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
            getContext().getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI,
                    contentValues,
                    MovieContract.MovieEntry.COLUMN_ID + " = ?",
                    new String[]{Long.toString(movieId)}
            );
            if(paneType) {
                imageButton.setImageResource(R.drawable.ic_favorite_black_48dp);
            } else {
                item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_36dp));
            }
            setState(1, movieId);
            Toast.makeText(getContext(), "Set as Favorite", Toast.LENGTH_SHORT).show();
        }
    }

    private void setState(int isFavorite, long movieId) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "Favorite", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String key = "State" + Long.toString(movieId);
        editor.putInt(key, isFavorite);
        editor.apply();
    }

    public static int getState(long movieId, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "Favorite", Context.MODE_PRIVATE);
        String key = "State" + Long.toString(movieId);
        return sharedPreferences.getInt(key, 0);
    }

}
