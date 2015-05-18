package com.briz.developer.briztv;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

/**
 * Created by user on 15.05.15.
 */
public class GenresListAdapter extends ArrayAdapter<String> implements Filterable {

    private static final String TAG = ChannelGridAdapter.class.getSimpleName();

    private Filter channelFilter;
    private ArrayList<String> genresList;
    private ArrayList<String> genresListOriginal;

    final GenresListAdapter that = this;



    public GenresListAdapter(Context context, ArrayList<String> genresList) {
        super(context, R.layout.channell_list_item , genresList);



    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
