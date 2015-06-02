/*
Attributed to Felix Ableitner
Source: https://gist.github.com/Nutomic/528ac465df545f34193b
 */

package org.creativecommons.thelist.misc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CustomChooserIntent {
    public static final String TAG = CustomChooserIntent.class.getSimpleName();

    /**
     * Creates a chooser that only shows installed apps that are allowed by the whitelist.
     *
     * @param pm PackageManager instance.
     * @param target The intent to share.
     * @param title The title of the chooser dialog.
     * @param blacklist A list of package names that are not allowed to show.
     * @return Updated intent, to be passed to {@link android.content.Context#startActivity}.
     */

    public static Intent create(PackageManager pm, Intent target, String title,
                                List<String> blacklist) {
        Intent dummy = new Intent(target.getAction());
        dummy.setType(target.getType());
        List<ResolveInfo> resInfo = pm.queryIntentActivities(dummy, 0);
        Log.v(TAG, resInfo.toString());

        List<HashMap<String, String>> metaInfo = new ArrayList<>();
        for (ResolveInfo ri : resInfo) {
            if (ri.activityInfo == null || blacklist.contains(ri.activityInfo.packageName))
                continue;

            HashMap<String, String> info = new HashMap<>();
            String simpleName;

            if(ri.activityInfo.name.equals("com.twitter.android.DMActivity")){
                simpleName = "Direct Message";

                info.put("simpleName", simpleName);
                Log.v(TAG, "simpleName " + simpleName);

            } else if(ri.activityInfo.name.equals("com.twitter.android.composer.ComposerActivity")){
                simpleName = "Tweet";
                info.put("simpleName", simpleName);
                Log.v(TAG, "simpleName " + simpleName);

            } else {
                info.put("simpleName", String.valueOf(ri.activityInfo.loadLabel(pm)));
            }

            info.put("packageName", ri.activityInfo.packageName);
            Log.v(TAG, "packageName: " + ri.activityInfo.packageName);
            info.put("className", ri.activityInfo.name);
            Log.v(TAG, "className: " + ri.activityInfo.name);
            metaInfo.add(info);
        }

        if (metaInfo.isEmpty()) {
            // Force empty chooser by setting a nonexistent target class.
            Intent emptyIntent = (Intent) target.clone();
            emptyIntent.setPackage("org.creativecommons.thelist");
            emptyIntent.setClassName("org.creativecommons.thelist", "MainActivity");
            return Intent.createChooser(emptyIntent, title);
        }

        // Sort items by display name.
        Collections.sort(metaInfo, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
                return map.get("simpleName").compareTo(map2.get("simpleName"));
            }
        });

        // Create the custom intent list
        List<Intent> targetedIntents = new ArrayList<>();
        for (HashMap<String, String> mi : metaInfo) {
            Intent targetedShareIntent = (Intent) target.clone();
            targetedShareIntent.setPackage(mi.get("packageName"));
            targetedShareIntent.setClassName(mi.get("packageName"), mi.get("className"));
            targetedIntents.add(targetedShareIntent);
        }

        Intent chooserIntent = Intent.createChooser(targetedIntents.get(0), title);
        targetedIntents.remove(0);
        Parcelable[] targetedIntentsParcelable =
                targetedIntents.toArray(new Parcelable[targetedIntents.size()]);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntentsParcelable);
        return chooserIntent;

    }

} //CustomChooserIntent
