package com.android.bytesbee.vpnapp.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * @author Prashant
 * @company BytesBee
 * @link http://bytesbee.com/
 */
public class Screens {

    public static void showClearTopScreen(final Context context, final Class<?> cls) {
        final Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void showCustomScreen(final Context context, final Class<?> cls) {
        final Intent intent = new Intent(context, cls);
        context.startActivity(intent);
        try {
            ((Activity) context).overridePendingTransition(0, 0);
        } catch (Exception ignored) {
        }
    }

}
