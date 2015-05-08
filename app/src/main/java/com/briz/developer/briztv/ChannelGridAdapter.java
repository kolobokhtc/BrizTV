package com.briz.developer.briztv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by eng210 on 23.04.2015.
 */
public class ChannelGridAdapter extends ArrayAdapter<Channel> {

    private static final String TAG = ChannelGridAdapter.class.getSimpleName();

    private static Filter channelFilter;
    private ArrayList<Channel> channelList;
    private ArrayList<Channel> channelListOriginal;
    private Context context;

    private ImageLoader imageLoader;
    private DisplayImageOptions imageOptions;

    public ChannelGridAdapter(Context context, ArrayList<Channel> channelList) {
        super(context, R.layout.channell_list_item, channelList);

        this.channelList = channelList;
        this.channelListOriginal = channelList;
        this.context = context;

        this.imageLoader = ImageLoader.getInstance();

        imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_drawer)
                .showImageForEmptyUri(R.drawable.ic_drawer)
                .showImageOnFail(R.drawable.ic_drawer)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new RoundedBitmapDisplayer(20)).build();

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
