package com.ad.sukasukanginep.apihelper;

/**
 * Created by USER on 11/25/2017.
 */

public class UtilsApi {
    //"http://amaliadmyt.000webhostapp.com/
    public static final String BASE_URL_API = "http://192.168.1.20/GIS/SukaSukaNginep/";

    // Mendeklarasikan Interface BaseApiService
    public static BaseApiService getAPIService(){
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }
}
