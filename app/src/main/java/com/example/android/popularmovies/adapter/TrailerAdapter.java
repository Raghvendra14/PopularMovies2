package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.TrailerTab;
import com.example.android.popularmovies.utils.ViewHolder;
import com.squareup.picasso.Picasso;

public class TrailerAdapter extends CursorAdapter {
    private final String thumbnailBaseUrl = "https://img.youtube.com/vi/";

    public TrailerAdapter (Context context, Cursor c, int flags) { super(context, c, flags);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_trailers, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.icon = (ImageView) view.findViewById(R.id.trailer_thumbnail);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        String videoUrl;
        videoUrl = cursor.getString(TrailerTab.COL_MOVIE_TRAILER_KEY);
        int width = TrailerTab.getWidth();
        int height = TrailerTab.getHeight();
        height = (context.getResources().getConfiguration().orientation == 2) ? height : (height/2);

        String thumbnail = thumbnailBaseUrl + videoUrl + "/0.jpg";

        if (videoUrl != null) {
            Picasso.with(context).load(thumbnail).placeholder(R.drawable.play_logo).error(R.drawable.play_logo)
                    .resize(width, height).into(holder.icon);
        }
        else {
            Picasso.with(context).load(R.drawable.android_placeholder).resize(width, height).into(holder.icon);
        }

    }
}
