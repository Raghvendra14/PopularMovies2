package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final String PATH_TRAILER = "trailer";

    public static final String PATH_REVIEW = "review";


    /* Inner class that defines the table contents of the movies table */
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_ID = "movie_id";
        //Movie title stored as provided by the themoviedb.org API.
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        //Poster path is stored in the form of URL.
        public static final String COLUMN_POSTER_PATH = "poster_path";
        //Movie's release date stored in YYYY-MM-DD format.
        public static final String COLUMN_RELEASE_DATE = "release_date";
        //Movie's user rating stored in double by the API
        public static final String COLUMN_USER_RATING = "user_rating";
        //Popularity of Movie stored in double by the API
        public static final String COLUMN_POPULARITY = "popularity";
        //Description of the movie, as provided by the API.
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_IS_POPULAR = "is_popular";
        public static final String COLUMN_IS_TOP_RATED = "is_top_rated";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static final String POPULAR = "popular";
        public static final String TOP_RATED = "top_rated";
        public static final String FAVORITE = "favorite";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //This method will return the URI of the recently inserted record
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Uri buildMovieUriWithId(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static Uri buildMovieUriWithSortOrder(String sortBy) {
            return CONTENT_URI.buildUpon().appendPath(sortBy).build();
        }
    }

    public static final class MovieTrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // The key will be used to parse the URI for watching the video.
        public static final String COLUMN_KEY = "key";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        //This method will return the URI of the recently inserted record
        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static String getTrailerIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class MovieReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "review";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        //This method will return the URI of the recently inserted record
        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static String getReviewIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildReviewUriById(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
    }

}
