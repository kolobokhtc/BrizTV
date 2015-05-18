package com.briz.developer.briztv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.Filter;
import android.widget.Filterable;
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
public class AllChannelsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnLongClickListener {

    private static final String TAG = AllChannelsFragment.class.getSimpleName();
    private static final String BASE_URL_V2 = "http://v2.api.ott.briz.ua";
    private static final String BASE_URL = "http://ott.briz.ua";
    private static final String RES_URL = BASE_URL_V2 + "/stalker_portal/api/users/";

    static ArrayList<Channel> channels = new ArrayList<Channel>();
    static ArrayList<String> resultRow;
    final AllChannelsFragment that = this;

    private boolean list_visibile = false;
    private ListView lv;
    private GridView gv;

    private ChannelListAdapter channelListAdapter;
    private ChannelGridAdapter channelGridAdapter;
    private StalkerClient sc;
    private View mView;

    private Integer user_id;

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
        APILoader = loader;
    }

    public StalkerLoader getAPILoader(){
        return APILoader;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_channels, container, false);

        mView = rootView;

        return rootView;
    }


    public Filter.FilterListener onFilterCompleted() {

        return new Filter.FilterListener() {

            @Override
            public void onFilterComplete(int count) {

                initChannelsUI();


                Log.d(TAG, "ADAPTER: " + that.channelGridAdapter.getChannelList().toString() + " Count: " + count);

            }
        };

    }

    private void initChannelsUI() {

        this.gv = (GridView) mView.findViewById(R.id.gv_channels_grid);
        this.gv.refreshDrawableState();
        this.gv.setAdapter(channelGridAdapter);
        this.gv.refreshDrawableState();

    }




    public void showChannels(){

        Log.d(TAG, "CHANNELS ON CREATE VIEW COUNT: " + channels.size());

        //channelListAdapter = new ChannelListAdapter(getActivity(), channels);
        channelGridAdapter = (!hasChannels()) ? new ChannelGridAdapter(getActivity(), channels) : channelGridAdapter;
        Log.d(TAG, "INIT ADAPTER: " + channels.size());
        //lv = (ListView) mView.findViewById(R.id.lv_channels_list);
        //this.gv = (GridView) mView.findViewById(R.id.gv_channels_grid);
        Log.d(TAG, "SET ADAPTER: " + channels.size());

        //gv.setAdapter(channelGridAdapter);

        initChannelsUI();
        //this.getFilter().filter(chs);

        //gv.setAdapter(channelGridAdapter);

       // lv.setOnItemClickListener(this);
        gv.setOnItemClickListener(this);
        gv.setOnLongClickListener(this);






        //Log.d(TAG, "ADAPTER: " + channelGridAdapter.getChannelList().toString() + " Count: " + channelGridAdapter.getChannelList().size());

    }

    public boolean hasChannels() {

        return (channelGridAdapter != null && channelGridAdapter.GetChannelsCount() > 0);

    }


    public void ApplyFilter() {

        CharSequence chs = "Банк";
        CharSequence chsb = "";
        this.channelGridAdapter.getFilter().filter(chsb.toString());//, this.onFilterCompleted());
        this.channelGridAdapter.getFilter().filter(chs.toString());//, this.onFilterCompleted());

    }

    public void getChannels(){

        //progressDialog.show();


        setupStalkerClient();

        String url = RES_URL + this.user_id + "/tv-channels";

        requestChannels(url, false);

    }


    public void getChannels(String genre_id) {

        this.setupStalkerClient();

        String url = RES_URL + this.user_id + "/tv-genres/" + genre_id + "/tv-channels";

        this.requestChannels(url, true);

    }

    private void requestChannels(String url, boolean reFresh) {

        Log.d(TAG, "START CHANNELS REQUEST");

        if (hasChannels() && !reFresh) return;

        getAPILoader().loader(url, new StalkerLoader.OnJSONResponseCallback() {
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

    private void setupStalkerClient() {

        this.sc = getAPILoader().getStalkerClient();
        this.user_id = this.sc.getUserId();

    }

    public static ArrayList<Channel> parseChannels(JSONArray data){

        ArrayList<Channel> channelsFromAjaxData = new ArrayList<>();

        try{

            for(int j = 0; j<data.length();j++){

                JSONObject json_data;
                json_data = data.getJSONObject(j);
                Channel resultRow;
                resultRow = new Channel();

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
    public boolean onLongClick(View v) {

        this.ApplyFilter();

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Log.d("TEST", "click listener:" + channels.get(1).name);

        final Channel channel;
        channel = channels.get(position);

        this.setupStalkerClient();
        this.ApplyFilter();

        String url = RES_URL + this.user_id + "/tv-channels/"+channel.channel_id + "/link";

        getAPILoader().loader(url, new StalkerLoader.OnJSONResponseCallback() {
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
