package project3.csc214.stepquest.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.Saver;
import project3.csc214.stepquest.model.*;
import project3.csc214.stepquest.pedometer.NoPedometerDialog;
import project3.csc214.stepquest.pedometer.PedometerService;

/**
 * This Activity controls the rest of the app
 */

public class MainActivity extends AppCompatActivity implements EventQueue.MakeToastListener, ActiveCharacter.LevelUpListener, SettingsFragment.SettingsListener {
    private static final String TAG = "MainActivity";
    private static final String PREF_HAS_SENSOR = "pref_has_sensor";
    public static final int RC = 4;

    private ProgressFragment mProgress;
    private MusicPlayerFragment mMusic;
    private EffectPlayer mEffectPlayer;
    private boolean mPlayMusic, mPlayEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start up effect player
        mEffectPlayer = new EffectPlayer(this);

        //put progress bar in frame
        mProgress = (ProgressFragment)getSupportFragmentManager().findFragmentById(R.id.frame_main_progress);
        if(mProgress == null){
            mProgress = new ProgressFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main_progress, mProgress).commit();
        }

        //put music in frame/play it?
        mMusic = (MusicPlayerFragment)getSupportFragmentManager().findFragmentById(R.id.frame_main_musicplayer);
        if(mMusic == null){
            Log.i(TAG, "Creating music player");
            mMusic = MusicPlayerFragment.newInstance("main_track_zacwilkins_loopermandotcom.wav");
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main_musicplayer, mMusic).commit();
        }

        //put fragment in main pane
        if(getSupportFragmentManager().findFragmentById(R.id.frame_main_gamepane) == null){
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main_gamepane, new CharacterInfoFragment()).commit();
        }

        //retrieve sound settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayMusic = prefs.getBoolean(SettingsFragment.PREF_MUSIC, true);
        mPlayEffects = prefs.getBoolean(SettingsFragment.PREF_EFFECTS, true);

        //connect to pedometer service
        doBindService();

        //do check for pedometer
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasSensor = prefs.getBoolean(PREF_HAS_SENSOR, false);
        Log.i(TAG, "hasSensor=" + hasSensor);
        if(hasSensor == false){
            SensorManager manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            Sensor pedometer = manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            Log.i(TAG, "pedometer=" + pedometer);
            if(pedometer == null){
                prefs.edit().putBoolean(PREF_HAS_SENSOR, false).apply();
                new NoPedometerDialog().show(getSupportFragmentManager(), NoPedometerDialog.TAG);
            }
        }
    }

    //replaces the fragment in the main frame with another
    private void swapFragments(Fragment frag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //TODO: add transaction animations with XML animation definitions
        ft.replace(R.id.frame_main_gamepane, frag).commit();
        Log.i(TAG, "Game fragment swapped.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventQueue.getInstance(getApplicationContext()).bindToastListener(this);
        ActiveCharacter.getInstance(this).bindLevelUpListener(this);

        //check whether a level up is possible
        if(ActiveCharacter.getInstance(this).getActiveCharacter().getLvlUpTokenAmnt() > 0) doLevelUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //save everything
        Saver.saveAll(this);

        EventQueue.getInstance(getApplicationContext()).unbindToastListener();
        ActiveCharacter.getInstance(this).unbindLevelUpListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEffectPlayer.release();
    }

    //Starts up pedometer service, somehow. I'm not entirely sure what does what but it seems to work
    private PedometerService mService;
    private boolean mIsBound;
    private ServiceConnection mConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((PedometerService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private void doBindService(){
        //bindService(new Intent(MainActivity.this, PedometerService.class), mConnection, Context.BIND_AUTO_CREATE);
        getApplicationContext().bindService(new Intent(getApplicationContext(), PedometerService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService(){
        if(mIsBound){
            unbindService(mConnection);
            mIsBound = false;
        }
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
            if(mPlayEffects)mEffectPlayer.play(EffectPlayer.LEVEL_UP);
            startActivity(new Intent(MainActivity.this, LevelUpActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
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
                if(!(current instanceof CharacterInfoFragment)) swapFragments(new CharacterInfoFragment());
                break;
            case R.id.menu_inventory: handled = true;
                if(!(current instanceof InventoryFragment)) swapFragments(new InventoryFragment());
                break;
            case R.id.menu_save: handled = true;
                Saver.saveAll(this);
                break;
            case R.id.menu_settings: handled = true;
                if(!(current instanceof SettingsFragment)) swapFragments(new SettingsFragment());
                break;
            case R.id.menu_misc: handled = true;
                startActivity(new Intent(MainActivity.this, MiscActivity.class));
                break;
            default: handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

    @Override
    public void toggleEffects(boolean shouldPlay) {
        mPlayEffects = shouldPlay;
    }

    @Override
    public void toggleMusic(boolean shouldPlay) {
        mPlayMusic = shouldPlay;
        if(mPlayMusic) mMusic.playMusic();
        else mMusic.stopMusic();
    }

    @Override
    public void quitDelete() {
        stopService(new Intent(MainActivity.this, PedometerService.class));
        setResult(RESULT_CANCELED);
        finish();
    }
}
