package project3.csc214.stepquest.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.EventList;
import project3.csc214.stepquest.model.*;
import project3.csc214.stepquest.model.Character;

/**
 * This Activity controls the rest of the app
 */

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ProgressFragment mProgress;
    private ScreenPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set adapter to view pager
        mViewPager = (ViewPager)findViewById(R.id.viewpager_main);
        mAdapter = new ScreenPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        //put progress bar in frame
        mProgress = (ProgressFragment)getSupportFragmentManager().findFragmentById(R.id.frame_main_progress);
        if(mProgress == null){
            mProgress = new ProgressFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main_progress, mProgress).commit();
        }

        EventList.getInstance(getApplicationContext());


        /** Do check that there's a character in the database, otherwise, make a new one.*/
        Character activeCharacter = ActiveCharacter.getInstance().getActiveCharacter();
        if(activeCharacter == null) startActivityForResult(CharacterCreationActivity.newInstance(this), CharacterCreationActivity.REQUEST_CHARACTER_INFO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CharacterCreationActivity.REQUEST_CHARACTER_INFO){
            if(resultCode == RESULT_OK){
                String name = data.getStringExtra(CharacterCreationFragment.ARG_NAME);
                int race = data.getIntExtra(CharacterCreationFragment.ARG_RACE, Race.BIRDPERSON);
                int vocation = data.getIntExtra(CharacterCreationFragment.ARG_CLASS, Vocation.FIGHTER);
                int[] stats = data.getIntArrayExtra(CharacterCreationFragment.ARG_STATS);

                Race bigRace = Race.newRace(race);
                Vocation bigVocation = Vocation.newVocation(vocation);

                Character newGuy = new Character(name, bigVocation, bigRace, stats);
                ActiveCharacter.getInstance().setActiveCharacter(newGuy);
                //mAdapter.refreshCharInfo(newGuy);
            }
        }
    }

    //adapter for the viewpager
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
}
