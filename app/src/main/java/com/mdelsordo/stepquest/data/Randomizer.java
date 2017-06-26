package com.mdelsordo.stepquest.data;

import android.content.Context;
import android.content.res.Resources;

import com.mdelsordo.stepquest.R;

import java.util.Random;

/**
 * Created by mdelsord on 6/26/17.
 * Provides randomized names to label things for flavor.
 */

public class Randomizer {

    private String[] mHeroNames;
    private Random mRand;

    private static Randomizer sSingleton;
    public Randomizer(Context context){
        mRand = new Random();
        Resources res = context.getResources();
        mHeroNames = res.getStringArray(R.array.names);
    }

    //Singleton because this needs context to get the resources
    public static Randomizer getInstance(Context context){
        if(sSingleton == null) sSingleton = new Randomizer(context);
        return sSingleton;
    }

    //returns a hero name
    public String getHeroName(){
        return mHeroNames[mRand.nextInt(mHeroNames.length)];
    }
}
