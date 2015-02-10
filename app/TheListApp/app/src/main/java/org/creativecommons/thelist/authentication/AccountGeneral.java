package org.creativecommons.thelist.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class AccountGeneral {
    Context mContext;
    AccountManager mAccountManager;
    String authtoken;

    public AccountGeneral(){
    }

    public AccountGeneral(Context context){
        this.mContext = context;
        this.mAccountManager = AccountManager.get(context);
    }

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

    //public static final ListAuthenticate sServerAuthenticate = new ListAuthenticate();

    //Authenticate Constants
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public static final String PARAM_USER_PASS = "USER_PASS";

    //Get Accounts
//    public Account getAccounts(final String authTokenType){
//        final Account availableAccounts[] = mAccountManager.getAccounts();
//        Account userAccount;
//
//        if(availableAccounts.length == 1){
//            return availableAccounts[0];
//        } else {
//            String name[] = new String[availableAccounts.length];
//            for (int i = 0; i < availableAccounts.length; i++) {
//                name[i] = availableAccounts[i].name;
//
//            //Account Picker
//            AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    userAccount = availableAccounts[which];
//                }
//            }).create();
//            alertDialog.show();
//        }
//
//        //Show account picker
//    }
//
//    public String getAuthToken(){
//        String authToken;
//
//        final Account availableAccounts[] = mAccountManager.getAccounts();
//
//        mAccountManager.getAuthToken()
//
//
//    }

    public void getToken(){
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        //final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(availableAccounts[0], authTokenType, null, this, null, null);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Bundle bnd = future.getResult();
//                    authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                    Log.d("udinic", "GetToken Bundle is " + bnd);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        return authtoken;
    } //getToken

}//AccountGeneral