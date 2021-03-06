package com.mdelsordo.stepquest.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.data.Saver;
import com.mdelsordo.stepquest.model.EffectPlayer;
import com.mdelsordo.stepquest.model.EventQueue;
import com.mdelsordo.stepquest.services.MusicManagerService;
import com.mdelsordo.stepquest.util.FragmentTransitionBuilder;

public class AdventureLogActivity extends AppCompatActivity implements EventQueue.MakeToastListener{
    private static final String TAG = "AdventureLogActivity";

    private BottomNavigationView mBottomNav;

    //sound stuff
    //private EffectPlayer mEffectPlayer;
    //private boolean mPlayEffects, mPlayMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure_log);
        setTitle(getString(R.string.advlog_activity_title));

        //load sounds
        //mEffectPlayer = new EffectPlayer(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //mPlayEffects = prefs.getBoolean(SettingsFragment.PREF_EFFECTS, true);
        //mPlayMusic = prefs.getBoolean(SettingsFragment.PREF_MUSIC, true);

        //handle menu navigation
        mBottomNav = (BottomNavigationView)findViewById(R.id.bnv_shop);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fl_log_mainframe);
                switch(item.getItemId()){
                    case R.id.m_log_journal:
                        if(!(current instanceof JournalFragment))swapFragment(new JournalFragment(), FragmentTransitionBuilder.leftToRight(AdventureLogActivity.this));
                        if(mMusicPlayer!=null)mMusicPlayer.playEffect(EffectPlayer.CLICK);
                        //if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                        return true;
                    case R.id.m_log_statistics:
                        mMusicPlayer.playEffect(EffectPlayer.CLICK);
                        //if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                        if(!(current instanceof StatisticFragment))swapFragment(new StatisticFragment(), FragmentTransitionBuilder.rightToLeft(AdventureLogActivity.this));
                        return true;
                    default: return false;
                }
            }
        });

        if(getSupportFragmentManager().findFragmentById(R.id.fl_log_mainframe) == null){
            mBottomNav.setSelectedItemId(R.id.m_log_journal);
        }
    }

    //swaps fragments in the main frame
    public void swapFragment(Fragment fragment, FragmentTransaction ft){
        ft.replace(R.id.fl_log_mainframe, fragment).commit();
        if(mMusicPlayer!=null)mMusicPlayer.playEffect(EffectPlayer.FRAGMENT_SWAP);
        //if(mPlayEffects)mEffectPlayer.play(EffectPlayer.FRAGMENT_SWAP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventQueue.getInstance(this).bindToastListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Saver.saveAll(this, false);
        EventQueue.getInstance(this).unbindToastListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mEffectPlayer.release();
    }

    @Override
    public void makeToast(String text, int duration) {

    }

    @Override
    public void playJingle() {

    }


    //Bind activity to music player service
    private boolean mIsBound = false;
    private MusicManagerService mMusicPlayer;
    private ServiceConnection mSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.i(TAG, "Music service connected.");
            MusicManagerService.MusicBinder binder = (MusicManagerService.MusicBinder)service;
            mMusicPlayer = binder.getService();
            mIsBound = true;

            mMusicPlayer.playMusic(MusicManagerService.MAIN_JINGLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.i(TAG, "Music service disconnected");
            mIsBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicManagerService.class);
        bindService(intent, mSCon, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mIsBound){
            unbindService(mSCon);
            mIsBound = false;
        }
    }
}
