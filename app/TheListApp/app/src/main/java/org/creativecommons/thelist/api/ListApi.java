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
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        return restAdapter.create(ListService.class);
    }

    public ListService getService(){
        return mListService;
    }


} //Request
