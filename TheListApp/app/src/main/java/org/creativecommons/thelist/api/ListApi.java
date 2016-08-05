package org.creativecommons.thelist.api;

import retrofit.RestAdapter;

public class ListApi {

    /*Switching Base Url
    NB: Uncomment desired URL between dev, staging, and live servers
     */
    public static final String URL = "http://54.166.151.139/api";
    //public static final String URL = "https://staging-thelist.creativecommons.org/api";
    //public static final String URL = "https://thelist.creativecommons.org/api";

    private ListService mListService;

    public ListApi(){
        mListService = init();

    }

    private ListService init(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(URL)
                .build();

        return restAdapter.create(ListService.class);
    }

    public ListService getService(){
        return mListService;
    }


} //Request
