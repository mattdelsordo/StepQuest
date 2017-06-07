package com.mdelsordo.stepquest.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mdelsordo.stepquest.model.ActiveCharacter;


/**
 * Intent service that acts as a timer to keep track of when
 * Useful stackoverflow to help me with this: http://stackoverflow.com/questions/22496863/how-to-run-countdowntimer-in-a-service-in-android
 */
public class BoostTimerService extends Service {

    private final static String TAG = "BoostTimerService";
    public static final String PREF_BOOST_ACTIVE = "pref_boost_active";
    public static final String PREF_BOOST_TIME_REMAINING = "pref_boost_time_remaining";
    public static final String PREF_BOOST_MAGNITUDE = "pref_boost_magnitude";

    public static final String TIMER_BROADCAST = "timer broadcast";
    public static final String TIMER_DONE = "timer done";
    public static final String ARG_SECONDS_LEFT = "seconds left";
    Intent broadcast = new Intent(TIMER_BROADCAST);

    private static final int SECOND = 1000;
    private CountDownTimer mCDT = null;
    private long mDuration;
    private boolean isRunning;
    private long millisLeft;
    public static boolean sServiceExists;

    private static final String ARG_DURATION = "arg_duration";
    public static Intent newInstance(Context c, long duration){
        Intent intent = new Intent(c, BoostTimerService.class);
        intent.putExtra(ARG_DURATION, duration);
        return intent;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            mDuration = intent.getLongExtra(ARG_DURATION, 0);


            //Log.i(TAG, "Starting boost timer for " + mDuration + " seconds...");

            mCDT = new CountDownTimer(mDuration, SECOND) {

                @Override
                public void onTick(long millisUntilFinished) {
                    isRunning = true;
                    //Log.i(TAG, millisUntilFinished + " ms until boost finished.");
                    millisLeft = millisUntilFinished;
                    broadcast.putExtra(ARG_SECONDS_LEFT, millisUntilFinished);
                    sendBroadcast(broadcast);
                }

                @Override
                public void onFinish() {
                    isRunning = false;
                    //Log.i(TAG, "Timer finished.");
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BoostTimerService.this);
//                    prefs.edit().putBoolean(PREF_BOOST_ACTIVE, false).apply();
                    ActiveCharacter.getInstance(BoostTimerService.this).removeBoost();

                    //stop everything
                    sendBroadcast(new Intent(TIMER_BROADCAST));
                    BoostTimerService.this.stopSelf(); //stop service
                }
            };

            //start boost
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putLong(PREF_BOOST_TIME_REMAINING, mDuration).apply();
            prefs.edit().putLong(PREF_BOOST_MAGNITUDE, Double.doubleToLongBits(ActiveCharacter.getInstance(this).getBoostMultiplier())).apply();
            mCDT.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sServiceExists = true;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        sServiceExists = false;
        //Log.i(TAG, "BoostTimer ontaskremoved called.");
        saveBoost();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        sServiceExists = false;
        //Log.i(TAG, "BoostTimer ondestroy called");
        //if the timer is still going save the state
        saveBoost();

        mCDT.cancel();
        //Log.i(TAG, "Timer cancelled.");
        //Log.i(TAG, "Timer service destroyed.");
        super.onDestroy();
    }

    public void saveBoost(){
        //Log.i(TAG, "saveBoost called");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(isRunning){
            prefs.edit().putLong(PREF_BOOST_TIME_REMAINING, millisLeft).apply();
            prefs.edit().putLong(PREF_BOOST_MAGNITUDE, Double.doubleToLongBits(ActiveCharacter.getInstance(this).getBoostMultiplier())).apply();
        }else{
            prefs.edit().putLong(PREF_BOOST_TIME_REMAINING, -1).apply();
            prefs.edit().putLong(PREF_BOOST_MAGNITUDE, 1).apply();
        }
    }

    private final IBinder mBinder = new BoostBinder();
    public class BoostBinder extends Binder {
        public BoostTimerService getService(){return BoostTimerService.this;}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
