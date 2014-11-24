package org.creativecommons.thelist.utils;

/**
 * Created by damaris on 2014-11-05.
 */
public final class ApiConstants {

    //TODO: Limit returned results
    //LIMIT for category request
    //LIMIT for list item requests

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

    //Must use POST
    public static final String CREATE_NEW_USER = "http://10.0.3.2:3000/api/user";
    //Must Add ID + use PUT
    public static final String UPDATE_USER = "http://10.0.3.2:3000/api/user";
    //Must send array of items
    public static final String GET_MULTIPLE_ITEMS = "http://10.0.3.2:3000/api/items";
    //GET Method
    public static final String GET_ALL_ITEMS = "http://10.0.3.2:3000/api/item/";
    //POST, GET Photo
    public static final String POST_PHOTO = "http://10.0.3.2:3000/api/photo";


}
