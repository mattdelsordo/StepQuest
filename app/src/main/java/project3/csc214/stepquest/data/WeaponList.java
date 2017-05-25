package project3.csc214.stepquest.data;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.Vocation;
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
    private LinkedHashMap<String, Weapon> mMisc;
    private LinkedHashMap<String, Weapon> mWood;
    private LinkedHashMap<String, Weapon> mBronze;
    private LinkedHashMap<String, Weapon> mIron;
    private LinkedHashMap<String, Weapon> mSteel;
    private LinkedHashMap<String, Weapon> mObsidian;
    private LinkedHashMap<String, Weapon> mMithril;
    private LinkedHashMap<String, Weapon> mLegendary;
    private ArrayList<LinkedHashMap<String, Weapon>> mListList;

    private Context mAppContext;

    private WeaponList(Context appContext){
        mAppContext = appContext.getApplicationContext();

        //create lists
        mWood = new LinkedHashMap<>();
        mBronze = new LinkedHashMap<>();
        mIron = new LinkedHashMap<>();
        mSteel = new LinkedHashMap<>();
        mObsidian = new LinkedHashMap<>();
        mMithril = new LinkedHashMap<>();
        mLegendary = new LinkedHashMap<>();
        mMisc = new LinkedHashMap<>();

        //put misc weapons in misc
        Weapon stick = new Weapon();
        mMisc.put(stick.getId(), stick);
        //fill lists
        fillGenericWeaponList(mWood, R.array.wood);
        fillGenericWeaponList(mBronze, R.array.bronze);
        fillGenericWeaponList(mIron, R.array.iron);
        fillGenericWeaponList(mSteel, R.array.steel);
        fillGenericWeaponList(mObsidian, R.array.obsidian);
        fillGenericWeaponList(mMithril, R.array.mithril);
        //fill legendary list
        fillLegendaryWeaponList(mLegendary, R.array.legendary_weapons);

        //make list of lists to search through
        mListList = new ArrayList<>();
        mListList.add(mMisc);
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
    private void fillGenericWeaponList(LinkedHashMap<String, Weapon> list, int arrayId){
        //load weapons from xml as strings
        Log.i(TAG, "Loading weapon list...");
        Resources res = mAppContext.getResources();
        TypedArray ta = res.obtainTypedArray(arrayId);
        int arrayLength = ta.length();
        String[][] array = new String[arrayLength][];

        //start by loading in whole array
        for(int i = 0; i < arrayLength; i++){
            int id = ta.getResourceId(i, 0);
            array[i] = res.getStringArray(id);
            //Log.i(TAG, array[i][0]);
//            if(id > 0){
//                array[i] = res.getStringArray(id);
//            }else{
//                Log.e(TAG, "id " + id + "==0?");
//            }
        }
        ta.recycle();

        //parse array into events
        //Log.i(TAG, array[0][0]);
        double material = Weapon.parseMaterial(array[0][0]); //first element is the classification
        for(int i = 1; i < array.length; i++){
            String id = array[i][0];
            String name = array[i][1];
            int type = Weapon.parseType(array[i][2]);
            Weapon weapon = new Weapon(id, name, type, material);
            list.put(id, weapon);
        }

        //testing
        for(Weapon w : list.values()){
            Log.i(TAG, w.toString());
        }
    }

    private void fillLegendaryWeaponList(HashMap<String, Weapon> list, int arrayId){
        //load weapons from xml as strings
        Log.i(TAG, "Loading legendary weapon list...");
        Resources res = mAppContext.getResources();
        TypedArray ta = res.obtainTypedArray(arrayId);
        int arrayLength = ta.length();
        String[][] array = new String[arrayLength][];

        //start by loading in whole array
        for(int i = 0; i < arrayLength; i++){
            int id = ta.getResourceId(i, 0);
            array[i] = res.getStringArray(id);
//            if(id > 0){
//                array[i] = res.getStringArray(id);
//            }else{
//                Log.e(TAG, "id " + id + "==0?");
//            }
        }
        ta.recycle();

        //parse array into events
        for(int i = 0; i < array.length; i++){
            String id = array[i][0];
            String name = array[i][1];
            int type = Weapon.parseType(array[i][2]);
            double material = Double.parseDouble(array[i][3]);
            Weapon weapon = new Weapon(id, name, type, material);
            list.put(id, weapon);
        }

        //testing
        for(Weapon w : list.values()){
            Log.i(TAG, w.toString());
        }
    }

    //returns a weapon given an id
    public Weapon getWeaponById(String id){
        Log.i(TAG, "Searching for " + id);
        if(id.equals("")) return null;
        for(LinkedHashMap<String, Weapon> map : mListList){
            Weapon w = map.get(id);
            if(w != null){
                Log.i(TAG, "got " + w.toString());
                return new Weapon(w);
            }
        }
        Log.e(TAG, "NO SUCH ELEMENT: " + id);
        throw new NoSuchElementException();
    }

    //returns a random weapon based on the character's level
    public Weapon getRandomLevelledWeapon(int charLevel){
        Random rand = new Random();
        ArrayList<Weapon> list = getLevelledWeaponList(charLevel);
        return list.get(rand.nextInt(list.size()));
    }


    public Weapon firstWeapon(Vocation v){
        switch(v.getGoodWeapon()){
            case(Weapon.BLADE): return new Weapon(WeaponList.getInstance(mAppContext).getWeaponById("wood_sword"));
            case(Weapon.BOW): return new Weapon(WeaponList.getInstance(mAppContext).getWeaponById("wood_bow"));
            case(Weapon.STAFF): return new Weapon(WeaponList.getInstance(mAppContext).getWeaponById("wood_staff"));
            case(Weapon.BLUNT): return new Weapon(WeaponList.getInstance(mAppContext).getWeaponById("wood_club"));
            default: return new Weapon();
        }
    }

    //returns the collection of all weapons the player is allowed to obtain
    //TODO: maybe encode these level caps for weapons somewhere
    public ArrayList<Weapon> getLevelledWeaponList(int charLevel){
        ArrayList levelledWeapons = new ArrayList<>();
        levelledWeapons.addAll(mWood.values());
        if(charLevel >= 2) levelledWeapons.addAll(mBronze.values());
        if(charLevel >= 5) levelledWeapons.addAll(mIron.values());
        if(charLevel >= 8) levelledWeapons.addAll(mSteel.values());
        if(charLevel >= 11) levelledWeapons.addAll(mObsidian.values());
        if(charLevel >= 15) levelledWeapons.addAll(mMithril.values());

        Collections.sort(levelledWeapons, new Weapon.WeaponComparator());

        return levelledWeapons;
    }
}
