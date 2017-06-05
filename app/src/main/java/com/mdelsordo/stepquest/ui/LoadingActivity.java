package com.mdelsordo.stepquest.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.data.Saver;
import com.mdelsordo.stepquest.model.ActiveCharacter;
import com.mdelsordo.stepquest.model.Character;
import com.mdelsordo.stepquest.model.Dungeon;
import com.mdelsordo.stepquest.model.EventQueue;
import com.mdelsordo.stepquest.model.PlotQueue;
import com.mdelsordo.stepquest.model.Race;
import com.mdelsordo.stepquest.model.Vocation;
import com.mdelsordo.stepquest.services.MusicManagerService;
import com.mdelsordo.stepquest.services.PedometerService;

public class LoadingActivity extends AppCompatActivity {
    public static final String TAG = "LoadingActivity";

    public static Intent newInstance(Context c){
        Intent i = new Intent(c, LoadingActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //load all information from database, if it isn't there, make a new game
        //Character activeCharacter = ActiveCharacter.getInstance(this).getActiveCharacter();

//        if(activeCharacter == null){
//            Log.i(TAG, "No character found, creating new one.");
//            startActivityForResult(CharacterCreationActivity.newInstance(this), CharacterCreationActivity.REQUEST_CHARACTER_INFO);
//        }
//        else startActivityForResult(new Intent(this, MainActivity.class), MainActivity.RC);

        new LoadDataTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CharacterCreationActivity.REQUEST_CHARACTER_INFO && resultCode == RESULT_OK){

            String name = data.getStringExtra(CharacterCreationFragment.ARG_NAME);
            int race = data.getIntExtra(CharacterCreationFragment.ARG_RACE, Race.BIRDPERSON);
            int vocation = data.getIntExtra(CharacterCreationFragment.ARG_CLASS, Vocation.FIGHTER);
            int[] stats = data.getIntArrayExtra(CharacterCreationFragment.ARG_STATS);

            Race bigRace = Race.newRace(race);
            Vocation bigVocation = Vocation.newVocation(vocation);

            Character newGuy = new Character(name, bigVocation, bigRace, stats);
            ActiveCharacter.getInstance(this).setActiveCharacter(newGuy);

            EventQueue.getInstance(this).addEvents(Dungeon.generateBackstory(this));

            //save everything
            Saver.saveAll(this, false);

            //start main activity
            startActivityForResult(new Intent(this, MainActivity.class), MainActivity.RC);

        }
        else if(requestCode == MainActivity.RC && resultCode == MainActivity.RESULT_DELETE){
            stopService(new Intent(LoadingActivity.this, PedometerService.class));
            //Log.i(TAG, "Deleing data...");
            Saver.deleteAll(this);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().clear().commit();
            finishAndRemoveTask();
        }
        else finish();
    }

    private void doesCharacterExistCheck(Character c){
        if(c == null){
            //Log.i(TAG, "No character found, creating new one.");
            startActivityForResult(CharacterCreationActivity.newInstance(this), CharacterCreationActivity.REQUEST_CHARACTER_INFO);
        }
        else{
            startActivityForResult(MainActivity.newInstance(this), MainActivity.RC);
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }


    //load everything in an async task to cut down on this lag
    private class LoadDataTask extends AsyncTask<Void, Void, Character>{

        @Override
        protected Character doInBackground(Void... params) {
            PlotQueue.getInstance(LoadingActivity.this);
            return ActiveCharacter.getInstance(LoadingActivity.this).getActiveCharacter();
        }

        @Override
        protected void onPostExecute(Character character) {
            doesCharacterExistCheck(character);
        }
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
