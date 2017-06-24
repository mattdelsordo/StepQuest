package com.mdelsordo.stepquest.util.rate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;

import com.mdelsordo.stepquest.R;

final class Utils {

    private Utils() {
    }

    static boolean underHoneyComb() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    }

    static boolean isLollipop() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    static int getDialogTheme() {
        return isLollipop() ? R.style.DialogTheme : 0;
    }

    @SuppressLint("NewApi")
    static AlertDialog.Builder getDialogBuilder(Context context) {
        return new AlertDialog.Builder(context, R.style.DialogTheme);
//        if (underHoneyComb()) {
//            return new AlertDialog.Builder(context);
//        } else {
//            return new AlertDialog.Builder(context, getDialogTheme());
//        }
    }

}
