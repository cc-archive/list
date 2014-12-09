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
    public static final String CATEGORY_NAME = "title";
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
    public static final String URL = "http://192.168.56.1:3000";


//    //REAL API
//    //Must POST
//    public static final String CREATE_NEW_USER = "https://thelist.creativecommons.org/api/user/";
//    //Must have user ID (+ userID) + GET
//    public static final String GET_ALL_USER_ITEMS = "https://thelist.creativecommons.org/api/user/";
//    //Must have user ID + PUT
//    public static final String UPDATE_USER = "https://thelist.creativecommons.org/api/user/";
//    //User Login
//    public static final String LOGIN_USER = "https://thelist.creativecommons.org/api/user/";
//    //Must send array of items (only works with PUT, not GET: TODO: look into this with real API)
//    public static final String GET_MULTIPLE_ITEMS = "https://thelist.creativecommons.org/api/items/";
//    //GET Method
//    public static final String GET_ALL_ITEMS = "https://thelist.creativecommons.org/api/item/";

//    FAKE API, MOBILE NETWORK
    //Must use POST

    public static final String CREATE_NEW_USER = URL + "/api/user/";
    //Must have user ID
    public static final String GET_ALL_USER_ITEMS = URL + "/api/user/";
    //Must Add ID + use PUT
    public static final String UPDATE_USER = URL + "/api/user/";
    //User Login
    public static final String LOGIN_USER = URL + "/api/user/";
    //Must send array of items
    public static final String GET_MULTIPLE_ITEMS = URL + "/api/items/";
    //GET Method
    public static final String GET_ALL_ITEMS = URL + "/api/item/";
    //GET Single Item Method
    public static final String GET_SINGLE_ITEM = URL + "/api/item/";
    //POST, GET Photo
    public static final String POST_PHOTO = URL + "/api/photo/";
    //GET Categories
    public static final String GET_CATEGORIES = URL + "/api/category/";

//    FAKE API, ANDROID
//    public static final String CREATE_NEW_USER = "http://10.0.2.2:3000/api/user/";
//    //Must have user ID
//    public static final String GET_ALL_USER_ITEMS = "http://10.0.2.2:3000/api/user/";
//    //Must Add ID + use PUT
//    public static final String UPDATE_USER = "http://10.0.2.2:3000/api/user/";
//    //User Login
//    public static final String LOGIN_USER = "http://10.0.2.2:3000/api/user/";
//    //Must send array of items
//    public static final String GET_MULTIPLE_ITEMS = "http://10.0.2.2:3000/api/items/";
//    //GET Method
//    public static final String GET_ALL_ITEMS = "http://10.0.2.2:3000/api/item/";
//    //GET Single Item Method
//    public static final String GET_SINGLE_ITEM = "http://10.0.2.2:3000/api/item/";
//    //POST, GET Photo
//    public static final String POST_PHOTO = "http://10.0.2.2:3000/api/photo/";
//    //GET Categories
//    public static final String GET_CATEGORIES = "http://10.0.2.2:3000/api/category/";

}
