package project3.csc214.stepquest.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Intent service that acts as a timer to keep track of when
 * Useful stackoverflow to help me with this: http://stackoverflow.com/questions/22496863/how-to-run-countdowntimer-in-a-service-in-android
 */
public class BoostTimerService extends Service {

    private final static String TAG = "BoostTimerService";
    public static final String PREF_BOOST_ACTIVE = "pref_boost_active";

    public static final String TIMER_BROADCAST = "timer broadcast";
    public static final String TIMER_DONE = "timer done";
    public static final String ARG_SECONDS_ELAPSED = "seconds elapsed";
    Intent broadcast = new Intent(TIMER_BROADCAST);

    private static final int SECOND = 1000;
    CountDownTimer mCDT = null;
    long mDuration;

    private static final String ARG_DURATION = "arg_duration";
    public static Intent newInstance(Context c, long duration){
        Intent intent = new Intent(c, BoostTimerService.class);
        intent.putExtra(ARG_DURATION, duration);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDuration = intent.getLongExtra(ARG_DURATION, 0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Starting boost timer for " + mDuration + " seconds...");

        mCDT = new CountDownTimer(mDuration, SECOND) {

            @Override
            public void onTick(long millisUntilFinished) {
                broadcast.putExtra(ARG_SECONDS_ELAPSED, millisUntilFinished);
                sendBroadcast(broadcast);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished.");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BoostTimerService.this);
                prefs.edit().putBoolean(PREF_BOOST_ACTIVE, false).apply();
                sendBroadcast(new Intent(TIMER_DONE));
            }
        };

        //start boost
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BoostTimerService.this);
        prefs.edit().putBoolean(PREF_BOOST_ACTIVE, true).apply();
        mCDT.start();
    }

    @Override
    public void onDestroy() {
        mCDT.cancel();
        Log.i(TAG, "Timer cancelled.");
        Log.i(TAG, "Timer service destroyed.");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
