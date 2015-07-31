/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.

   If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.api;

import org.creativecommons.thelist.models.Photo;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface ListService {

    //App Version

    //User Profile

    //Photo Feed
    @GET("/feed")
    void getPhotoFeed(Callback<List<Photo>> callback);


    // Contribute List

    @POST("/userlist/{userid}/{itemid}")
    void addItem(@Path("userid") String userid,
                 @Path("itemid") String itemid, Callback<Response> callback);


    // Request List


    // Item Suggestion


    // Category Preference //TODO: eventually this will be tags

    //TODO: create Category Object
//    @GET("/category")
//    void getCategories(Callback<List<Category>> callback);
//
//    @GET("usercategories/list/{userid}")
//    void getUserCategories(@Path("userid") String userid, Callback<List<Category>> callback);

    @POST("/usercategories/add/{userid}/{catId}")
    void addCategory(@Path("userid") String userid,
                     @Path("catId") String catId, Callback<Response> callback);

    @POST("/usercategories/delete/{userid}/{catId}")
    void deleteCategory(@Path("userid") String userid,
                     @Path("catId") String catId, Callback<Response> callback);


//    @GET("/items/{itemid}")
//    void getItem(@Path("itemid") String itemid, Callback<Item> callback);
//
//    @GET("/items")
//    void getRandomItems(Callback<List<Item>>callback callback);


}
