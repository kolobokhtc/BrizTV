package com.briz.developer.briztv;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Класс активности авторизации приложения
 *
 */
public class LoginActivity extends ActionBarActivity{

    private static final String TAG = LoginActivity.class.getSimpleName();

    private StalkerClient stalkerClient;
    private StalkerLoader APILoader;

    String etUsername;
    String etPassword;


    FragmentManager fragmentManager;

    SharedPreferences sp;

    @Override
    protected void onResume() {

        super.onResume();

        sp = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        setupPreferences();
        this.loginUser(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fragmentManager = getSupportFragmentManager();

        APILoader = new StalkerLoader(getApplicationContext(), new StalkerClient());

    }

    private void showFragmentByInstance(Fragment fragment, String tag) {

        fragmentManager.beginTransaction()
                .replace(R.id.loginContainer, fragment, tag)
                .commit();

    }



    /**
     * Метод получения настроек приложения
     */
    private void setupPreferences() {

        etUsername = sp.getString(getString(R.string.user_login_pref_key), getString(R.string.user_login_pref_key));
        etPassword = sp.getString(getString(R.string.user_pwd_pref_key), "guest");


    }

    public void savePreferenses(String login, String passwd) {

        sp.edit().putString(getString(R.string.user_login_pref_key), login).putString(getString(R.string.user_pwd_pref_key), passwd).apply();

    }

    /**
     * Метод управления отображением фрагмента прогрессбара/логина
     * @param shPb - показывать прогрессбар или нет
     */
    protected void loginPbViewLogic(boolean shPb) {

        if (shPb) {

            this.showFragmentByInstance(new LoginBusyFragment(), "loginbusy");

        } else {

            this.showFragmentByInstance(loginFormFragment.newInstance(etUsername, etPassword), "loginform");

        }

    }


    /**
     * Метод авторизации пользователя на Stalker
     *
     */
    public void loginUser(boolean getPref){

        if (getPref) this.setupPreferences();


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

                            //Toast.makeText(getApplicationContext(), response.getString("error_description"), Toast.LENGTH_LONG).show();
                            ErrorsToast.showToast(getApplicationContext(), that, response.getString("error_description"));

                        } catch (JSONException e){

                            Log.d(TAG, "LOGIN PARSE ERROR: " + e.toString());

                        } finally {

                            that.loginPbViewLogic(false);

                        }

                    }
                }


            });



        } else {

            Toast.makeText(getApplicationContext(), getString(R.string.enter_correct_login), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Метод вывода активности настроек приложения
     */
    private void showSettingMenu() {

        Intent prefIntent = new Intent(getApplicationContext(), BrizTVSettingsActivity.class);
        startActivity(prefIntent);

    }

    /**
     * Метод восстановления экшн-бара
     */
    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setTitle(getString(R.string.activity_title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.global, menu);
        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings || id == R.id.actions_settings) {

            this.showSettingMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Метод запуска активности-контейнера фрагментов
     */
    private void startHomeActivity() {

        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        homeIntent.putExtra(StalkerClient.class.getCanonicalName(), stalkerClient);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();

    }

}



