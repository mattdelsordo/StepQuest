package project3.csc214.stepquest.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import java.util.ArrayList;

import project3.csc214.stepquest.R;

/**
 * This Activity controls the rest of the app
 */

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ProgressFragment mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set adapter to view pager
        mViewPager = (ViewPager)findViewById(R.id.viewpager_main);
        ScreenPagerAdapter pagerAdapter = new ScreenPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

        //put progress bar in frame
        mProgress = (ProgressFragment)getSupportFragmentManager().findFragmentById(R.id.frame_main_progress);
        if(mProgress == null){
            mProgress = new ProgressFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main_progress, mProgress).commit();
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
    }
}
