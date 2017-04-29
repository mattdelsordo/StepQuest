package project3.csc214.stepquest.data;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.Weapon;

/**
 * Created by mdelsord on 4/16/17.
 * Static list of all weapons in the game,
 * associated with how many of each the player owns
 */

public class WeaponList {
    private static final String TAG = "WeaponList";

    //singleton
    private static WeaponList sWeaponList;

    //lists that contain the weapons
    private HashMap<String, Weapon> mWood;
    private HashMap<String, Weapon> mBronze;
    private HashMap<String, Weapon> mIron;
    private HashMap<String, Weapon> mSteel;
    private HashMap<String, Weapon> mObsidian;
    private HashMap<String, Weapon> mMithril;
    private HashMap<String, Weapon> mLegendary;
    private ArrayList<HashMap<String, Weapon>> mListList;

    private Context mAppContext;

    private WeaponList(Context appContext){
        mAppContext = appContext;

        //create lists
        mWood = new HashMap<>();
        mBronze = new HashMap<>();
        mIron = new HashMap<>();
        mSteel = new HashMap<>();
        mObsidian = new HashMap<>();
        mMithril = new HashMap<>();
        mLegendary = new HashMap<>();

        //fill lists
        fillGenericWeaponList(mWood, R.array.wood);
        fillGenericWeaponList(mBronze, R.array.bronze);
        fillGenericWeaponList(mIron, R.array.iron);
        fillGenericWeaponList(mSteel, R.array.steel);
        fillGenericWeaponList(mObsidian, R.array.obsidian);
        fillGenericWeaponList(mMithril, R.array.mithril);
        //fill legendary list
        fillLegendaryWeaponList(mLegendary, R.array.legendary_weapons);

        mListList = new ArrayList<>();
        mListList.add(mWood);
        mListList.add(mBronze);
        mListList.add(mIron);
        mListList.add(mSteel);
        mListList.add(mObsidian);
        mListList.add(mMithril);
        mListList.add(mLegendary);
    }

    public static WeaponList getInstance(Context appContext){
        if(sWeaponList == null) sWeaponList = new WeaponList(appContext);
        return sWeaponList;
    }

    //fills a given list with a given list from xml
    private void fillGenericWeaponList(HashMap<String, Weapon> list, int arrayId){
        //load weapons from xml as strings
        Log.i(TAG, "Loading weapon list...");
        Resources res = mAppContext.getResources();
        TypedArray ta = res.obtainTypedArray(arrayId);
        int arrayLength = ta.length();
        String[][] array = new String[arrayLength][];

        //start by loading in whole array
        for(int i = 0; i < arrayLength; i++){
            int id = ta.getResourceId(i, 0);
            if(id > 0){
                array[i] = res.getStringArray(id);
            }else{
                Log.e(TAG, "id " + id + "==0?");
            }
        }
        ta.recycle();

        //parse array into events
        double material = Weapon.parseMaterial(array[0][0]); //first element is the classification
        for(int i = 1; i < array.length; i++){
            String id = array[i][0];
            String name = array[i][1];
            int type = Integer.parseInt(array[i][2]);
            Weapon weapon = new Weapon(name, type, material);
            list.put(id, weapon);
        }

        //testing
        for(Weapon w : list.values()){
            Log.i(TAG, w.toString());
        }
    }

    private void fillLegendaryWeaponList(HashMap<String, Weapon> list, int arrayId){
        //load weapons from xml as strings
        Log.i(TAG, "Loading weapon list...");
        Resources res = mAppContext.getResources();
        TypedArray ta = res.obtainTypedArray(arrayId);
        int arrayLength = ta.length();
        String[][] array = new String[arrayLength][];

        //start by loading in whole array
        for(int i = 0; i < arrayLength; i++){
            int id = ta.getResourceId(i, 0);
            if(id > 0){
                array[i] = res.getStringArray(id);
            }else{
                Log.e(TAG, "id " + id + "==0?");
            }
        }
        ta.recycle();

        //parse array into events
        for(int i = 1; i < array.length; i++){
            String id = array[i][0];
            String name = array[i][1];
            int type = Integer.parseInt(array[i][2]);
            double material = Double.parseDouble(array[i][3]);
            Weapon weapon = new Weapon(name, type, material);
            list.put(id, weapon);
        }

        //testing
        for(Weapon w : list.values()){
            Log.i(TAG, w.toString());
        }
    }

    //returns a weapon given an id
    public Weapon getWeaponById(String id){
        for(HashMap<String, Weapon> map : mListList){
            Weapon w = map.get(id);
            if(w != null) return w;
        }
        throw new NoSuchElementException();
    }
}
