package com.briz.developer.briztv;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eng210 on 22.04.2015.
 */

public class StalkerLoader{

    private static final String TAG = StalkerLoader.class.getSimpleName();

    private StalkerClient stalkerClient;
    private Context context;


    public StalkerLoader(Context context, StalkerClient stalkerClient){

        this.stalkerClient = stalkerClient;
        this.context = context;


    }

    public StalkerClient getStalkerClient(){
        return stalkerClient;
    }

    public interface OnJSONResponseCallback {
        public void onJSONResponse(boolean success, JSONObject response);
    }

    /**
     * Аторизация пользователя на сервере.
     * Происходит только один раз при запуске приложения
     */
    public void login(RequestParams params,final OnJSONResponseCallback callback){

        AsyncHttpClient client = new AsyncHttpClient();

        client.post("http://ott.briz.ua/stalker_portal/auth/token.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers,  byte[] bytes) {
                try {
                    String response = new String(bytes);

                    JSONObject obj = new JSONObject(response);

                    if (obj.has("access_token")) {
                        Log.d(TAG, "login success full: " + obj.toString());

                        stalkerClient.setData(obj);
                        callback.onJSONResponse(true, obj);
                    }

                    if (obj.has("error")) {
                        Log.e(TAG, "login failed: " + obj.toString());
                        callback.onJSONResponse(false, obj);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "error in response: " + e.toString());
                    Toast.makeText(context, "error in response...", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  byte[] response, Throwable throwable) {

                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found ", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong..((", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Unexpected error..((", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    /**
     * Обновление access_token-а авторизации пользователя.
     */
    public void refreshToken(final OnJSONResponseCallback callback){

        RequestParams params = new RequestParams();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", stalkerClient.getRefreshToken());

        AsyncHttpClient client = new AsyncHttpClient();

        client.post("http://ott.briz.ua/stalker_portal/auth/token.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers,  byte[] bytes) {
                try {
                    String response = new String(bytes);

                    JSONObject obj = new JSONObject(response);

                    if (obj.has("access_token")) {
                        Log.d(TAG, "refresh success full: " + obj.toString());

                        stalkerClient.setData(obj);
                        callback.onJSONResponse(true, obj);
                    }

                    if (obj.has("error")) {
                        Log.e(TAG, "refresh failed: " + obj.toString());
                        callback.onJSONResponse(false, obj);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "error in response: " + e.toString());
                    Toast.makeText(context, "error in response...", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  byte[] response, Throwable throwable) {

                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found ", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong..((", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Unexpected error..((", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    /**
     * Запрос на серевер для получения json данных
     * url - полный url запроса на api сервера
     * callback - запускаемая функция после завершения асинхронного запроса
     */
    public void invokeRequest(String url, final OnJSONResponseCallback callback){

        Log.d(TAG,"LOAD URL: " + url);

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader("Accept","application/json");
        client.addHeader("Authorization","Bearer "+stalkerClient.getAccessToken());
        client.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");

        client.get( url , new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {

                    //Intent loginIntent = new Intent(context, LoginActivity.class);
                    //loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   // loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //context.startActivity(loginIntent);

                    String response = new String(bytes);

                    Log.d(TAG,"REQUEST COMPLETE: " + response.toString());

                    JSONObject obj = new JSONObject(response);
                    if (obj.has("status")) {
                        callback.onJSONResponse(true, obj);
                    }

                    if (obj.has("error")) {
                        callback.onJSONResponse(false, obj);
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "Error in code..((", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable throwable) {
                //progressDialog.hide();
                Log.e(TAG, "-------request statusCode: " + statusCode);
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found ", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong..((", Toast.LENGTH_LONG).show();
                } else if (statusCode == 401){
                    Toast.makeText(context, "No authorized request", Toast.LENGTH_LONG).show();
                   // Intent loginIntent = new Intent(context, LoginActivity.class);
                   // context.startActivity(loginIntent);

                }
                else {
                    Toast.makeText(context, "Unexpected error..((", Toast.LENGTH_LONG).show();
                }
            }


        });

    }

    /**
     * Запрос на серевер для получения json данных c проверкой на истекший access_token пользователя
     * url - полный url запроса на api сервера
     * callback - запускаемая функция после завершения асинхронного запроса
     */
    public void loader(final String url, final OnJSONResponseCallback callback){

        if (stalkerClient.isExpireToken()){
            Log.e(TAG, "EXPIRE TOKEN: ");
            refreshToken(new OnJSONResponseCallback() {
                @Override
                public void onJSONResponse(boolean success, JSONObject response) {
                    if (success == true){
                        Toast.makeText(context, "Token updated", Toast.LENGTH_SHORT).show();
                        stalkerClient.setData(response);
                        invokeRequest(url, callback);
                    } else {
                        Toast.makeText(context, "Token not updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.i(TAG, "TOKEN CORRECT: ");
            invokeRequest(url, callback);
        }

    }

}
