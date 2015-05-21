package com.briz.developer.briztv;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;



public class LoginActivity extends Activity{

    private static final String TAG = LoginActivity.class.getSimpleName();

    private StalkerClient stalkerClient;
    private StalkerLoader APILoader;

    TextView tvErrorMessage;
    String etUsername;
    String etPassword;
    ProgressBar eLoginPb;
    Button eLoginBtn;
    SharedPreferences sp;


    @Override
    protected void onResume() {
        super.onResume();
        this.loginPbViewLogic(false);

        sp = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        setupPreferences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        APILoader = new StalkerLoader(getApplicationContext(), new StalkerClient());

        tvErrorMessage = (TextView) findViewById(R.id.login_error);

        eLoginPb = (ProgressBar) findViewById(R.id.loginProgress);
        eLoginBtn = (Button) findViewById(R.id.btnLogin);



        this.loginPbViewLogic(false);


    }



    private void setupPreferences() {

        etUsername = sp.getString("user_login", "user_login");
        etPassword= sp.getString("user_pwd", "guest");

        if (etUsername.equals("user_login")) {

            Intent prefIntent = new Intent(getApplicationContext(), BrizTVSettingsActivity.class);
            startActivity(prefIntent);
        }



    }


    protected void loginPbViewLogic(boolean shPb) {

        eLoginPb.setVisibility((shPb) ? View.VISIBLE : View.INVISIBLE);
        eLoginBtn.setVisibility((shPb) ? View.INVISIBLE : View.VISIBLE);

    }

    public void loginUser(View view){


        final LoginActivity that = this;


        this.loginPbViewLogic(true);


        RequestParams params = new RequestParams();

        if (!TextUtils.isEmpty(etUsername) && !TextUtils.isEmpty(etPassword)){

            params.put("grant_type", "password");
            params.put("username", etUsername);
            params.put("password", etPassword);

            APILoader.login(params, new StalkerLoader.OnJSONResponseCallback() {
                @Override
                public void onJSONResponse(boolean success, JSONObject response) {

                    Log.d(TAG, "LOGIN RESULT: " + success + " | RESPONSE: " + response.toString());



                    if (success){

                        stalkerClient = APILoader.getStalkerClient();

                        Log.d(TAG, "CLIENT ID: " + stalkerClient.getUserId());

                        startHomeActivity();

                    } else {


                        try{

                            tvErrorMessage.setText(response.getString("error_description"));
                            Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();

                        } catch (JSONException e){

                            Log.d(TAG, "LOGIN PARSE ERROR: " + e.toString());

                        } finally {

                            that.loginPbViewLogic(false);

                        }

                    }
                }


            });



        } else {

            Toast.makeText(getApplicationContext(), "Пожалуйста введите корретный логин и пароль", Toast.LENGTH_LONG).show();
        }

    }

    private void showSettingMenu() {

        Intent prefIntent = new Intent(getApplicationContext(), BrizTVSettingsActivity.class);
        startActivity(prefIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.global, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            this.showSettingMenu();
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void startHomeActivity() {

        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        homeIntent.putExtra(StalkerClient.class.getCanonicalName(), stalkerClient);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }

}



