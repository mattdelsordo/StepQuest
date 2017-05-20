package project3.csc214.stepquest.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import project3.csc214.stepquest.data.QuestCursorWrapper;
import project3.csc214.stepquest.data.QuestDatabaseHelper;
import project3.csc214.stepquest.data.QuestDbSchema;
import project3.csc214.stepquest.data.WeaponList;
import project3.csc214.stepquest.util.BoostOverListener;

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
    private Double mActiveBoost;
    private Context mAppContext;
    private final SQLiteDatabase mDatabase;

    private ActiveCharacter(Context appContext){
        mAppContext = appContext.getApplicationContext();
        mWeaponSet = new TreeMap<>(new Weapon.WeaponComparator());
        mDatabase = new QuestDatabaseHelper(mAppContext).getWritableDatabase();

        load();
    }

    public void setActiveCharacter(Character c){
        mCharacter = c;
    }

    public Character getActiveCharacter(){
        return mCharacter;
    }

    public static synchronized ActiveCharacter getInstance(Context appContext){
        if(sActiveCharacter == null) sActiveCharacter = new ActiveCharacter(appContext);
        return sActiveCharacter;
    }

    //adds a weapon to the inventory
    public void addWeaponToInventory(Weapon w){
        //Log.i(TAG, "Adding " + w);
        Integer i = mWeaponSet.get(w);
        //Log.i(TAG, "There are " + i);
        if(i != null) mWeaponSet.put(w, i + 1);
        else mWeaponSet.put(w, 1);

        //mWeaponSet.put(w, mWeaponSet.get(w) + 1);
        //equip weapon if there's none equipped
        if(mEquippedWeapon == null) mEquippedWeapon = w;

        if(mWeaponListener != null) mWeaponListener.updateList();

        //increment number of weapons acquired ever
        AdventureLog.getInstance(mAppContext).addTotalWeaponsAcquired();
    }

    //adds exp to the character
    public void addExp(int exp, EventQueue.NotificationListener listener){
        boolean levelledUp = mCharacter.addExp(exp);
        if(mExpListener != null) mExpListener.updateExpProgress(mCharacter);

        if(levelledUp){
            if(mLevelUpListener != null) mLevelUpListener.doLevelUp();
            //else, notify user because this means the activity is dead
            else if(listener != null) listener.notifyUser(mCharacter.getName() + " grew to level " + mCharacter.getLevel());
        }
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
        AdventureLog.getInstance(mAppContext).addTotalGoldAcquired(funds);
    }

    //boost methods
    public void setBoost(Boost b){mActiveBoost = b.getStepMultiplier();}
    public void removeBoost(){
        mActiveBoost = null;
        if(mBoostOver != null)mBoostOver.boostOver();
    }
    public double getBoostMultiplier(){
        if(mActiveBoost != null) return mActiveBoost;
        else return 1.0;
    }

    /** WELCOME TO THE INTERFACE ZONE **/
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

    //listens for exp updates
    public interface ExpUpdateListener{
        void updateExpProgress(Character c);
    }
    private ExpUpdateListener mExpListener;
    public void bindExpListener(ExpUpdateListener eul){mExpListener = eul;}
    public void unbindExpListener(){mExpListener = null;}

    //listens for levelups
    public interface LevelUpListener{
        void doLevelUp();
    }
    public LevelUpListener mLevelUpListener;
    public void bindLevelUpListener(LevelUpListener lul){mLevelUpListener = lul;}
    public void unbindLevelUpListener(){mLevelUpListener = null;}

    private BoostOverListener mBoostOver;
    public void bindBoostOverListener(BoostOverListener bol){mBoostOver = bol;}
    public void unbindBoostOVerListener(){mBoostOver = null;}


    /** methods to load and save the active character **/
    private QuestCursorWrapper queryCharacters(String where, String args[]){
        Cursor cursor = mDatabase.query(
                QuestDbSchema.CharacterTable.NAME,
                null,
                where,
                args,
                null,
                null,
                null
        );
        return new QuestCursorWrapper(cursor);
    }

    private QuestCursorWrapper queryInventory(String where, String args[]){
        Cursor cursor = mDatabase.query(
                QuestDbSchema.InventoryTable.NAME,
                null,
                where,
                args,
                null,
                null,
                null
        );
        return new QuestCursorWrapper(cursor);
    }

    //loads the active character and the inventory
    public void load(){
        mWeaponSet.clear();

        //get character and weapon
        QuestCursorWrapper charWrapper = queryCharacters(null, null);
        try{
            if(charWrapper.getCount() == 1){
                charWrapper.moveToFirst();
                QuestCursorWrapper.CharacterBundle bundle = charWrapper.getCharacter();
                setActiveCharacter(bundle.mCharacter);
                setEquippedWeapon(WeaponList.getInstance(mAppContext).getWeaponById(bundle.mWeaponId));
                mActiveBoost = bundle.mBoost;
            }else if(charWrapper.getCount() > 1){
                Log.e(TAG, "ERROR: there's two characters in the database??");
            }

        }finally{
            charWrapper.close();
        }

        //get inventory
        QuestCursorWrapper invWrapper = queryInventory(null, null);
        try{
            if(invWrapper.getCount() > 0){
                invWrapper.moveToFirst();
                while(invWrapper.isAfterLast() == false){
                    QuestCursorWrapper.WeaponBundle bundle = invWrapper.getWeapon();
                    Weapon w = WeaponList.getInstance(mAppContext).getWeaponById(bundle.weapon_id);
                    mWeaponSet.put(w, bundle.quantity);

                    invWrapper.moveToNext();
                }
            }
        }finally{
            invWrapper.close();
        }
    }

    private ContentValues getCharacterContentValues(){
        ContentValues values = new ContentValues();
        values.put(QuestDbSchema.CharacterTable.Params.NAME, getActiveCharacter().getName());
        values.put(QuestDbSchema.CharacterTable.Params.RACE, getActiveCharacter().getRace().getId());
        values.put(QuestDbSchema.CharacterTable.Params.VOCATION, getActiveCharacter().getVocation().getId());
        values.put(QuestDbSchema.CharacterTable.Params.LEVEL, getActiveCharacter().getLevel());

        //stats
        //Log.i(TAG, "Saving stats: " + getActiveCharacter().get)
        int[] stats = getActiveCharacter().getBaseStats();
        values.put(QuestDbSchema.CharacterTable.Params.STR, stats[Stats.STR]);
        values.put(QuestDbSchema.CharacterTable.Params.DEX, stats[Stats.DEX]);
        values.put(QuestDbSchema.CharacterTable.Params.CON, stats[Stats.CON]);
        values.put(QuestDbSchema.CharacterTable.Params.INT, stats[Stats.INT]);
        values.put(QuestDbSchema.CharacterTable.Params.WIS, stats[Stats.WIS]);
        values.put(QuestDbSchema.CharacterTable.Params.CHR, stats[Stats.CHR]);

        values.put(QuestDbSchema.CharacterTable.Params.GOLD, getActiveCharacter().getFunds());
        values.put(QuestDbSchema.CharacterTable.Params.EXP, getActiveCharacter().getExp());
        values.put(QuestDbSchema.CharacterTable.Params.LVL_TOKENS, getActiveCharacter().getLvlUpTokenAmnt());

        if(mActiveBoost != null)values.put(QuestDbSchema.CharacterTable.Params.BOOST, mActiveBoost);
        else values.put(QuestDbSchema.CharacterTable.Params.BOOST, 1.0);


        if(mEquippedWeapon != null) values.put(QuestDbSchema.CharacterTable.Params.WEAPON_ID, mEquippedWeapon.getId());
        else values.put(QuestDbSchema.CharacterTable.Params.WEAPON_ID, "");

        return values;
    }

    private ContentValues getWeaponContentValues(Map.Entry<Weapon, Integer> entry){
        ContentValues values = new ContentValues();
        values.put(QuestDbSchema.InventoryTable.Params.WEAPON_ID, entry.getKey().getId());
        values.put(QuestDbSchema.InventoryTable.Params.QUANTITY, entry.getValue());
        return values;
    }

    //save all the stuff from this class at once
    //TODO: if this takes too long put it in an asynctask idk
    public void save(){
        mDatabase.delete(QuestDbSchema.CharacterTable.NAME, null, null);
        mDatabase.insert(QuestDbSchema.CharacterTable.NAME, null, getCharacterContentValues());

        mDatabase.delete(QuestDbSchema.InventoryTable.NAME, null, null);
        for(Map.Entry<Weapon, Integer> entry : mWeaponSet.entrySet()){
            mDatabase.insert(QuestDbSchema.InventoryTable.NAME, null, getWeaponContentValues(entry));
        }
    }
}

