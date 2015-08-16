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

package org.creativecommons.thelist.authentication;

public class AccountGeneral {
//    Context mContext;
//    AccountManager mAccountManager;
//
//    public AccountGeneral(){
//    }
//
//    public AccountGeneral(Context context){
//        this.mContext = context;
//        this.mAccountManager = AccountManager.get(context);
//    }

    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "org.creativecommons.thelist";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "CCID";

    /**
     * Features
     */
    public static final String FEATURE_FULL_ACCOUNT_DEFAULT = "CCID Full Account";

    public static final String[] FULL_ACCOUNT_FEATURES = {
            FEATURE_FULL_ACCOUNT_DEFAULT
    };

    /**
     * User Data Keys
     */
    public static final String USER_ID = "userid";
    public static final String ANALYTICS_OPTOUT = "analyticsOptOut";
    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to a CCID";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to a CCID";

    //public static final ListAuthenticate sServerAuthenticate = new ListAuthenticate();

    //Authenticate Bundle Constants
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public static final String USER_PASS = "USER_PASS";



}//AccountGeneral
