package org.creativecommons.thelist.authentication;

import org.creativecommons.thelist.utils.ApiConstants;

import java.io.Serializable;

public class ListUserAuthenticate implements ServerAuthenticate {
    @Override
    public String userSignIn(String user, String pass, String authType) throws Exception {
        String url = ApiConstants.REGISTER_USER;





    }


    @Override
    public String userSignUp(String name, String email, String pass, String authType) throws Exception {
        String url = ApiConstants.LOGIN_USER;

    }



} //ListUserAuthenticate
