package com.briz.developer.briztv;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GenresActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = GenresActivity.class.getSimpleName();

    ListView lwGenres;
    View mView;
    ArrayList<Genre> genres;
    StalkerLoader APILoader;
    StalkerClient sc;
    Integer user_id;
    String genreID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);


        sc = getIntent().getParcelableExtra("StalkerClient");
        Log.d(TAG, "stalkerClient imported: " + sc.getAccessToken());

        APILoader = new StalkerLoader(getApplicationContext(), sc);

        user_id = sc.getUserId();

        getGenres();


    }




    private void showGenres() {

        lwGenres = (ListView) findViewById(R.id.lwGenresList);

        ArrayList<Genre> gnrs = new ArrayList<>();

        Genre gnr = new Genre();
        gnr.genre_id = "all";
        gnr.genre_desc = "все жанры";

        gnrs.clear();
        gnrs.add(gnr);

        gnrs.addAll(genres);

        genres.clear();
        genres.addAll(gnrs);

        lwGenres.setAdapter(new GenresListAdapter(this, genres));

        lwGenres.setOnItemClickListener(this);


    }


    public static ArrayList<Genre> parseGenres(JSONArray data) {

        ArrayList<Genre> genresFromAjaxData = new ArrayList<>();

        try{

            for(int j = 0; j<data.length();j++){

                JSONObject json_data;
                json_data = data.getJSONObject(j);
                Genre resultRow;
                resultRow = new Genre();

                resultRow.genre_id = json_data.getString("id");
                resultRow.genre_desc = json_data.getString("title");

                genresFromAjaxData.add(resultRow);
            }

        } catch (JSONException e ){

        }

        Log.d(TAG, "PARSED GENRES COUNT: " + genresFromAjaxData.size());

        return genresFromAjaxData;

    }


    public void getGenres() {


        String url = UrlSettings.getResUrl() + user_id + "/tv-genres";
        APILoader.loader(url, new StalkerLoader.OnJSONResponseCallback() {
            @Override
            public void onJSONResponse(boolean success, JSONObject response) {
                //progressDialog.hide();
                try {
                    if (response.has("status")) {

                        JSONArray web_genres = response.getJSONArray("results");

                        genres = parseGenres(web_genres);
                        showGenres();
                    }

                    if (response.has("error")) {
                        //Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(), "Error in code..((", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Log.d(TAG, "GET GENRES REQUEST COMPLETE: " + response.toString());
            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_genres, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void returnActivityResult(String genre_id) {

        genreID = genre_id;

        Intent returnIntent = new Intent();
        returnIntent.putExtra("genre_id", genreID);
        setResult(RESULT_OK, returnIntent);


        finish();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "Item clicked: " + position);

        final Genre genre;
        genre = genres.get(position);

        this.returnActivityResult(genre.genre_id);

    }

    @Override
    protected void onDestroy() {

        if (genreID == null) {

            Intent returnIntent = new Intent();
            returnIntent.putExtra("genre_id", "all");
            setResult(RESULT_OK, returnIntent);

        }

        super.onDestroy();



        Log.d(TAG, "Item genre: " + genreID);
    }
}
