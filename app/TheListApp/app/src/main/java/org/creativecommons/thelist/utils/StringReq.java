package org.creativecommons.thelist.utils;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class StringReq extends StringRequest{

    private int mStatusCode;

    public StringReq(int method, String url, Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        mStatusCode = response.statusCode;
        return super.parseNetworkResponse(response);
    }
} //StringReq
