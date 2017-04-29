package project3.csc214.stepquest.pedometer;

import android.app.IntentService;
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
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import project3.csc214.stepquest.model.EventQueue;


/**
 * This service handles the pedometer, updating the step count in the event queue
 * on every step.
 */
public class PedometerService extends Service implements SensorEventListener{
    private static final String TAG = "PedometerService";
    private static final int RC_REMINDER = 2;

    private SensorManager mManager;
    private Sensor mStepSensor;

    private EventQueue mEventQueue;

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

        Log.i(TAG, "service oncreate called");

        mManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mStepSensor = mManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mEventQueue  = EventQueue.getInstance(getApplicationContext());

        //register step counter, is this the right place?
        //TODO: this delay may be overkill
        mManager.registerListener(this, mStepSensor, SensorManager.SENSOR_DELAY_FASTEST);

        //simulateSteps();
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
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) mEventQueue.incrementProgress();
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
                mEventQueue.incrementProgress();
                h.postDelayed(this, delay);
            }
        }, delay);

    }
}
