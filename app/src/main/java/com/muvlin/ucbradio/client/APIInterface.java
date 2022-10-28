package com.muvlin.ucbradio.client;

import com.muvlin.ucbradio.client.pojo.InfoResponse;
import com.muvlin.ucbradio.client.pojo.RadioResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface APIInterface {
    @GET("/ucb-radio.json")
    Observable<RadioResponse> getData();

    @GET("/info.json")
    Observable<InfoResponse> getInfo();
}
