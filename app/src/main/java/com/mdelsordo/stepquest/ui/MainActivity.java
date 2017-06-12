package com.mdelsordo.stepquest.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.data.Saver;
import com.mdelsordo.stepquest.model.*;
import com.mdelsordo.stepquest.services.*;
import com.mdelsordo.stepquest.util.BasicOKDialog;
import com.mdelsordo.stepquest.util.FragmentTransitionBuilder;
import com.mdelsordo.stepquest.util.InventorySoundListener;
import com.mdelsordo.stepquest.util.Logger;
import com.mdelsordo.stepquest.util.NoPedometerDialog;
import com.mdelsordo.stepquest.services.PedometerService;

import java.lang.reflect.Method;

/**
 * This Activity controls the rest of the app
 */

public class MainActivity extends AppCompatActivity implements EventQueue.MakeToastListener, ActiveCharacter.LevelUpListener, SettingsFragment.SettingsListener, InventorySoundListener, PlotQueue.PlotAdvancedListener {
    private static final String TAG = "MainActivity";
    private static final String PREF_DONE_SENSOR_CHECK = "pref_has_sensor";
    private static final String CHECKED_SENSOR_THIS_RUN = "checked sensor this run";
    private static final String ARG_MUSIC_PLAYING = "arg_isplaying";
    private boolean mDoneSensorCheck;
    public static final int RC = 4;
    public static final int RESULT_DELETE = 10;

    private ProgressFragment mProgress;
    //private MusicPlayerFragment mMusic;
    private EffectPlayer mEffectPlayer;
    private boolean mPlayMusic, mPlayEffects;

    public static Intent newInstance(Context c){
        Intent intent = new Intent(c, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("");

        //connect to pedometer service
        startService(new Intent(getApplicationContext(), PedometerService.class));
        //connect to saver service
        startService(new Intent(getApplicationContext(), SaverService.class));

        //start up effect player
        mEffectPlayer = new EffectPlayer(this);

        //put progress bar in frame
        mProgress = (ProgressFragment)getSupportFragmentManager().findFragmentById(R.id.frame_main_progress);
        if(mProgress == null){
            mProgress = new ProgressFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main_progress, mProgress).commit();
        }

        //put fragment in main pane
        if(getSupportFragmentManager().findFragmentById(R.id.frame_main_gamepane) == null){
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main_gamepane, new CharacterInfoFragment()).commit();
        }

        //get info from saved state
        if(savedInstanceState != null){
            mDoneSensorCheck = savedInstanceState.getBoolean(CHECKED_SENSOR_THIS_RUN);
            mPlayMusic = savedInstanceState.getBoolean(ARG_MUSIC_PLAYING);
        }

        //retrieve sound settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayMusic = prefs.getBoolean(SettingsFragment.PREF_MUSIC, true);
        mPlayEffects = prefs.getBoolean(SettingsFragment.PREF_EFFECTS, true);



        //do check for pedometer
        boolean doneCheck = prefs.getBoolean(PREF_DONE_SENSOR_CHECK, false);
        //Log.i(TAG, "hasSensor=" + doneCheck);
        if(!doneCheck){
            boolean hasPedometer = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
            //Log.i(TAG, "pedometer=" + hasPedometer);
            if(hasPedometer == false){
                new NoPedometerDialog().show(getSupportFragmentManager(), NoPedometerDialog.TAG);
                if(mPlayEffects)mEffectPlayer.play(EffectPlayer.DIALOG);
            }
            prefs.edit().putBoolean(PREF_DONE_SENSOR_CHECK, true).apply();
        }

