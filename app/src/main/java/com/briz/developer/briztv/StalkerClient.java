package com.briz.developer.briztv;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Объект пользователя авторизировавшегося на Stalker
 *
 * Created by Vladimir(kolobokhtc@gmail.com) on 21.04.2015.
 */
public class StalkerClient implements Parcelable{

    private static final String TAG = StalkerClient.class.getSimpleName();

    private String access_token;
    private String token_type;
    private String refresh_token;
    private Integer user_id;
    private Long expires_in;

    public StalkerClient(){

    }

    public String getAccessToken(){
        return this.access_token;
    }

    public String getTokenType(){
        return this.token_type;
    }

    public String getRefreshToken(){
        return this.refresh_token;
    }

    public Integer getUserId(){
        return this.user_id;
    }

    public Long getExpiresIn(){
        return this.expires_in;
    }

    public void setAccessToken(String value){
        this.access_token = value;
    }

    public void setTokenType(String value){
        this.token_type = value;
    }

    public void setRefreshToken(String value){
        this.refresh_token = value;
    }

    public void setUserId(Integer value){
        this.user_id = value;
    }

    public void setExpiresIn(Long value){
        long time = System.currentTimeMillis();
        this.expires_in = time + (value*1000);
    }

    public void setData(JSONObject jsonObject){

        try{

            if (jsonObject.has("access_token")){
                setAccessToken(jsonObject.getString("access_token"));
                Log.e(TAG, "access_token: " + getAccessToken());
            }

            if (jsonObject.has("token_type")){
                setTokenType(jsonObject.getString("token_type"));
                Log.e(TAG, "token_type: " + getTokenType());
            }

            if (jsonObject.has("refresh_token")){
                setRefreshToken(jsonObject.getString("refresh_token"));
                Log.e(TAG, "refresh_token: " + getRefreshToken());
            }

            if (jsonObject.has("user_id")){
                setUserId(jsonObject.getInt("user_id"));
                Log.e(TAG, "user_id: " + getUserId());
            }

            if (jsonObject.has("expires_in")){
                setExpiresIn(jsonObject.getLong("expires_in"));
                Log.e(TAG, "expires_in(ms): " + getExpiresIn());
            }

        } catch (JSONException e ){
            Log.e(TAG, "setData exception: " + e.toString());
        }


    }

    public boolean isLogin(){

        if ( getAccessToken() != null ){

            if (!isExpireToken())
                      return true;

        }

        return false;

    }

    public boolean isExpireToken(){

        long time = System.currentTimeMillis();
        Log.e(TAG, "CURRENT TIME: " + time);
        Log.e(TAG, "EXPIRE TIME: " + getExpiresIn());

        return ( time > getExpiresIn());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.e(TAG, "writeToParcel: ");
        dest.writeString(this.access_token);
        dest.writeString(this.refresh_token);
        dest.writeString(this.token_type);
        dest.writeInt(this.user_id);
        dest.writeLong(this.expires_in);
    }

    public static final Creator<StalkerClient> CREATOR = new Creator<StalkerClient>(){

        @Override
        public StalkerClient createFromParcel(Parcel source) {
            Log.e(TAG, "readFromParcel: ");
            return new StalkerClient(source);
        }

        @Override
        public StalkerClient[] newArray(int size) {
            return new StalkerClient[size];
        }
    };

    private StalkerClient(Parcel parcel){

        Log.e(TAG, "deserializing From Parcel: ");

        this.access_token = parcel.readString();
        this.refresh_token = parcel.readString();
        this.token_type = parcel.readString();
        this.user_id = parcel.readInt();
        this.expires_in = parcel.readLong();

    }
}
