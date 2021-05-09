package com.ramayan.mycom.Utils;



import com.ramayan.mycom.Model.VideosModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("playlist/{input}/videos?page=1&limit=100&fields=duration,id,item_type,thumbnail_url,title,description")
    Observable<VideosModel> getvideosList(@Path("input") String input);

}