        //check whether the current boost should continue
        long boostTimeRemaining = prefs.getLong(BoostTimerService.PREF_BOOST_TIME_REMAINING, -1);
        if(boostTimeRemaining > 0){
            Double boostMagnitude = Double.longBitsToDouble(prefs.getLong(BoostTimerService.PREF_BOOST_MAGNITUDE, 1));
            Boost boost = new Boost(boostMagnitude, boostTimeRemaining);
            ActiveCharacter.getInstance(this).setBoost(boost);
            startService(BoostTimerService.newInstance(this, boostTimeRemaining));
        }
    }

    //replaces the fragment in the main frame with another
    private void swapFragments(Fragment frag, FragmentTransaction ft){
        ft.replace(R.id.frame_main_gamepane, frag).commit();
        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.FRAGMENT_SWAP);
        //Log.i(TAG, "Game fragment swapped.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(mPlayMusic) mMusicPlayer.resumeMusic();
        EventQueue.getInstance(getApplicationContext()).bindToastListener(this);
        ActiveCharacter.getInstance(this).bindLevelUpListener(this);
        PlotQueue.getInstance(this).bindPlotListener(this);

        //check whether a level up is possible
        if(ActiveCharacter.getInstance(this).getActiveCharacter().getLvlUpTokenAmnt() > 0) doLevelUp();
        else{
            checkPlotAdvanced();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if(mPlayMusic)mMusicPlayer.pauseMusic();
        //save everything
        Logger.i(TAG, "Main saving");
        Saver.saveAll(this, false);

        EventQueue.getInstance(getApplicationContext()).unbindToastListener();
        ActiveCharacter.getInstance(this).unbindLevelUpListener();
        PlotQueue.getInstance(this).unbindPlotListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEffectPlayer.release();
    }

    @Override
    public void makeToast(String text, int duration) {
        Toast t = Toast.makeText(this, text, duration);
        t.setGravity(Gravity.BOTTOM, 0,20);
        t.show();
    }

    @Override
    public void playJingle() {
        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.TASK_DONE);
    }

    @Override
    public void doLevelUp() {
        //do check to avoid popping up tons of things at once
        if(!LevelUpActivity.sIsRunning){
//            if(mPlayEffects)mEffectPlayer.play(EffectPlayer.LEVEL_UP);
            startActivity(new Intent(MainActivity.this, LevelUpActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    //handle menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled;

        //get current fragment to check what sort of thing it is
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.frame_main_gamepane);
        switch(item.getItemId()){
            case R.id.menu_charinfo: handled = true;
                if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                if(!(current instanceof CharacterInfoFragment)) swapFragments(new CharacterInfoFragment(), FragmentTransitionBuilder.leftToRight(this));
                break;
            case R.id.menu_inventory: handled = true;
                if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                if(!(current instanceof InventoryFragment)) swapFragments(new InventoryFragment(), FragmentTransitionBuilder.rightToLeft(this));
                break;
            case R.id.menu_save: handled = true;
                if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                Saver.saveAll(this, true);
                break;
            case R.id.menu_settings: handled = true;
                if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                if(!(current instanceof SettingsFragment)) swapFragments(new SettingsFragment(), FragmentTransitionBuilder.rightToLeft(this));
                break;
            case R.id.menu_shop:
                if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                handled = true;
                startActivity(new Intent(this, ShopActivity.class));
                break;
            case R.id.menu_advlog:
                if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                handled = true;
                startActivity(new Intent(this, AdventureLogActivity.class));
                break;
            case R.id.menu_tutorial:
                if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                handled = true;
                startActivity(TutorialActivity.newInstance(this, false));
                break;
            default: handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

    //adds icons to the overflow menu
    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e(TAG, "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void toggleEffects(boolean shouldPlay) {
        mPlayEffects = shouldPlay;
    }

    @Override
    public void toggleMusic(boolean shouldPlay) {
        mPlayMusic = shouldPlay;
        //do check for music playing
        if(mPlayMusic&&!mMusicPlayer.isPlaying())mMusicPlayer.play(MusicManagerService.MAIN_JINGLE);
        else mMusicPlayer.stopMusic();
    }

    @Override
    public void quitDelete() {
        setResult(RESULT_DELETE);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHECKED_SENSOR_THIS_RUN, mDoneSensorCheck);
        outState.putBoolean(ARG_MUSIC_PLAYING, mPlayMusic);
    }

    @Override
    public void playEffect(String effectPath) {
        if(mPlayEffects)mEffectPlayer.play(effectPath);
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

            //do check for music playing
            if(mPlayMusic&&!mMusicPlayer.isPlaying())mMusicPlayer.play(MusicManagerService.MAIN_JINGLE);
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

    @Override
    public void popDialog() {
        checkPlotAdvanced();
    }

    private void checkPlotAdvanced(){
        String plotText = PlotQueue.getInstance(this).plotAvailable();
        if(plotText != null) BasicOKDialog.newInstance(plotText).show(getSupportFragmentManager(), "Plot");
    }
}
