package com.mdelsordo.stepquest.util.rate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.mdelsordo.stepquest.R;

import java.util.Date;

import static com.mdelsordo.stepquest.util.rate.DialogManager.create;
import static com.mdelsordo.stepquest.util.rate.IntentHelper.*;
import static com.mdelsordo.stepquest.util.rate.PreferenceHelper.*;
import static com.mdelsordo.stepquest.util.rate.Utils.*;



public final class AppRate {

    private static AppRate singleton;

    private final Context context;

    private final DialogOptions options = new DialogOptions();

    private int installDate = 10;

    private int launchTimes = 10;

    private int remindInterval = 1;

    private boolean isDebug = false;

    private static final int PROMPT_CAP = 3;

    private AppRate(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AppRate with(Context context) {
        if (singleton == null) {
            synchronized (AppRate.class) {
                if (singleton == null) {
                    singleton = new AppRate(context);
                }
            }
        }
        return singleton;
    }

    public static boolean showRateDialogIfMeetsConditions(Activity activity) {
        boolean isMeetsConditions = singleton.isDebug || singleton.shouldShowRateDialog();
        if (isMeetsConditions) {
            singleton.showRateDialog(activity);
        }
        return isMeetsConditions;
    }

    private static boolean isOverDate(long targetDate, int threshold) {
        return new Date().getTime() - targetDate >= threshold * 24 * 60 * 60 * 1000;
    }

    public AppRate setLaunchTimes(int launchTimes) {
        this.launchTimes = launchTimes;
        return this;
    }

    public AppRate setInstallDays(int installDate) {
        this.installDate = installDate;
        return this;
    }

    public AppRate setRemindInterval(int remindInterval) {
        this.remindInterval = remindInterval;
        return this;
    }

    public AppRate setShowLaterButton(boolean isShowNeutralButton) {
        options.setShowNeutralButton(isShowNeutralButton);
        return this;
    }

    public AppRate setShowNeverButton(boolean isShowNeverButton) {
        options.setShowNegativeButton(isShowNeverButton);
        return this;
    }

    public AppRate setShowTitle(boolean isShowTitle) {
        options.setShowTitle(isShowTitle);
        return this;
    }

    public AppRate clearAgreeShowDialog() {
        PreferenceHelper.setAgreeShowDialog(context, true);
        return this;
    }

    public AppRate clearSettingsParam() {
        PreferenceHelper.setAgreeShowDialog(context, true);
        PreferenceHelper.clearSharedPreferences(context);
        return this;
    }

    public AppRate setAgreeShowDialog(boolean clear) {
        PreferenceHelper.setAgreeShowDialog(context, clear);
        return this;
    }

    public AppRate setView(View view) {
        options.setView(view);
        return this;
    }

    public AppRate setOnClickButtonListener(OnClickButtonListener listener) {
        options.setListener(listener);
        return this;
    }

    public AppRate setTitle(int resourceId) {
        options.setTitleResId(resourceId);
        return this;
    }

    public AppRate setTitle(String title) {
        options.setTitleText(title);
        return this;
    }

    public AppRate setMessage(int resourceId) {
        options.setMessageResId(resourceId);
        return this;
    }

    public AppRate setMessage(String message) {
        options.setMessageText(message);
        return this;
    }

    public AppRate setTextRateNow(int resourceId) {
        options.setTextPositiveResId(resourceId);
        return this;
    }

    public AppRate setTextRateNow(String positiveText) {
        options.setPositiveText(positiveText);
        return this;
    }

    public AppRate setTextLater(int resourceId) {
        options.setTextNeutralResId(resourceId);
        return this;
    }

    public AppRate setTextLater(String neutralText) {
        options.setNeutralText(neutralText);
        return this;
    }

    public AppRate setTextNever(int resourceId) {
        options.setTextNegativeResId(resourceId);
        return this;
    }

    public AppRate setTextNever(String negativeText) {
        options.setNegativeText(negativeText);
        return this;
    }

    public AppRate setCancelable(boolean cancelable) {
        options.setCancelable(cancelable);
        return this;
    }

    public AppRate setStoreType(StoreType appstore) {
        options.setStoreType(appstore);
        return this;
    }

    public void monitor() {
        if (isFirstLaunch(context)) {
            setInstallDate(context);
        }
        PreferenceHelper.setLaunchTimes(context, getLaunchTimes(context) + 1);
    }

    public void showRateDialog(Activity activity) {
        if (!activity.isFinishing()) {
            Dialog dialog = create(activity, options);
            dialog.show();
            incrementNumberOfPrompts(activity);
        }
    }

    public boolean shouldShowRateDialog() {
        if(getNumberOfPrompts(context) > PROMPT_CAP) return false;

        return getIsAgreeShowDialog(context) &&
                isOverLaunchTimes() &&
                isOverInstallDate() &&
                isOverRemindDate();
    }

    private boolean isOverLaunchTimes() {
        return getLaunchTimes(context) >= launchTimes;
    }

    private boolean isOverInstallDate() {
        return isOverDate(getInstallDate(context), installDate);
    }

    private boolean isOverRemindDate() {
        return isOverDate(getRemindInterval(context), remindInterval);
    }

    public boolean isDebug() {
        return isDebug;
    }

    public AppRate setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

}
