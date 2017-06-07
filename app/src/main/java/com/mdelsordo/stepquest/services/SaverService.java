package com.mdelsordo.stepquest.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.mdelsordo.stepquest.data.Saver;
import com.mdelsordo.stepquest.util.Logger;

/**This service handles saving the game when the process is killed*/
public class SaverService extends Service {
    private static final String TAG = "SaverService";

    public SaverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Logger.i(TAG, "Saver Service called");
        Saver.saveAll(this, false);

        //save Boost info
        if(BoostTimerService.sServiceExists){
            ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    BoostTimerService.BoostBinder binder = (BoostTimerService.BoostBinder)service;
                    binder.getService().saveBoost();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
            bindService(new Intent(this, BoostTimerService.class), connection, BIND_AUTO_CREATE);
        }
        super.onTaskRemoved(rootIntent);
    }
}
