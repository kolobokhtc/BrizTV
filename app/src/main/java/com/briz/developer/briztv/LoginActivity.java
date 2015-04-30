package com.briz.developer.briztv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText etUsername;
    EditText etPassword;
    ProgressBar eLoginPb;
    Button eLoginBtn;

    @Override
    protected void onResume() {
        super.onResume();
        this.loginPbViewLogic(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        APILoader = new StalkerLoader(getApplicationContext(), new StalkerClient());

        tvErrorMessage = (TextView) findViewById(R.id.login_error);
        etUsername = (EditText) findViewById(R.id.login_email);
        etPassword = (EditText) findViewById(R.id.login_password);
        eLoginPb = (ProgressBar) findViewById(R.id.loginProgress);
        eLoginBtn = (Button) findViewById(R.id.btnLogin);

        this.loginPbViewLogic(false);


    }


    protected void loginPbViewLogic(boolean shPb) {

        eLoginPb.setVisibility((shPb) ? View.VISIBLE : View.INVISIBLE);
        eLoginBtn.setVisibility((shPb) ? View.INVISIBLE : View.VISIBLE);

    }

    public void loginUser(View view){

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();


        this.loginPbViewLogic(true);


        RequestParams params = new RequestParams();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){

            params.put("grant_type", "password");
            params.put("username", username);
            params.put("password", password);

            APILoader.login(params, new StalkerLoader.OnJSONResponseCallback() {
                @Override
                public void onJSONResponse(boolean success, JSONObject response) {

                    Log.d(TAG, "LOGIN RESULT: " + success + " | RESPONSE: " + response.toString());

                    //that.loginPbViewLogic(false);

                    if (success == true){

                        stalkerClient = APILoader.getStalkerClient();

                        Log.d(TAG, "CLIENT ID: " + stalkerClient.getUserId());

                        startHomeActivity();

                    } else {

                        try{

                            tvErrorMessage.setText(response.getString("error_description"));
                            Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();

                        } catch (JSONException e){

                            Log.d(TAG, "LOGIN PARSE ERROR: " + e.toString());

                        }

                    }
                }
            });

            //invokeLogin(params);

        } else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }

    private void startHomeActivity() {

        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        homeIntent.putExtra(StalkerClient.class.getCanonicalName(), stalkerClient);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }

}



