package org.creativecommons.thelist.api;

import org.creativecommons.thelist.models.Photo;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface ListService {


    @GET("/feed")
    void getPhotoFeed(Callback<List<Photo>> callback);


//    @GET("/items/{itemid}")
//    void getItem(@Path("itemid") String itemid, Callback<Item> callback);
//
//    @GET("/items")
//    void getRandomItems(Callback<List<Item>>callback callback);


}
