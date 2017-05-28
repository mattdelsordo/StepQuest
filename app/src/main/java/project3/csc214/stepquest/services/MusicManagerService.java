package project3.csc214.stepquest.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import project3.csc214.stepquest.R;

/**
 * Created by mdelsord on 5/28/17.
 * Handle music in a service so that it persists throughout the app
 */

public class MusicManagerService extends Service implements MediaPlayer.OnErrorListener{
    private static final String TAG = "MusicManagerService";


    private final IBinder mBinder = new MusicBinder();
    private MediaPlayer mPlayer;
    private int position = 0;

    public class MusicBinder extends Binder {
        public MusicManagerService getService(){
            return MusicManagerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Returned binder.");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = MediaPlayer.create(this, R.raw.main_jingle);
        mPlayer.setOnErrorListener(this);

        if(mPlayer != null){
            mPlayer.setLooping(true);
            mPlayer.setVolume(100,100);
        }

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                onError(mPlayer, what, extra);
                return true;
            }
        });

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "Music player is prepared.");
            }
        });

        Log.i(TAG, "Music manager created.");
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        mPlayer.start();
        return START_STICKY;
    }

    public void pauseMusic(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
            position = mPlayer.getCurrentPosition();
        }
    }

    public void resumeMusic(){
        if(!mPlayer.isPlaying()){
            mPlayer.seekTo(position);
            mPlayer.start();
        }
    }

    public void stopMusic(){
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPlayer!=null){
            try{
                mPlayer.stop();
                mPlayer.release();
            }finally {
                mPlayer = null;
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "Music player failed!", Toast.LENGTH_SHORT).show();
        if(mPlayer!=null){
            try{
                mPlayer.stop();
                mPlayer.release();
            }finally {
                mPlayer = null;
            }
        }
        return false;
    }
}
