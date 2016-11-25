package com.example.android.popularmovies.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.MovieTrailerEntry;
import com.example.android.popularmovies.data.MovieContract.MovieReviewEntry;
import com.example.android.popularmovies.data.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMovieDetailsTask extends AsyncTask<String, Void, Void> {
    private static final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();

    private static final String API_KEY = "ENTER YOUR API KEY HERE";

    private final Context mContext;

    private String mSchemeName;
    private String mAuthorityName;
    private String mUrlNumber;
    private String mMovieUrl;
    private ProgressBar progressBar;
    private MovieDbHelper mOpenHelper;


    public FetchMovieDetailsTask(Context context, ProgressBar pb) {
        mContext = context;
        mOpenHelper = new MovieDbHelper(mContext);
        mSchemeName = mContext.getResources().getString(R.string.scheme_name);
        mAuthorityName = mContext.getResources().getString(R.string.authority_movie);
        mUrlNumber = mContext.getResources().getString(R.string.url_number);
        mMovieUrl = mContext.getResources().getString(R.string.movie_url);
        progressBar = pb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(ProgressBar.VISIBLE);

    }

    private boolean DEBUG = true;

    private void getMovieReviewsDetailsFromId(long id) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the raw JSON response as a string.
        String movieReviewDetailsJsonStr = null;
        try {
            // Construct the URL for the TheMovieDb query
            final String APP_KEY_PARAM = "api_key";
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(mSchemeName)
                    .authority(mAuthorityName)
                    .appendPath(mUrlNumber)
                    .appendPath(mMovieUrl)
                    .appendPath(Long.toString(id))
                    .appendPath("reviews")
                    .appendQueryParameter(APP_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builder.toString());

            // Create the request to TheMovieDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                // Stream is empty. No need in parsing.
                return;
            }

            movieReviewDetailsJsonStr = buffer.toString();
            getMovieReviewsDetailsFromJson(movieReviewDetailsJsonStr, id);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void getMovieReviewsDetailsFromJson(String movieReviewDetailsJsonStr, long movieId) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMD_RESULTS = "results";
        final String TMD_AUTHOR = "author";
        final String TMD_ID = "id";
        final String TMD_CONTENT = "content";
        final String TMD_URL = "url";

        try {
            JSONObject movieReviewDetails = new JSONObject(movieReviewDetailsJsonStr);
            JSONArray movieReviewsArray = movieReviewDetails.getJSONArray(TMD_RESULTS);

            if(movieReviewsArray.length() != 0) {
                Vector<ContentValues> cVVector = new Vector<ContentValues>(movieReviewsArray.length());
                for (int i = 0; i < movieReviewsArray.length(); i++) {
                    String author;
                    String id;
                    String content;
                    String url;

                    JSONObject movieReviewData = movieReviewsArray.getJSONObject(i);
                    author = movieReviewData.getString(TMD_AUTHOR);
                    id = movieReviewData.getString(TMD_ID);
                    content = movieReviewData.getString(TMD_CONTENT);
                    url = movieReviewData.getString(TMD_URL);

                    ContentValues movieReviewValues = new ContentValues();

                    movieReviewValues.put(MovieReviewEntry.COLUMN_ID, id);
                    movieReviewValues.put(MovieReviewEntry.COLUMN_AUTHOR, author);
                    movieReviewValues.put(MovieReviewEntry.COLUMN_MOVIE_ID, movieId);
                    movieReviewValues.put(MovieReviewEntry.COLUMN_CONTENT, content);
                    movieReviewValues.put(MovieReviewEntry.COLUMN_URL, url);

                    cVVector.add(movieReviewValues);
                }

                int inserted = 0;
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(MovieReviewEntry.CONTENT_URI, cvArray);
                }

            }
            else {
                ContentValues movieReviewValue = new ContentValues();
                movieReviewValue.put(MovieTrailerEntry.COLUMN_MOVIE_ID, movieId);
                Uri insertedId = mContext.getContentResolver().insert(MovieReviewEntry.CONTENT_URI, movieReviewValue);

            }

        } catch(JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private void getMovieTrailerDetailsFromId(long id) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the raw JSON response as a string.
        String movieTrailerDetailsJsonStr = null;
        try {
            // Construct the URL for the TheMovieDb query
            final String MOVIE_DETAILS_BASE_URL = "http://api.themoviedb.org/3/movie/" + id +"/videos?";
            final String APP_KEY_PARAM = "api_key";
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(mSchemeName)
                    .authority(mAuthorityName)
                    .appendPath(mUrlNumber)
                    .appendPath(mMovieUrl)
                    .appendPath(Long.toString(id))
                    .appendPath("videos")
                    .appendQueryParameter(APP_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builder.toString());

            // Create the request to TheMovieDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                // Stream is empty. No need in parsing.
                return;
            }

            movieTrailerDetailsJsonStr = buffer.toString();
            getMovieTrailerDetailsFromJson(movieTrailerDetailsJsonStr, id);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void getMovieTrailerDetailsFromJson(String movieTrailerDetailsJsonStr, long movieId) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMD_RESULTS = "results";
        final String TMD_KEY = "key";
        final String TMD_ID = "id";
        final String TMD_TYPE = "type";

        try {
            JSONObject movieTrailerDetails = new JSONObject(movieTrailerDetailsJsonStr);
            JSONArray movieTrailersArray = movieTrailerDetails.getJSONArray(TMD_RESULTS);

            if(movieTrailersArray.length() != 0) {
                Vector<ContentValues> cVVector = new Vector<ContentValues>(movieTrailersArray.length());
                for (int i = 0; i < movieTrailersArray.length(); i++) {
                    String key;
                    String id;
                    String type;

                    JSONObject movieTrailerData = movieTrailersArray.getJSONObject(i);
                    key = movieTrailerData.getString(TMD_KEY);
                    id = movieTrailerData.getString(TMD_ID);
                    type = movieTrailerData.getString(TMD_TYPE);

                    ContentValues movieTrailerValues = new ContentValues();

                    movieTrailerValues.put(MovieTrailerEntry.COLUMN_ID, id);
                    movieTrailerValues.put(MovieTrailerEntry.COLUMN_KEY, key);
                    movieTrailerValues.put(MovieTrailerEntry.COLUMN_MOVIE_ID, movieId);

                    cVVector.add(movieTrailerValues);
                }

                int inserted = 0;
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(MovieTrailerEntry.CONTENT_URI, cvArray);
                }

            }
            else {
                ContentValues movieTrailerValue = new ContentValues();
                movieTrailerValue.put(MovieTrailerEntry.COLUMN_MOVIE_ID, movieId);
                Uri insertedId = mContext.getContentResolver().insert(MovieTrailerEntry.CONTENT_URI, movieTrailerValue);

            }

        } catch(JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private void getMovieDetailsFromJson(String movieDetailsJsonStr, String orderBy) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMD_RESULTS = "results";
        final String TMD_POSTER_PATH = "poster_path";
        final String TMD_OVERVIEW = "overview";
        final String TMD_RELEASE_DATE = "release_date";
        final String TMD_TITLE = "original_title";
        final String TMD_USER_RATING = "vote_average";
        final String TMD_ID = "id";
        final String TMD_POPULARITY = "popularity";

        String categoryType = null;

        try {
            JSONObject movieDetails = new JSONObject(movieDetailsJsonStr);
            JSONArray moviesArray = movieDetails.getJSONArray(TMD_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            switch (orderBy) {
                case MovieEntry.POPULAR: {
                    categoryType = MovieEntry.COLUMN_IS_POPULAR;
                    break;
                }
                case MovieEntry.FAVORITE: {
                    categoryType = MovieEntry.COLUMN_IS_FAVORITE;
                    break;
                }
                case MovieEntry.TOP_RATED: {
                    categoryType = MovieEntry.COLUMN_IS_TOP_RATED;
                    break;
                }
                default: {
                    // By default, the category for ordering the movies is popular
                    categoryType = MovieEntry.COLUMN_IS_POPULAR;
                    break;
                }
            }

            Long[] movId = new Long[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                String posterPath;
                String overview;
                String releaseDate;
                String originalTitle;
                double voteAverage;
                long id;
                int movieIdFlag;
                double popularity;

                JSONObject movieData = moviesArray.getJSONObject(i);
                posterPath = movieData.getString(TMD_POSTER_PATH).substring(0);
                overview = movieData.getString(TMD_OVERVIEW);
                releaseDate = movieData.getString(TMD_RELEASE_DATE);
                originalTitle = movieData.getString(TMD_TITLE);
                voteAverage = movieData.getDouble(TMD_USER_RATING);
                id = movieData.getLong(TMD_ID);
                popularity = movieData.getDouble(TMD_POPULARITY);

                movId[i] = id;
                movieIdFlag = checkMovieById(id, categoryType);

                ContentValues movieValues = new ContentValues();

                if (movieIdFlag == 2) {
                    movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
                    movieValues.put(MovieEntry.COLUMN_USER_RATING, voteAverage);
                    int updatedRow = 0;
                    String selection = MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_ID + " = ? ";
                    updatedRow = mContext.getContentResolver().update(MovieEntry.CONTENT_URI, movieValues, selection, new String[] {Long.toString(id)});
                }
                else if (movieIdFlag == 1) {
                    movieValues.put(categoryType, 1);
                    int updatedRow = 0;
                    String selection = MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_ID + " = ? ";
                    updatedRow = mContext.getContentResolver().update(MovieEntry.CONTENT_URI, movieValues, selection, new String[]{Long.toString(id)});
                }
                else if (movieIdFlag == 0) {
                    movieValues.put(MovieEntry.COLUMN_ID, id);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, originalTitle);
                    movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
                    movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                    movieValues.put(MovieEntry.COLUMN_USER_RATING, voteAverage);
                    movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
                    movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
                    movieValues.put(categoryType, 1);

                    // To store the trailer information in the database.
                    getMovieTrailerDetailsFromId(id);
                    // To store the review information in the database.
                    getMovieReviewsDetailsFromId(id);

                    cVVector.add(movieValues);
                }
            }

            int inserted = 0;
            //add to database
            if (cVVector.size() > 0 && !cVVector.isEmpty()) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            deleteUnnecessaryData(movId, categoryType);


        } catch(JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
    protected Void doInBackground(String... params) {

        if(params.length == 0 || params[0].equals("favorite")) {
            return null;
        }

        String categoryOrder = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the raw JSON response as a string.
        String movieDetailsJsonStr = null;
        try {
            // Construct the URL for the TheMovieDb query
            final String APP_KEY_PARAM = "api_key";
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(mSchemeName)
                    .authority(mAuthorityName)
                    .appendPath(mUrlNumber)
                    .appendPath(mMovieUrl)
                    .appendPath(categoryOrder)
                    .appendQueryParameter(APP_KEY_PARAM, API_KEY)
                    .build();
            URL url = new URL(builder.toString());

            // Create the request to TheMovieDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                // Stream is empty. No need in parsing.
                return null;
            }

            movieDetailsJsonStr = buffer.toString();
            getMovieDetailsFromJson(movieDetailsJsonStr, categoryOrder);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBar.setVisibility(ProgressBar.GONE);
    }

    int checkMovieById (long id, String categoryType) {
        long movieId = 0;
        int categoryFlag = 0;
        int returnFlag = 0;
        final String [] projection = {Long.toString(id), categoryType};

        final int COL_ID = 0;
        final int COL_ORDER_BY = 1;

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieUriWithId(id),
                projection,
                null,
                null,
                null);
        if (movieCursor != null) {
            if (movieCursor.moveToFirst()) {
                movieId = movieCursor.getLong(COL_ID);
                returnFlag = 1;
                categoryFlag = movieCursor.getInt(COL_ORDER_BY);
                if (categoryFlag == 1 && movieId != 0) {
                    returnFlag = 2;
                }

            }
            movieCursor.close();
        }

        return returnFlag;
    }

    String makePlaceHolder(int length) {
        if (length < 1) {
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(length * 2 -1);
            sb.append("?");
            for (int i = 1; i < length; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    void deleteUnnecessaryData(Long[] movId, String category) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String[] movieIds = new String[movId.length];
        for (int i = 0; i < movId.length; i++) {
            movieIds[i] = movId[i].toString();
        }

        String subQuery = "SELECT " + MovieContract.MovieEntry.COLUMN_ID + " FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " +
                category + " = 1 AND " + MovieContract.MovieEntry.COLUMN_ID + " NOT IN (" + makePlaceHolder(movId.length) + " );";
        Cursor cursor = db.rawQuery(subQuery, movieIds);
        if(cursor != null) {
            long id;
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {
                id = cursor.getLong(0);
                int updatedRow = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_ID + " = ?",
                        new String[] {Long.toString(id)});
            }
            cursor.close();
        }
        db.close();

    }

}