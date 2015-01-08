/* The List powered by Creative Commons

   Copyright (C) 2014 Creative Commons

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

    //Creative Commons API
    public static final String URL = "https://thelist.creativecommons.org/api";

    //Real API
    //Must add item ID
    public static final String GET_SINGLE_ITEM = URL + "/items/";
    //Must add item ID
    public static final String POST_SINGLE_ITEM = URL + "/items/";
    //GET ITEMS (currently returns 20 random ones)
    public static final String GET_RANDOM_ITEMS = URL + "/items/";
    //GET Categories
    public static final String GET_CATEGORIES = URL + "/category/";
    //GET Maker Object (+ID)
    public static final String GET_MAKER_NAME = URL + "/makers/";
    //GET User List (+ ID)
    public static final String GET_USER_LIST = URL + "/userlist/";
    //POST Item to User List (+ ID , + ItemID)
    public static final String ADD_ITEM = URL + "/userlist/";
    //POST photo (+ ID, + ItemID)
    public static final String ADD_PHOTO = URL + "/photos/";
    //POST login (+ email and password)
    public static final String LOGIN_USER = URL + "/users/login";

    //TODO: API not ready yet
    //USER REGISTER: send email address and password
    public static final String REGISTER_USER = URL + "/users/register";
    //USER PROFILE (append with EMAIL?)
    //public static final String USER_PROFILE = URL + "/api/users/";

}
