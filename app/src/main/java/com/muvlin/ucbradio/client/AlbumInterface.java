package com.muvlin.ucbradio.client;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AlbumInterface {
    @GET("?method=track.getInfo&format=json")
    Observable<Response<ResponseBody>> getAlbumArt( @Query("api_key") String api_key,
                                                    @Query("artist") String artist,
                                                    @Query("track") String track);
}
