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
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Класс фрагмента списка каналов (вид сеткой)
 * @version 21.04.2015.
 */
public class AllChannelsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = AllChannelsFragment.class.getSimpleName();


    static ArrayList<Channel> channels = new ArrayList<>();
    static ArrayList<String> resultRow;
    final AllChannelsFragment that = this;

    private boolean list_visibile = false;
    private ListView lv;
    private GridView gv;

    private ChannelListAdapter channelListAdapter;
    private ChannelGridAdapter channelGridAdapter;
    private StalkerClient sc;
    private View mView;
    Genre genreFlag;

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

        genreFlag = new Genre();
        genreFlag.InitGenres(); // Инициализирум структуру жанров значением "все жанры"

    }


    /**
     * Метод установки загрузчика Stalker
     * @param loader - загрузчик Stalker
     */
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


    /**
     * Метод инициализации GUI сетки каналов
     */
    private void initChannelsUI() {

        if (this.gv == null) { this.gv = (GridView) mView.findViewById(R.id.gv_channels_grid); }
        //this.gv.refreshDrawableState();
        this.gv.setAdapter(channelGridAdapter);
        //this.gv.refreshDrawableState();

    }


    /**
     * Метод показа сетки каналов
     */
    public void showChannels(){

        Log.d(TAG, "CHANNELS ON CREATE VIEW COUNT: " + channels.size());

        channelGridAdapter = (!hasChannels()) ? new ChannelGridAdapter(getActivity(), channels) : channelGridAdapter;
        //channelGridAdapter = new ChannelGridAdapter(getActivity(), channels);
        this.channelGridAdapter.setRefreshOrigin(true);
        Log.d(TAG, "INIT ADAPTER: " + channels.size());
        Log.d(TAG, "SET ADAPTER: " + channels.size());


        initChannelsUI();

        gv.setOnItemClickListener(this);

        this.showGenresInfo(genreFlag, channels.size());


    }

    public boolean hasChannels() {

        return (channelGridAdapter != null && channelGridAdapter.GetChannelsCount() > 0);

    }

    /**
     * Метод вызова фильтрации содержимого сетки каналов по названию канала
     * @param chs - часть фильтра названия
     */
    public void ApplyFilter(CharSequence chs) {

        //CharSequence chsb = "";
        //this.channelGridAdapter.getFilter().filter(chsb.toString());
        this.channelGridAdapter.getFilter().filter(chs.toString());

    }

    /**
     * Метод получения списка всех каналов без обновления
     */
    public void getChannels(){

        this.getChannels(false);

    }

    /**
     * Метод получения списка всех каналов
     * @param reFresh - обновлять или нет список каналов
     */
    public void getChannels(boolean reFresh){

        setupStalkerClient();

        String url = UrlSettings.getResUrl() + this.user_id + "/tv-channels";

        requestChannels(url, reFresh);

    }

    /**
     * Метод получения списка каналов по определенному жанру
     * @param genre_id - идентификатор жанра видео
     */
    public void getChannels(String genre_id) {

        this.setupStalkerClient();

        String url = UrlSettings.getResUrl() + this.user_id + "/tv-genres/" + genre_id + "/tv-channels";

        this.requestChannels(url, true);

    }

    /**
     * Метод вызова активности выбора жанра видео
     */
    public void startChooseGenres() {

        Intent genresIntent = new Intent(getActivity().getBaseContext(), GenresActivity.class);
        StalkerClient sc = APILoader.getStalkerClient();
        genresIntent.putExtra("StalkerClient", sc);
        startActivityForResult(genresIntent, 7);

    }


    /**
     * Обработчик возврата результата от дочерней активности
     * @param requestCode - код запроса
     * @param resultCode - код возврата
     * @param data - данные из намерений
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Genre genreItem = new Genre();
        genreItem.InitGenres();

        try {

            super.onActivityResult(requestCode, resultCode, data);

            genreItem.genre_id = data.getStringExtra("genre_id");
            genreItem.genre_desc = data.getStringExtra("genre_desc");

        } catch (NullPointerException e) {

            genreItem.InitGenres();

        } finally {

            Log.d("Genre", genreItem.genre_desc + "  " + resultCode);

            this.onGenreResult(genreItem);

        }


    }

    /**
     * Метод вывода оповещения о выбранном жанре
     * @param genreItem - выбранный объект жанра
     * @param channels_count - количество найденных каналов
     */
    private void showGenresInfo(Genre genreItem, int channels_count) {

        String postMsg = (genreItem.genre_id.equals("all")) ? "!" : " " + getString(R.string.genre)  + " " + genreItem.genre_desc;

        String firstMesg = (channels_count > 0) ? getString(R.string.genre_found) + " " + channels_count +
                " " + getString(R.string.genre_channelses) + postMsg : getString(R.string.genre_channels) + postMsg +
                " " + getString(R.string.genre_not_found);

        Toast.makeText(getActivity().getApplicationContext(), firstMesg, Toast.LENGTH_LONG).show();

    }

    /**
     * Метод-роутер вызова нужного метода получения списка каналов
     * @param genreItem - - выбранный объект жанра
     */
    private void onGenreResult(Genre genreItem) {

        genreFlag.InitGenres(genreItem);

        if (genreItem.genre_id.equals("all")) {

            this.getChannels(true);

        } else {

            this.getChannels(genreItem.genre_id);

        }


    }

    /**
     * Метод получения списка каналов из ресурса Stalker RESTfull
     * @param url - URL русурса Stalker RESTfull API
     * @param reFresh - обновить значения ?
     */
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
                        channels.clear();

                        channels = parseChannels(web_channels);
                        showChannels();
                    }

                    if (response.has("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error in code..((", Toast.LENGTH_LONG).show();
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

    /**
     * Метод парсинга списка каналов
     * @param data - JSON данные от talker RESTfull API
     * @return - коллекция каналов типа Channel
     */
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Log.d("TEST", "click listener:" + channels.get(1).name);

        final Channel channel;
        channel = channels.get(position);

        this.setupStalkerClient();

        String url = UrlSettings.getResUrl() + this.user_id + "/tv-channels/"+channel.channel_id + "/link";

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

                        Toast.makeText(getActivity().getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    Toast.makeText(getActivity().getApplicationContext(), "Error in code..((", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }

                Log.d(TAG, "GET CHANNELS REQUEST COMPLETE: " + response.toString());
            }
        });

    }

}
