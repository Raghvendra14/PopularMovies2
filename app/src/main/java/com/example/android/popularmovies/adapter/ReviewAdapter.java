package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.ReviewTab;
import com.example.android.popularmovies.utils.ViewHolder;

/**
 * Created by Raghvendra on 23-07-2016.
 */
public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_reviews, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.textView = (TextView) view.findViewById(R.id.review_home_page_text);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String text;
        String authorName;
        authorName = cursor.getString(ReviewTab.COL_AUTHOR);
        if(authorName != null) {
            text = "Review By " + authorName;
        }
        else {
            text = "Review Not Available";
        }
        holder.textView.setText(text);
    }

}
