package project3.csc214.stepquest.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * Created by mdelsord on 4/17/17.
 * Wrapper for a character that loads any previous character from the SQL database/stores it as a singleton
 */

public class ActiveCharacter{
    private static final String TAG = "ActiveCharacter";
    private static ActiveCharacter sActiveCharacter;

    private Character mCharacter;
    private TreeMap<Weapon, Integer> mWeaponSet;
    private Weapon mEquippedWeapon;

    private ActiveCharacter(){
        mWeaponSet = new TreeMap<>(new Weapon.WeaponComparator());
    }

    public void setActiveCharacter(Character c){
        mCharacter = c;
    }

    public Character getActiveCharacter(){
        return mCharacter;
    }

    public static ActiveCharacter getInstance(){
        if(sActiveCharacter == null) sActiveCharacter = new ActiveCharacter();
        return sActiveCharacter;
    }

    //adds a weapon to the inventory
    public void addWeaponToInventory(Weapon w){
        //TODO: error that this process is happening very wrong
        //Log.i(TAG, "Adding " + w);
        Integer i = mWeaponSet.get(w);
        //Log.i(TAG, "There are " + i);
        if(i != null) mWeaponSet.put(w, i + 1);
        else mWeaponSet.put(w, 1);

        //mWeaponSet.put(w, mWeaponSet.get(w) + 1);
        //equip weapon if there's none equipped
        if(mEquippedWeapon == null) mEquippedWeapon = w;

        if(mWeaponListener != null) mWeaponListener.updateList();
    }

    public void setEquippedWeapon(Weapon w){
        mEquippedWeapon = w;
    }

    public Weapon getEquippedWeapon(){
        return mEquippedWeapon;
    }

    public Collection<Weapon> getWeaponInventory(){
//        for(Weapon w : mWeaponSet.keySet()){
//            Log.i("heh", w.toString());
//        }
        return mWeaponSet.keySet();
    }

    public double getExpModifier()
    {
        if(mEquippedWeapon == null) return 1.0;
        else return mEquippedWeapon.getModifier(mCharacter.getVocation());
    }

    public int getWeaponQuantity(Weapon weapon){
        return mWeaponSet.get(weapon);
    }

    public void addFunds(int funds){
        mCharacter.setFunds(mCharacter.getFunds() + funds);
        if(mFundsUpdateListener != null) mFundsUpdateListener.updateFunds(mCharacter.getFunds());
    }
    //listens for gold amount updates
    public interface FundsUpdateListener{
        void updateFunds(int totalFunds);
    }
    private FundsUpdateListener mFundsUpdateListener;
    public void bindFundsUpdater(FundsUpdateListener ful){
        mFundsUpdateListener = ful;
    }
    public void unbindFundsUpdater(){mFundsUpdateListener = null;}

    //listens for weapon updates
    public interface WeaponUpdateListener{
        void updateList();
    }
    private WeaponUpdateListener mWeaponListener;
    public void bindWeaponListener(WeaponUpdateListener wul){mWeaponListener = wul;}
    public void unbindWeaponListener(){mWeaponListener = null;}

}

