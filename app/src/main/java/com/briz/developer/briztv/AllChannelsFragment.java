package com.briz.developer.briztv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.GridView;
import android.widget.ListView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by eng210 on 21.04.2015.
 */
public class AllChannelsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = AllChannelsFragment.class.getSimpleName();
    private static final String BASE_URL = new String("http://ott.briz.ua");

    static ArrayList<Channel> channels = new ArrayList<Channel>();
    static ArrayList<String> resultRow;

    private boolean list_visibile = false;
    private ListView lv;
    private GridView gv;

    private ChannelListAdapter channelListAdapter;
    private ChannelGridAdapter channelGridAdapter;
    private View mView;

    private static StalkerLoader APILoader;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .build();
        ImageLoader.getInstance().init(config);

    }

    public void setAPILoader(StalkerLoader loader){
        this.APILoader = loader;
    }

    public StalkerLoader getAPILoader(){
        return APILoader;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_channels, container, false);

        mView = rootView;

        return rootView;
    }

    public void showChannels(){

        Log.d(TAG, "CHANNELS ON CREATE VIEW COUNT: " + channels.size());

        //channelListAdapter = new ChannelListAdapter(getActivity(), channels);
        channelGridAdapter = new ChannelGridAdapter(getActivity(), channels);
        Log.d(TAG, "INIT ADAPTER: " + channels.size());
        //lv = (ListView) mView.findViewById(R.id.lv_channels_list);
        gv = (GridView) mView.findViewById(R.id.gv_channels_grid);
        Log.d(TAG, "SET ADAPTER: " + channels.size());
        gv.setAdapter(channelGridAdapter);
       // lv.setOnItemClickListener(this);
        gv.setOnItemClickListener(this);

    }


    public void getChannels(){

        //progressDialog.show();

        StalkerClient sc = APILoader.getStalkerClient();

        String url = new String("http://v2.api.ott.briz.ua/stalker_portal/api/users/"+sc.getUserId()+"/tv-channels");
        Log.d(TAG, "START CHANNELS REQUEST");
        APILoader.loader(url, new StalkerLoader.OnJSONResponseCallback() {
            @Override
            public void onJSONResponse(boolean success, JSONObject response) {
                //progressDialog.hide();
                try {
                    if (response.has("status")) {

                        JSONArray web_channels = response.getJSONArray("results");

                        channels = parseChannels(web_channels);
                        showChannels();
                    }

                    if (response.has("error")) {
                        //Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(), "Error in code..((", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Log.d(TAG, "GET CHANNELS REQUEST COMPLETE: " + response.toString());
            }
        });
    }

    public static ArrayList<Channel> parseChannels(JSONArray data){

        ArrayList<Channel> channelsFromAjaxData = new ArrayList<Channel>();

        try{

            for(int j = 0; j<data.length();j++){

                JSONObject json_data = data.getJSONObject(j);
                Channel resultRow = new Channel();

                resultRow.channel_id = json_data.getString("id");
                resultRow.name = json_data.getString("name");
                resultRow.logo = json_data.getString("logo");

                channelsFromAjaxData.add(resultRow);
            }

        } catch (JSONException e ){

        }

        Log.d(TAG, "PARSED CHANNELS COUNT: " + channelsFromAjaxData.size());

        return channelsFromAjaxData;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("TEST", "click listener:" + channels.get(1).name);

        final Channel channel = channels.get(position);

        StalkerClient sc = APILoader.getStalkerClient();

        String url = new String("http://v2.api.ott.briz.ua/stalker_portal/api/users/"+sc.getUserId()+"/tv-channels/"+channel.channel_id+"/link");

        APILoader.loader(url, new StalkerLoader.OnJSONResponseCallback() {
            @Override
            public void onJSONResponse(boolean success, JSONObject response) {

                try {
                    if (response.has("status")) {

                        String channelLink = response.getString("results");
                        Log.d(TAG, "Channel LINK is: " + channelLink);

                        Intent player = new Intent();
                        player.setAction(Intent.ACTION_VIEW);

                        player.setDataAndType(Uri.parse(channelLink), "video/*");
                        player.putExtra("title", channel.name);
                        player.putExtra("secure_uri", true);
                        startActivity(player);
                    }

                    if (response.has("error")) {
                        //Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(), "Error in code..((", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Log.d(TAG, "GET CHANNELS REQUEST COMPLETE: " + response.toString());
            }
        });

    }

}
