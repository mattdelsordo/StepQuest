package project3.csc214.stepquest.util;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import project3.csc214.stepquest.R;

/**
 * Created by mdelsord on 5/24/17.
 * Handles creating animated fragment transitions
 */

public class FragmentTransitionBuilder {

    public static FragmentTransaction leftToRight(AppCompatActivity activity){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        return ft;
    }

    public static FragmentTransaction rightToLeft(AppCompatActivity activity){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        return ft;
    }

}
