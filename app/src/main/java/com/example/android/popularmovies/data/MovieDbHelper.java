package com.example.android.popularmovies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.MovieReviewEntry;
import com.example.android.popularmovies.data.MovieContract.MovieTrailerEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 16;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_IS_POPULAR + " INTEGER DEFAULT 0, " +
                MovieEntry.COLUMN_IS_TOP_RATED + " INTEGER DEFAULT 0, " +
                MovieEntry.COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, " +
                " UNIQUE (" + MovieEntry.COLUMN_ID + ", " +
                MovieEntry.COLUMN_IS_POPULAR + ", " +
                MovieEntry.COLUMN_IS_FAVORITE + ", " +
                MovieEntry.COLUMN_IS_TOP_RATED + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieTrailerEntry.TABLE_NAME + " (" +
                MovieTrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieTrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieTrailerEntry.COLUMN_ID + " TEXT, " +
                MovieTrailerEntry.COLUMN_KEY + " TEXT, " +
                " FOREIGN KEY (" + MovieTrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_ID + "), " +
                " UNIQUE (" + MovieTrailerEntry.COLUMN_ID + ", " +
                MovieTrailerEntry.COLUMN_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieReviewEntry.TABLE_NAME + " (" +
                MovieReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieReviewEntry.COLUMN_ID + " TEXT, " +
                MovieReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                MovieReviewEntry.COLUMN_CONTENT + " TEXT, " +
                MovieReviewEntry.COLUMN_URL + " TEXT, " +
                " FOREIGN KEY (" + MovieReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_ID + "), " +
                " UNIQUE (" + MovieReviewEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieTrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
