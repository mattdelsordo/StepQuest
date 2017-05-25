package project3.csc214.stepquest.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.Saver;
import project3.csc214.stepquest.model.AdventureLog;
import project3.csc214.stepquest.model.EffectPlayer;
import project3.csc214.stepquest.util.FragmentTransitionBuilder;

public class AdventureLogActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;

    //sound stuff
    private EffectPlayer mEffectPlayer;
    private boolean mPlayEffects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure_log);

        //load sounds
        mEffectPlayer = new EffectPlayer(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayEffects = prefs.getBoolean(SettingsFragment.PREF_EFFECTS, true);

        //handle menu navigation
        mBottomNav = (BottomNavigationView)findViewById(R.id.bnv_shop);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fl_log_mainframe);
                switch(item.getItemId()){
                    case R.id.m_log_journal:
                        if(!(current instanceof JournalFragment))swapFragment(new JournalFragment(), FragmentTransitionBuilder.leftToRight(AdventureLogActivity.this));
                        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
                        return true;
                    case R.id.m_log_statistics:
                        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.CLICK);
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
        if(mPlayEffects)mEffectPlayer.play(EffectPlayer.FRAGMENT_SWAP);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Saver.saveAll(this, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEffectPlayer.release();
    }
}
