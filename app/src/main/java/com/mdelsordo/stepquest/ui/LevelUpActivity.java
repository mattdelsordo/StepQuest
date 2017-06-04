package com.mdelsordo.stepquest.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.data.Saver;
import com.mdelsordo.stepquest.model.EffectPlayer;
import com.mdelsordo.stepquest.model.EventQueue;
import com.mdelsordo.stepquest.services.MusicManagerService;

public class LevelUpActivity extends AppCompatActivity implements LevelUpFragment.LevelUpDoneListener, EventQueue.MakeToastListener{
    private static final String TAG = "LevelUpActivity";

    public static boolean sIsRunning = false;
    private boolean mPlayMusic, mPlayEffects;
    private EffectPlayer mEffectPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);

        setTitle(getString(R.string.you_levelled_up_allocate_your_stat_points));

        //put fragment in thing
        LevelUpFragment frag = (LevelUpFragment)getSupportFragmentManager().findFragmentById(R.id.frame_levelupfragment);
        if(frag == null){
            frag = new LevelUpFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_levelupfragment, frag).commit();
        }

        //retrieve sound settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayMusic = prefs.getBoolean(SettingsFragment.PREF_MUSIC, true);
        mPlayEffects = prefs.getBoolean(SettingsFragment.PREF_EFFECTS, true);

        mEffectPlayer = new EffectPlayer(this);
        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.LEVEL_UP);
    }

    @Override
    public void lvlUpDone() {
        Saver.saveAll(this.getApplicationContext(), false);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sIsRunning = true;

        Intent intent = new Intent(this, MusicManagerService.class);
        bindService(intent, mSCon, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sIsRunning = false;

        if(mIsBound){
            unbindService(mSCon);
            mIsBound = false;
        }
    }

    @Override
    public void makeToast(String text, int duration) {

    }

    @Override
    public void playJingle() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventQueue.getInstance(this).bindToastListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventQueue.getInstance(this).unbindToastListener();
    }

    //Bind activity to music player service
    private boolean mIsBound = false;
    private MusicManagerService mMusicPlayer;
    private ServiceConnection mSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Music service connected.");
            MusicManagerService.MusicBinder binder = (MusicManagerService.MusicBinder)service;
            mMusicPlayer = binder.getService();
            mIsBound = true;

            if(mPlayMusic&&!mMusicPlayer.isPlaying())mMusicPlayer.play(MusicManagerService.MAIN_JINGLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.i(TAG, "Music service disconnected");
            mIsBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEffectPlayer.release();
    }
}
