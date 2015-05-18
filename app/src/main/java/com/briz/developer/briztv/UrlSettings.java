package com.briz.developer.briztv;

/**
 * Created by user on 18.05.15.
 */
public class UrlSettings {

    private static final String BASE_URL_V2 = "http://v2.api.ott.briz.ua";
    private static final String BASE_URL = "http://ott.briz.ua";
    private static final String RES_URL = BASE_URL_V2 + "/stalker_portal/api/users/";

    public static String getBaseUrlV2() {
        return BASE_URL_V2;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getResUrl() {
        return RES_URL;
    }
}
