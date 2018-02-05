package com.videorec.peaksource.videorecorder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

/**
 * Created by safa on 04/02/18.
 */

public interface APiInterface {

    @Multipart
    @POST("")
    Call<String> uploadVideo(String file);




}
