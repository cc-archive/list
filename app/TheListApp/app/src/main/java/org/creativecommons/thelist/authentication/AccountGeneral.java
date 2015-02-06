package org.creativecommons.thelist.authentication;

import org.creativecommons.thelist.misc.ListAuthenticate;

public class AccountGeneral {
    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "org.creativecommons.thelist";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "CCID";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to a CCID";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to a CCID";

    public static final ListAuthenticate sServerAuthenticate = new ListAuthenticate();

    //Authenticate Constants
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public static final String PARAM_USER_PASS = "USER_PASS";
}
