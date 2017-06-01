package com.mdelsordo.stepquest.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.services.MusicManagerService;

public class CharacterCreationActivity extends AppCompatActivity implements CharacterCreationFragment.CreationCompleteListener{
    private static final String TAG = "CreationActivity";

    CharacterCreationFragment fragment;

    //stuff for a new intent
    public static final int REQUEST_CHARACTER_INFO = 3;
    public static Intent newInstance(AppCompatActivity a){
        return new Intent(a, CharacterCreationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_creation);

        setTitle(getString(R.string.create_your_hero));

        //put fragment in frame
        fragment = (CharacterCreationFragment)getSupportFragmentManager().findFragmentById(R.id.frame_charactercreation);
        if(fragment == null){
            fragment = new CharacterCreationFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_charactercreation, fragment).commit();
        }
    }

    @Override
    public void creationComplete(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

            mMusicPlayer.play(MusicManagerService.MAIN_JINGLE);
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
