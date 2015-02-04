package org.creativecommons.thelist.authentication;

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

    public static final ServerAuthenticate sServerAuthenticate = new ListAuthenticate();
}
