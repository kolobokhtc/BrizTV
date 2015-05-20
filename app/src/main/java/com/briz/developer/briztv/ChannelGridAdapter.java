package com.briz.developer.briztv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eng210 on 23.04.2015.
 */
public class ChannelGridAdapter extends ArrayAdapter<Channel> implements Filterable{

    private static final String TAG = ChannelGridAdapter.class.getSimpleName();

    private Filter channelFilter;
    private ArrayList<Channel> channelList;
    private ArrayList<Channel> channelListOriginal;
    private Context context;

    private boolean refreshOrigin = false;

    private ImageLoader imageLoader;
    private DisplayImageOptions imageOptions;
    final ChannelGridAdapter that = this;

    public ChannelGridAdapter(Context context, ArrayList<Channel> channelList) {
        super(context, R.layout.channell_grid_item, channelList);

        this.channelList = channelList;

        if (this.channelListOriginal == null || (this.channelListOriginal != null && this.channelListOriginal.size() == 0) || this.refreshOrigin) {

            this.channelListOriginal  = new ArrayList<>();

            this.channelListOriginal.addAll(channelList);

            Log.d(TAG, "Origin init count:"  + this.channelListOriginal.size());


        }

        this.context = context;

        this.imageLoader = ImageLoader.getInstance();

        imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_drawer)
                .showImageForEmptyUri(R.drawable.ic_drawer)
                .showImageOnFail(R.drawable.ic_drawer)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new RoundedBitmapDisplayer(20)).build();

    }

    public void setRefreshOrigin(boolean refreshOrigin) {

        this.refreshOrigin = refreshOrigin;

    }

    public void setChannelList(ArrayList<Channel> channelList) {

        this.channelList.clear();
        this.channelList.addAll(channelList);
        //this.channelList = new ArrayList<>();

        //this.channelList = channelList;
        Log.d(TAG, "SetChList: " + this.channelList.size());

    }

    public ArrayList<Channel> getChannelList() {

        return this.channelList;

    }

    public int GetChannelsCount() {

        return  this.channelList.size();

    }

    public ArrayList<Channel> getOriginalChannelList() {

        return this.channelListOriginal;

    }


    private class ChannelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            setRefreshOrigin(false);

            FilterResults results = new FilterResults();

            List<Channel> eChannelList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // Без фильтра возвращаем все содержимое адаптера
                eChannelList.addAll(getOriginalChannelList());
                results.values = eChannelList;
                results.count = getOriginalChannelList().size();


            }
            else {
                // Начинаем фильтрацию содержимого адаптера
                List<Channel> nChannelList = new ArrayList<>();

                ArrayList <Channel> tchannelList = new ArrayList<>();
                tchannelList.addAll(getOriginalChannelList());
                Log.d(TAG, "Original count: " + tchannelList.size());

                for (Channel p : tchannelList) {
                    if (p.name.toUpperCase().contains(constraint.toString().toUpperCase()))
                        nChannelList.add(p);
                }

                results.values = nChannelList;
                results.count = nChannelList.size();

            }
            //Log.d(TAG, "ResFilterCount: " + results.values.toString() + " ValCount: " + results.count);
            return results;

        }




        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {

                notifyDataSetInvalidated();

            } else {

                setChannelList((ArrayList<Channel>) results.values) ;
                Log.d(TAG, "ResFilterCount: " +  getChannelList().size());
                notifyDataSetChanged();
            }


        }
    }

    @Override
    public Filter getFilter() {

        if (this.channelFilter == null) {
            this.channelFilter = new ChannelFilter();

        }
        return this.channelFilter;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.channell_grid_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.tvChannelName);
            //holder.description = (TextView) view.findViewById(R.id.tvChannelDescription);
            holder.logo = (ImageView) view.findViewById(R.id.ivChannelIcon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Channel channel = getItem(position);

        holder.name.setText(channel.name);
        //holder.description.setText(channel.logo);
        String logoUrl;
        if (channel.logo.length() > 0) {
            logoUrl = "http://ott.briz.ua" + channel.logo;
        } else {
            logoUrl = "http://ott.briz.ua/stalker_portal/misc/logos/320/no-logo.jpg";
        }
        Log.d(TAG, "convert view for: " + logoUrl);
        imageLoader.displayImage(logoUrl, holder.logo, imageOptions);

        return view;
    }

    static class ViewHolder {
        TextView name;
        TextView description;
        ImageView logo;
    }




}
