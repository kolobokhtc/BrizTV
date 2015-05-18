package com.briz.developer.briztv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 15.05.15.
 */
public class GenresListAdapter extends ArrayAdapter<Genre> implements Filterable {

    private static final String TAG = ChannelGridAdapter.class.getSimpleName();

    private Filter genreFilter;
    private ArrayList<Genre> genresList;
    private ArrayList<Genre> genresListOriginal;

    final GenresListAdapter that = this;



    public GenresListAdapter(Context context, ArrayList<Genre> genresList) {

        super(context, R.layout.genres_list_item, genresList);


        this.genresList = genresList;
        this.genresListOriginal= genresList;


    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.genres_list_item, parent, false);
            holder = new ViewHolder();
            holder.genreName = (TextView) view.findViewById(R.id.genreOption);
            view.setTag(holder);
        } else {

            holder = (ViewHolder) view.getTag();
        }

        Genre genre = getItem(position);

        holder.genreName.setText(genre.genre_desc);

        return view;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    static class ViewHolder {

        TextView genreName;

    }
}
