package project3.csc214.stepquest.ui;

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
import android.widget.TextView;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.Saver;
import project3.csc214.stepquest.model.EffectPlayer;
import project3.csc214.stepquest.model.EventQueue;
import project3.csc214.stepquest.services.MusicManagerService;
import project3.csc214.stepquest.util.FragmentTransitionBuilder;
import project3.csc214.stepquest.util.ShopFragmentListener;
import project3.csc214.stepquest.model.ActiveCharacter;

/**
 * This activity handles functionality for the shop. User can buy/sell
 * items and buy boosts.
 */
public class ShopActivity extends AppCompatActivity implements ShopFragmentListener, EventQueue.MakeToastListener {
    private static final String TAG = "ShopActivity";


    private BottomNavigationView mBottomNav;
    private TextView mGoldTotal;

    //music player handlers
    private EffectPlayer mEffectPlayer;
    private boolean mPlayEffects, mPlayMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //load sounds
        mEffectPlayer = new EffectPlayer(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayEffects = prefs.getBoolean(SettingsFragment.PREF_EFFECTS, true);
        mPlayMusic = prefs.getBoolean(SettingsFragment.PREF_MUSIC, true);

        mGoldTotal = (TextView)findViewById(R.id.tv_shop_goldtotal);

        //handle menu navigation
        mBottomNav = (BottomNavigationView)findViewById(R.id.bnv_shop);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fl_shop_mainframe);
                switch(item.getItemId()){
                    case R.id.m_shop_weapons:
                        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                        if(!(current instanceof ShopWeaponFragment)) swapFragment(new ShopWeaponFragment(), FragmentTransitionBuilder.leftToRight(ShopActivity.this));
                        return true;
                    case R.id.m_shop_boosts:
                        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                        if(!(current instanceof ShopBoostFragment))swapFragment(new ShopBoostFragment(), FragmentTransitionBuilder.rightToLeft(ShopActivity.this));
                        return true;
                    default: return false;
                }
            }
        });

        if(getSupportFragmentManager().findFragmentById(R.id.fl_shop_mainframe) == null){
            mBottomNav.setSelectedItemId(R.id.m_shop_weapons);
        }

        //update to current gold total
        updateGoldTotal(ActiveCharacter.getInstance(this).getActiveCharacter().getFunds());

    }

    //swaps fragments in the main frame
    public void swapFragment(Fragment fragment, FragmentTransaction ft){
        ft.replace(R.id.fl_shop_mainframe, fragment).commit();
        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.FRAGMENT_SWAP);
    }

    @Override
    public void updateGoldTotal(int goldTotal) {
        mGoldTotal.setText(Integer.toString(goldTotal));
    }

    @Override
    public void playEffect(String effectPath) {
        if(mPlayEffects)mEffectPlayer.play(effectPath);
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
        EventQueue.getInstance(this).unbindUpdateListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEffectPlayer.release();
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
            Log.i(TAG, "Music service connected.");
            MusicManagerService.MusicBinder binder = (MusicManagerService.MusicBinder)service;
            mMusicPlayer = binder.getService();
            mIsBound = true;

            if(mPlayMusic&&!mMusicPlayer.isPlaying())mMusicPlayer.play(MusicManagerService.MAIN_JINGLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Music service disconnected");
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
