package project3.csc214.stepquest.data;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.Event;
import project3.csc214.stepquest.model.Character;

/**
 * Created by mdelsord on 4/16/17.
 * Singleton list of monsters from the static list of monsters
 */

public class EventList {

    private static EventList sEventList;
    private static String TAG = "EventList";

    private ArrayList<Event> mMonsters;
    private ArrayList<Event> mBosses;
    private ArrayList<Event> mEvents;
    private Random rand;
    private Context mAppContext;

    private EventList(Context appContext){
        mAppContext = appContext.getApplicationContext();
        mMonsters = new ArrayList<>();
        mBosses = new ArrayList<>();
        mEvents = new ArrayList<>();
        rand = new Random();
        loadEventList(mMonsters, R.array.monsters, true, true);
        loadEventList(mBosses, R.array.bosses, true, false);
        loadEventList(mEvents, R.array.events, false, false);

//        for(Event e : mMonsters){
//            Log.i(TAG, e.getDescription());
//        }
    }

    //returns list instance
    public static EventList getInstance(Context appContext){
        if(sEventList == null) sEventList = new EventList(appContext);
        return sEventList;
    }

    //loads list of monsters from the xml file
    private void loadEventList(ArrayList<Event> list, int arrayId, boolean assign_gold, boolean assign_drop){
        //load monsters from xml as strings
        Log.i(TAG, "Loading monster list.");
        Resources res = mAppContext.getResources();
        TypedArray ta = res.obtainTypedArray(arrayId);
        int taLength = ta.length();
        String[][] array = new String[taLength][];
        for(int i = 0; i < taLength; i++){
            int id = ta.getResourceId(i, 0);
            if(id > 0){
                array[i] = res.getStringArray(id);
            }else{
                Log.e(TAG, "id " + id + "==0?");
            }
        }
        ta.recycle();

        //parse arrays into events
        for(String[] a : array){
            Event monster = new Event(a[0], Integer.parseInt(a[1]));
            if(a.length > 2) monster.setGoldReward(Integer.parseInt(a[2]));
            if(assign_gold) monster.setGoldReward((int)(monster.getDuration() * 0.05) + rand.nextInt(100));
            if(a.length > 3) monster.setItemReward(WeaponList.getInstance(mAppContext).getWeaponById(a[3]));
            else if(assign_drop){
                int chance = rand.nextInt(10);
                if(chance == 6) monster.setItemReward(WeaponList.getInstance(mAppContext).getRandomLevelledWeapon(ActiveCharacter.getInstance(mAppContext).getActiveCharacter().getLevel()));
            }
            list.add(monster);
        }

        //sort list
        Collections.sort(list, new Event.EventComparator());
    }

    //methods to get a random event of a given type
    public Event getRandomMonster(){
        return new Event(mMonsters.get(rand.nextInt(mMonsters.size())));
    }

    public Event getRandomBoss(){
        return new Event(mBosses.get(rand.nextInt(mBosses.size())));
    }

    public Event getRandomEvent(){
        return new Event(mEvents.get(rand.nextInt(mEvents.size())));
    }

    public Event getChest(){
        Event chest = new Event("Opening a chest...", 50);
        int contentChance = rand.nextInt(100);
        if(contentChance > 70) chest.setItemReward(WeaponList.getInstance(mAppContext).getRandomLevelledWeapon(ActiveCharacter.getInstance(mAppContext).getActiveCharacter().getLevel() + 1));
        else chest.setGoldReward(rand.nextInt(400));
        return chest;
    }

    //returns a random event of appropriate difficulty for a given character
    public Event getLevelledMonster(int level){
        int totalMonsters = mMonsters.size();
        int chunk = totalMonsters / 20;

        int ceiling = (chunk * level) + (2 * chunk);
        int floor = (chunk * level) - (chunk);

        //constrain so you dont go out of bounds
        if(floor < 0)floor = 0;
        if(ceiling > totalMonsters) ceiling = totalMonsters;

        int selection = rand.nextInt(ceiling - floor) + floor;

        return mMonsters.get(selection);
        //int cap = Character.levelUpFunction(level) /2;
//        Event event = getRandomMonster();
//        while(event.getDuration() > cap) event = getRandomMonster();
//        return event;
    }

    public Event getLevelledBoss(int level){
        int totalMonsters = mBosses.size();
        int chunk = totalMonsters / 20;

        int ceiling = (chunk * level) + (2 * chunk);
        int floor = (chunk * level) - (chunk);

        //constrain so you dont go out of bounds
        if(floor < 0)floor = 0;
        if(ceiling > totalMonsters) ceiling = totalMonsters;

        int selection = rand.nextInt(ceiling - floor) + floor;

        return mBosses.get(selection);
    }

}
