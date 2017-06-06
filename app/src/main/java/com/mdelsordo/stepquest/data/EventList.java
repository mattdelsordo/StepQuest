package com.mdelsordo.stepquest.data;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.ActiveCharacter;
import com.mdelsordo.stepquest.model.Event;

/**
 * Created by mdelsord on 4/16/17.
 * Singleton list of monsters from the static list of monsters
 */

public class EventList {

    private static final double[] fiveDistribution = new double[]{0.1, 0.2, 0.4, 0.2, 0.1};
    private static final double[] threeDistribution = new double[]{0.25, 0.5, 0.25};

    private static EventList sEventList;
    private static String TAG = "EventList";

    //private ArrayList<Event> mMonsters;
    //private ArrayList<Event> mBosses;
    private HashMap<Integer, ArrayList<Event>> mClassifiedEventMap;
    private ArrayList<Event> mEvents;
    private Random rand;
    private Context mAppContext;

    private EventList(Context appContext){
        mAppContext = appContext.getApplicationContext();
        //mMonsters = new ArrayList<>();
        //mBosses = new ArrayList<>();
        mEvents = new ArrayList<>();
        rand = new Random();

        //loadEventList(mMonsters, R.array.monsters, true, true, Event.MONSTER);
        //loadEventList(mMonsters, R.array.bosses, true, false, Event.MONSTER);
        loadMonsterList();
        loadEventList(mEvents, R.array.events, false, false, 0);

//        for(Event e : mMonsters){
//            Log.i(TAG, e.getDescription());
//        }
    }

    //returns list instance
    public static EventList getInstance(Context appContext){
        if(sEventList == null) sEventList = new EventList(appContext);
        return sEventList;
    }

    //handles loading the list of all monsters + bosses
    private void loadMonsterList(){
        //get sorted list of all monsters
        ArrayList<Event> allMonsters = new ArrayList<>();
        loadEventList(allMonsters, R.array.monsters, true, true, Event.MONSTER);
        loadEventList(allMonsters, R.array.bosses, true, false, Event.MONSTER);
        Collections.sort(allMonsters, new Event.EventComparator());

        //separate list into chunks
        //Log.i(TAG, "Amount of CRs: " + Event.amountOfCRs());
        int chunksize = allMonsters.size() / Event.amountOfCRs();
        int noOfStragglers = allMonsters.size() % Event.amountOfCRs();
        //Log.i(TAG, "Chunk size: " + chunksize);
        //Log.i(TAG, "Stragglers: " + noOfStragglers);

        //initialize hashmap
        mClassifiedEventMap = new HashMap<>();
        int ratingFloor = Event.RATING_FLOOR;
        //fill with events
        for(int i = 0; i < Event.amountOfCRs(); i++){
           int placement = ratingFloor + i;
            for(int j = 0; j <= chunksize; j++){
                //only do this if there are monsters to sample from
                if(!allMonsters.isEmpty()){
                    ArrayList<Event> eventSublist = mClassifiedEventMap.get(placement);
                    if(eventSublist == null){
                        mClassifiedEventMap.put(placement, new ArrayList<Event>());
                        eventSublist = mClassifiedEventMap.get(placement);
                    }

                    Event monsterToAdd = allMonsters.remove(0);
                    eventSublist.add(monsterToAdd);
                    //Log.i(TAG, "Added " + monsterToAdd.getDescription() + " to bucket " + placement);
                }

            }
        }

        //
    }

    //loads list of monsters from the xml file
    private void loadEventList(ArrayList<Event> list, int arrayId, boolean assign_gold, boolean assign_drop, int tag){
        //load monsters from xml as strings
        //Log.i(TAG, "Loading monster list.");
        Resources res = mAppContext.getResources();
        TypedArray ta = res.obtainTypedArray(arrayId);
        int taLength = ta.length();
        String[][] array = new String[taLength][];
        for(int i = 0; i < taLength; i++){
            int id = ta.getResourceId(i, 0);
            if(id > 0){
                array[i] = res.getStringArray(id);
            }else{
                //Log.e(TAG, "id " + id + "==0?");
            }
        }
        ta.recycle();

        //parse arrays into events
        for(String[] a : array){
            Event monster = new Event(a[0], Integer.parseInt(a[1]));
            if(a.length > 2) monster.setGoldReward(Integer.parseInt(a[2]));
            if(assign_gold) monster.setGoldReward((int)(monster.getDuration() * 0.05) + rand.nextInt(10));
            if(a.length > 3) monster.setItemReward(WeaponList.getInstance(mAppContext).getWeaponById(a[3]));
            else if(assign_drop){
                int chance = rand.nextInt(20);
                if(chance == 6) monster.setItemReward(WeaponList.getInstance(mAppContext).getRandomLevelledWeapon(ActiveCharacter.getInstance(mAppContext).getActiveCharacter().getLevel()));
            }
            monster.setEventClassTag(tag);
            list.add(monster);
        }

        //sort list
        Collections.sort(list, new Event.EventComparator());
    }

