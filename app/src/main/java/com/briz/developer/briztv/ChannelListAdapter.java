package com.briz.developer.briztv;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by eng210 on 21.04.2015.
 */
public class ChannelListAdapter  extends ArrayAdapter<Channel>{

    private static final String TAG = ChannelListAdapter.class.getSimpleName();

    private static Filter channelFilter;
    private ArrayList<Channel> channelList;
    private ArrayList<Channel> channelListOriginal;
    private Context context;

    private ImageLoader imageLoader;
    private DisplayImageOptions imageOptions;

    public ChannelListAdapter(Context context, ArrayList<Channel> channelList) {
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

        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.channell_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.tvChannelName);
            holder.description = (TextView) view.findViewById(R.id.tvChannelDescription);
            holder.logo = (ImageView) view.findViewById(R.id.ivChannelIcon);
            holder.logo.setMaxHeight(100);
            holder.logo.setMaxWidth(100);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Channel channel = getItem(position);

        holder.name.setText(channel.name);
        holder.description.setText(channel.logo);
        if (channel.logo.length() > 0){

            Log.d(TAG, "convert view for: " + channel.logo);
            String logoUrl = new String("http://ott.briz.ua" + channel.logo);
            imageLoader.displayImage(logoUrl, holder.logo, imageOptions);

        }

        return view;
    }

    static class ViewHolder{
        TextView name;
        TextView description;
        ImageView logo;
    }

}


