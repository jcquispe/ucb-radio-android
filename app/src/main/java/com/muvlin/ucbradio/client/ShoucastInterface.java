package com.muvlin.ucbradio.client;
import com.muvlin.ucbradio.client.pojo.MetadataResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ShoucastInterface {
    @GET("/cp/get_info.php?p=8186")
    Call<MetadataResponse> getMetadata();
}
