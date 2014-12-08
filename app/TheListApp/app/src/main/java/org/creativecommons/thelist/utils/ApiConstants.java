package org.creativecommons.thelist.utils;

/**
 * Created by damaris on 2014-11-05.
 */
public final class ApiConstants {

    //TODO: Limit results
    //LIMIT for category request
    //LIMIT for list item requests

    //Limit how many items user will view before possibility of repeats (RandomActivity)
    public static final int MAX_ITEMS_VIEWED = 5;


    //Response Object
    public static final String RESPONSE_CONTENT = "content";

    //User Object
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_ITEMS = "items";
    public static final String USER_CATEGORIES = "categories";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";

    //Category Object
    public static final String CATEGORY_NAME = "name";
    public static final String CATEGORY_ID = "id";

    //List Item
    public static final String ITEM_NAME = "name";
    public static final String ITEM_ID = "id";
    public static final String ITEM_CATEGORY = "category";
    public static final String MAKER_ID = "userId";
    public static final String MAKER_NAME = "user";
    public static final String EXAMPLE_URL = "exampleUrl";

    //Photo
    public static final String PHOTO_ID = "id";
    public static final String PHOTO_ITEM_ID = "itemID";
    public static final String PHOTO_USER_ID = "userID";
    public static final String PHOTO_BYTE_ARRAY = "photoFile";

    //URL
    //TODO: Add all Endpoints

    //Virtual Box
    public static final String URL = "http://192.168.56.1:3000/api";
    //Android Emulator
    //public static final String URL = "http://http://10.0.2.2:3000/api";
    //Creative Commons API
    //public static final String URL = "https://thelist.creativecommons.org/api";

    //Must POST
    public static final String CREATE_NEW_USER = "/user/";
    //Must have user ID (+ userID) + GET
    public static final String GET_ALL_USER_ITEMS = "/user/";
    //Must have user ID + PUT
    public static final String UPDATE_USER = "/user/";
    //User Login
    public static final String LOGIN_USER = "/user/";
    //Must send array of items (only works with PUT, not GET: TODO: look into this with real API)
    public static final String GET_MULTIPLE_ITEMS = "/items/";
    //GET Method
    public static final String GET_ALL_ITEMS = "/item/";
    //GET Single Item Method
    public static final String GET_SINGLE_ITEM = URL + "/item/";
    //POST, GET Photo
    public static final String POST_PHOTO = URL + "/photo/";
    //GET Categories
    public static final String GET_CATEGORIES = URL + "/category/";


}
