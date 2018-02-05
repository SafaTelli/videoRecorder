package com.videorec.peaksource.videorecorder;

import android.os.Build;
import android.support.annotation.RequiresApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by safa on 04/02/18.
 */

public class ApiConfig {


    public static final String BASE_URL = "";

    public static Retrofit retrofit = null ;




    public static  Retrofit getClient ()
    {

        if (retrofit == null)

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                    addConverterFactory(GsonConverterFactory.create())
                    .build();


        return  retrofit ;
    }




}
