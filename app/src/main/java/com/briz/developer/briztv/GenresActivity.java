package com.briz.developer.briztv;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;


public class GenresActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ListView lwGenres;
    View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        lwGenres = (ListView) findViewById(R.id.lwGenresList);
        //View vv = new Button(getBaseContext());
        //lwGenres.addView(vv);
        lwGenres.setOnItemClickListener(this);


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



    private void returnActivityResult() {

        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent.putExtra("genre_id", "musics"));
        finish();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        returnActivityResult();

    }
}
