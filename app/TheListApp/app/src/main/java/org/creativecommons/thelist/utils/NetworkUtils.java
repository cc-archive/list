package org.creativecommons.thelist.utils;

import com.android.volley.VolleyError;

import org.creativecommons.thelist.adapters.UserListItem;
import org.json.JSONArray;

import java.util.List;

public class NetworkUtils {

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

}
