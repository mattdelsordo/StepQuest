package com.mdelsordo.stepquest.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.data.Saver;
import com.mdelsordo.stepquest.model.EventQueue;
import com.mdelsordo.stepquest.ui.LoadingActivity;


/**
 * This service handles the pedometer, updating the step count in the event queue
 * on every step.
 */
public class PedometerService extends Service implements SensorEventListener, EventQueue.NotificationListener{
    private static final String TAG = "PedometerService";
    private static final int RC_REMINDER = 2;

    //interval in between each save
    private static final int SAVE_INTERVAL = 300000; //five minutes

    private SensorManager mManager;
    private Sensor mStepSensor;
    private EventQueue mEventQueue;

    private Handler mSaveHandler;
    private Runnable mSaveTimer;

    public class LocalBinder extends Binder{
        public PedometerService getService(){
            return PedometerService.this;
        }
    }

    public PedometerService(){
        //super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Log.i(TAG, "service oncreate called");

        mManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mStepSensor = mManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mEventQueue  = EventQueue.getInstance(getApplicationContext());
        mEventQueue.bindNotificationListener(this);

        //register step counter, is this the right place?
        //TODO: this delay may be overkill
        mManager.registerListener(this, mStepSensor, SensorManager.SENSOR_DELAY_FASTEST);

        /** THIS MUST BE COMMENTED OUT IN PRODUCTION **/
        simulateSteps();


        //set up save timer
//        mSaveHandler = new Handler();
//        mSaveTimer = new Runnable() {
//            @Override
//            public void run() {
//                Saver.saveAll(PedometerService.this);
//                mSaveHandler.postDelayed(this, SAVE_INTERVAL);
//            }
//        };
//        mSaveHandler.postDelayed(mSaveTimer, SAVE_INTERVAL);
    }

    //recieves interactions from clients
    private final IBinder mBinder = new LocalBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) mEventQueue.incrementProgress(this);
//        Log.i(TAG, "Step taken!");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //simluates steps because I can't actually do that at all on an emulator
    public void simulateSteps(){
        //Handler is suggested but I'm not totally clear on how that works
        final Handler h = new Handler();
        final int delay  = 1000;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEventQueue.incrementProgress(PedometerService.this);
                h.postDelayed(this, delay);
            }
        }, delay);

    }

    //make sure to save all
    @Override
    public void onDestroy() {
        Saver.saveAll(getApplicationContext(), false);
        //mSaveHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void notifyUser(String message){
        //Log.i(TAG, "Notifying user: " + message);

        Intent main = LoadingActivity.newInstance(this);
        PendingIntent pi = PendingIntent.getActivity(
                this,
                0,
                main,
                0
        );

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(message)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(getString(R.string.hark))
                .setContentText(message)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat m = NotificationManagerCompat.from(this);
        m.notify(0, notification);
    }
}
