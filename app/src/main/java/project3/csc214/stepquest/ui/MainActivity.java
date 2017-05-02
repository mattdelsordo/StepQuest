package project3.csc214.stepquest.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.EventList;
import project3.csc214.stepquest.data.Saver;
import project3.csc214.stepquest.data.WeaponList;
import project3.csc214.stepquest.model.*;
import project3.csc214.stepquest.model.Character;
import project3.csc214.stepquest.pedometer.PedometerService;

/**
 * This Activity controls the rest of the app
 */

public class MainActivity extends AppCompatActivity implements EventQueue.MakeToastListener, ActiveCharacter.LevelUpListener{
    private static final String TAG = "MainActivity";
    public static final int RC = 4;

    private ViewPager mViewPager;
    private ProgressFragment mProgress;
    private ScreenPagerAdapter mAdapter;
    private MusicPlayerFragment mMusic;

    //requisite media player components
    private MediaPlayer mPlayer;
    private AssetManager mAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set adapter to view pager
        mViewPager = (ViewPager)findViewById(R.id.viewpager_main);
        mAdapter = new ScreenPagerAdapter(getSupportFragmentManager());
        //TODO: this keeps causing the program to crash in landscape mode, I might just get rid of the viewpager all together
        mViewPager.setAdapter(mAdapter);

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

        //load list of events
        //EventList.getInstance(getApplicationContext());


//        /** Do check that there's a character in the database, otherwise, make a new one.*/
        //This happens in the loading activity now
//        Character activeCharacter = ActiveCharacter.getInstance().getActiveCharacter();
//        if(activeCharacter == null) startActivityForResult(CharacterCreationActivity.newInstance(this), CharacterCreationActivity.REQUEST_CHARACTER_INFO);
//        else doBindService();
        doBindService();
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

        //doUnbindService(); //unbind service so it doesnt throw that error

        //this might stop the service when changing orientations but it already crashes during that so thats an issue for another day
        //actually it might not be an issue cause itll get just started up again if this activity is recreated
        //stopService(new Intent(this, PedometerService.class));
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
    public void doLevelUp() {
        //do check to avoid popping up tons of things at once
        if(!LevelUpActivity.sIsRunning) startActivity(new Intent(MainActivity.this, LevelUpActivity.class));
    }

    //adapter for the viewpager
    //TODO: might get rid of this
    public class ScreenPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> mFragments;

        public ScreenPagerAdapter(FragmentManager fm) {
            super(fm);
            //TODO: the way this is implemented might need heavily changed
            mFragments = new ArrayList<>();
            mFragments.add(new CharacterInfoFragment());
            mFragments.add(new InventoryFragment());
        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void refreshCharInfo(Character c){
            CharacterInfoFragment info = (CharacterInfoFragment) mFragments.get(0);
            info.updateUI(c);
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
        switch(item.getItemId()){
            case R.id.menu_charinfo: handled = true;
                Log.i(TAG, "Character");
                break;
            case R.id.menu_inventory: handled = true;
                Log.i(TAG, "Inventory");
                break;
            case R.id.menu_save: handled = true;
                Log.i(TAG, "Save");
                break;
            case R.id.menu_settings: handled = true;
                Log.i(TAG, "Settings");
                break;
            case R.id.menu_misc: handled = true;
                Log.i(TAG, "Misc");
                break;
            default: handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }
}
