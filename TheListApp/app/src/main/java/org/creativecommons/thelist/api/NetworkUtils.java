package org.creativecommons.thelist.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.VolleyError;

import org.creativecommons.thelist.adapters.UserListItem;
import org.json.JSONArray;

import java.util.List;

public class NetworkUtils {

    // --------------------------------------------------------
    // NETWORK CHECKS
    // --------------------------------------------------------

    //Check if thar be internets (public helper)
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    // --------------------------------------------------------
    // CODES
    // --------------------------------------------------------

    //Cancel Codes
    public enum CancelResponse {
        NETWORK_ERROR,
        FILESIZE_ERROR
    }

    // --------------------------------------------------------
    // CALLBACKS
    // --------------------------------------------------------

    //Callback for requests
    public interface RequestCallback {
        void onSuccess();
        void onFail();
        void onCancelled(CancelResponse response);
    }

    public interface ResponseCallback {
        void onSuccess(JSONArray response);
        void onFail(VolleyError error);
    }

    public interface UploadResponse {
        void onSuccess();
        void onFail();
        void onCancelled(CancelResponse response);
    }

    public interface UserListCallback {
        void onSuccess(JSONArray response);
        void onFail(VolleyError error);
        void onUserOffline(List<UserListItem> response);
    }

} //NetworkUtils
