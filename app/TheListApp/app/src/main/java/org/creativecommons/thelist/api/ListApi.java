package org.creativecommons.thelist.api;

import retrofit.RestAdapter;

public class ListApi {

    public static final String LIST_BASE_URL = "http://54.166.151.139/api";

    private ListService mListService;

    public ListApi(){
        mListService = init();

    }

    private ListService init(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(LIST_BASE_URL)
                .build();

        return restAdapter.create(ListService.class);
    }

    public ListService getService(){
        return mListService;
    }


} //Request
