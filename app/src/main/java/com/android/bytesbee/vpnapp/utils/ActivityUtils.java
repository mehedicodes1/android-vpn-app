package com.android.bytesbee.vpnapp.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link https://bytesbee.com
 */
public class ActivityUtils {

    private static ActivityUtils sActivityUtils = null;

    public static ActivityUtils getInstance() {
        if (sActivityUtils == null) {
            sActivityUtils = new ActivityUtils();
        }
        return sActivityUtils;
    }

    /**
     * open  the activity
     *
     * @param activity     context of current activity
     * @param tClass       class name of the activity to be opened
     * @param shouldFinish true ==> finishes the current activity
     *                     false ==> dont finish the current activity
     */

    public void invokeActivity(Activity activity, Class<?> tClass, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }
}
