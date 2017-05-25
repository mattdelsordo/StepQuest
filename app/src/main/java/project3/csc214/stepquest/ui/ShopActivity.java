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
import android.widget.TextView;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.Saver;
import project3.csc214.stepquest.model.EffectPlayer;
import project3.csc214.stepquest.util.FragmentTransitionBuilder;
import project3.csc214.stepquest.util.ShopFragmentListener;
import project3.csc214.stepquest.model.ActiveCharacter;

/**
 * This activity handles functionality for the shop. User can buy/sell
 * items and buy boosts.
 */
public class ShopActivity extends AppCompatActivity implements ShopFragmentListener {

    private BottomNavigationView mBottomNav;
    private TextView mGoldTotal;

    //music player handlers
    private EffectPlayer mEffectPlayer;
    private boolean mPlayEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //load sounds
        mEffectPlayer = new EffectPlayer(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayEffects = prefs.getBoolean(SettingsFragment.PREF_EFFECTS, true);

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
