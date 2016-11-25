package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.activity.MainActivity;
import com.example.android.popularmovies.fragment.MainActivityFragment;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.utils.ViewHolder;
import com.squareup.picasso.Picasso;

public class MoviePosterAdapter extends CursorAdapter {
    private final String baseUrl = "http://image.tmdb.org/t/p/w185/";

    public MoviePosterAdapter(Context context, Cursor c, int flags ) {super(context, c, flags);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular_movies, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.icon = (ImageView) view.findViewById(R.id.movie_poster);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String posterPath = cursor.getString(MainActivityFragment.COL_POSTER_PATH);
        int width = MainActivityFragment.getWidth();
        int height = MainActivityFragment.getHeight();

        width = (MainActivity.getPaneMode())? width : (width/2);
        height = (context.getResources().getConfiguration().orientation == 1) ? (height/2) : height;

        Picasso.with(context).load(baseUrl + posterPath).placeholder(R.drawable.android_placeholder).
                error(R.drawable.android_placeholder).resize(width , height).into(holder.icon);
    }

}
