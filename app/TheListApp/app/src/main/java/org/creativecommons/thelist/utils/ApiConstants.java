/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.utils;

public final class ApiConstants {

    //User Object
    public static final String USER_ID = "userid";
    public static final String USER_NAME = "name";
    public static final String USER_ITEMS = "items";
    public static final String USER_CATEGORIES = "categories";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";

    //Category Object
    public static final String CATEGORY_NAME = "title";
    public static final String CATEGORY_ID = "id";
    public static final String CATEGORY_COLOUR = "color";

    //Category IDs
    public static final int PEOPLE = 1;
    public static final int PLACES = 2;
    public static final int CLOTHING = 3;
    public static final int NATURE = 4;
    public static final int FOOD = 5;
    public static final int OBJECTS = 6;

    //List Item
    public static final String ITEM_MAKER_ID = "makerid";
    public static final String ITEM_ID = "id";
    public static final String ITEM_NAME = "title";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_URI = "uri";
    public static final String ITEM_PHOTOS = "approved";
    public static final String ITEM_CATEGORY = "category";
    public static final String ITEM_COMPLETED = "complete";
    //public static final String EXAMPLE_URL = "exampleUrl";

    //List Item Request
    public static final String POST_ITEM_ID = "listid";
    public static final String POST_USER_ID = "userid";

    //Maker Item
    public static final String MAKER_ID = "id";
    public static final String MAKER_NAME = "name";
    public static final String MAKER_URI = "uri";

    //Photo
    public static final String PHOTO_ID = "id";
    public static final String PHOTO_ITEM_ID = "itemID";
    public static final String PHOTO_USER_ID = "userID";
    public static final String PHOTO_BYTE_ARRAY = "photo";
    public static final String POST_PHOTO_KEY = "filedata";
    public static final String USER_TOKEN = "skey";

    //User Photo List
    public static final String USER_PHOTO_URL = "url";

    //Creative Commons API
    public static final String URL = "https://thelist.creativecommons.org/api";

    //Real API

    //APP VERSION
    public static final String GET_CURRENT_APP_VERSION = URL + "/androidversion/";

    //ITEMS
    //Must add item ID
    public static final String GET_SINGLE_ITEM = URL + "/items/";
    //Must add item ID
    public static final String POST_SINGLE_ITEM = URL + "/items/";
    //GET ITEMS (currently returns 20 random ones)
    public static final String GET_RANDOM_ITEMS = URL + "/items/";

    //CATEGORIES
    //GET Categories
    public static final String GET_CATEGORIES = URL + "/category/";

    //MAKERS
    //GET Maker Object (+ID)
    public static final String GET_MAKER_NAME = URL + "/makers/";
    //GET User List (+ ID)

    //USERLIST
    public static final String GET_USER_LIST = URL + "/userlist/";
    //POST Item to User List (+ ID , + ItemID)
    public static final String ADD_ITEM = URL + "/userlist/";
    //DELETE Item from User List (+ ID, +ItemID)
    //TODO: update with real info
    public static final String REMOVE_ITEM = URL + "/userlist/delete/";

    //USERCATEGORIES
    //(+ UserID)
    public static final String GET_USER_CATEGORIES = URL + "/usercategories/list/";
    //(+ UserID, + "/" + CategoryID)
    public static final String ADD_CATEGORY = URL +"/usercategories/add/";
    //(+ UserID, + "/" + CategoryID)
    public static final String REMOVE_CATEGORY = URL +"/usercategories/delete/";

    //PHOTOS
    //POST photo (+ ID, + ItemID)
    public static final String ADD_PHOTO = URL + "/photos/";
    //GET user photos (+ID)
    public static final String GET_USER_PHOTOS = URL + "/photos/";

    //LOGIN
    public static final String LOGIN_USER = URL + "/users/login";
    //TODO: API not ready yet
    //USER REGISTER: send email address and password
    public static final String REGISTER_USER = URL + "/users/register";
    //USER PROFILE (append with EMAIL?)
    //public static final String USER_PROFILE = URL + "/api/users/";

}