    //methods to get a random event of a given type
//    public Event getRandomMonster(){
//        return new Event(mMonsters.get(rand.nextInt(mMonsters.size())));
//    }

//    public Event getRandomBoss(){
//        return new Event(mBosses.get(rand.nextInt(mBosses.size())));
//    }

    public Event getRandomEvent(int level){
        Event selectedEvent = new Event(mEvents.get(rand.nextInt(mEvents.size())));

        int CR = selectLevelFromThree(level - 3);
        int duration = Event.getChallengeRating(CR);
        selectedEvent.setDuration(duration);
        return selectedEvent;
    }

    public Event getChest(int level){
        int CR = selectLevelFromThree(level - 3);
        int duration = Event.getChallengeRating(CR);
        Event chest = new Event("Opening a chest...", duration);
        int contentChance = rand.nextInt(100);
        if(contentChance > 70) chest.setItemReward(WeaponList.getInstance(mAppContext).getRandomLevelledWeapon(ActiveCharacter.getInstance(mAppContext).getActiveCharacter().getLevel() + 1));
        else chest.setGoldReward(rand.nextInt(400));
        return chest;
    }

    //returns a random event of appropriate difficulty for a given character
    public Event getLevelledMonster(int level){
//        int totalMonsters = mMonsters.size();
//        int chunk = totalMonsters / 20;
//
//        int ceiling = (chunk * level) + (2 * chunk);
//        int floor = (chunk * level) - (chunk);
//
//        //constrain so you dont go out of bounds
//        if(floor < 0)floor = 0;
//        if(ceiling > totalMonsters) ceiling = totalMonsters;
//
//        int selection = rand.nextInt(ceiling - floor) + floor;
//
//        return mMonsters.get(selection);
        int selection = selectLevelFromThree(level);
        if(selection > 23) selection = 23;
        ArrayList<Event> selectedBucket = mClassifiedEventMap.get(selection);
        int sampleSpace = selectedBucket.size();
        Event selectedEvent = selectedBucket.get(rand.nextInt(sampleSpace));
        selectedEvent.setDuration(Event.getChallengeRating(selection));
        //Log.i(TAG, "Selected monster " + selectedEvent.getDescription() + " with CR " + selection + " and duration "+ selectedEvent.getDuration());
        return selectedEvent;
    }

    public Event getLevelledBoss(int level){
//        int totalMonsters = mBosses.size();
//        int chunk = totalMonsters / 20;
//
//        int ceiling = (chunk * level) + (2 * chunk);
//        int floor = (chunk * level) - (chunk);
//
//        //constrain so you dont go out of bounds
//        if(floor < 0)floor = 0;
//        if(ceiling > totalMonsters) ceiling = totalMonsters;
//
//        int selection = rand.nextInt(ceiling - floor) + floor;
//
//        return mBosses.get(selection);

        int selection = selectLevelFromThree(level + 4);
        if(selection > 23) selection = 23;
        ArrayList<Event> selectedBucket = mClassifiedEventMap.get(selection);
        int sampleSpace = selectedBucket.size();
        Event selectedEvent = selectedBucket.get(rand.nextInt(sampleSpace));
        selectedEvent.setDuration(Event.getChallengeRating(selection));
        //Log.i(TAG, "Selected boss " + selectedEvent.getDescription() + " with CR " + selection + " and duration "+ selectedEvent.getDuration());
        return selectedEvent;
    }

    //these two methods are used by the levelling algorithms to return a level of event with some probability
    private int selectLevelFromFive(int centerLevel){
        double selection = rand.nextDouble();
        double selectionSpace = 0;
        for(int i = 0; i < fiveDistribution.length; i++){
            selectionSpace += fiveDistribution[i];
            if(selectionSpace < selection){
                //Log.i(TAG, "Leveller selected " + i);
                return centerLevel - 2 + i;
            }
        }
        return centerLevel + 2;
    }

    private int selectLevelFromThree(int centerLevel){
        double selection = rand.nextDouble();
        double selectionSpace = 0;
        for(int i = 0; i < threeDistribution.length; i++){
            selectionSpace += threeDistribution[i];
            if(selectionSpace < selection){
                //Log.i(TAG, "Leveller selected " + i);
                return centerLevel - 1 + i;
            }
        }
        return centerLevel + 1;
    }

}
