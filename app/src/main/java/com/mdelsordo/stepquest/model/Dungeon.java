package com.mdelsordo.stepquest.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

import com.mdelsordo.stepquest.data.EventList;

/**
 * Created by mdelsord on 4/15/17.
 * Handles random event generation
 *
 * A dungeon is just a series of events in a prescribed order.
 * A single event is just a one-long list of events
 */

public class Dungeon extends ArrayList<Event> {


    public static Dungeon newRandomDungeon(Context context){

        Random rand = new Random();
        int chance = rand.nextInt(100);

        if(chance < 10) return dungeon(context);
        else if(chance < 50) return killingField(context);
        else if(chance < 70) return village(context);
        else if(chance < 90) return singleEvent(context);
        else return singleMonster(context);
    }

    public static Dungeon singleMonster(Context context){
        Dungeon dungeon = new Dungeon();
        dungeon.add(EventList.getInstance(context).getLevelledMonster(ActiveCharacter.getInstance(context).getActiveCharacter().getLevel()));
        return dungeon;
    }

    public static Dungeon singleEvent(Context context){
        int activeCharacterLevel = ActiveCharacter.getInstance(context).getActiveCharacter().getLevel();
        Dungeon dungeon = new Dungeon();
        dungeon.add(EventList.getInstance(context).getRandomEvent(activeCharacterLevel));
        return dungeon;
    }

    public static Dungeon killingField(Context context){
        Random rand = new Random();
        Dungeon dungeon = new Dungeon();

        dungeon.add(new Event("Heading into the wilderness...", 100));

        int duration = rand.nextInt(9) + 3;
        for(int i = 0; i < duration; i++){
            dungeon.add(EventList.getInstance(context).getLevelledMonster(ActiveCharacter.getInstance(context).getActiveCharacter().getLevel()));
        }

        return dungeon;
    }

    public static Dungeon village(Context context){
        int activeCharacterLevel = ActiveCharacter.getInstance(context).getActiveCharacter().getLevel();
        Random rand = new Random();
        Dungeon dungeon = new Dungeon();

        //TODO: make a town name generator
        dungeon.add(new Event("Approaching a small town...", 100));

        int duration = rand.nextInt(7) + 5;
        for(int i = 0; i < duration; i++){
            dungeon.add(EventList.getInstance(context).getRandomEvent(activeCharacterLevel));
        }

        return dungeon;
    }

    public static Dungeon dungeon(Context context){
        int ActiveCharLevel = ActiveCharacter.getInstance(context).getActiveCharacter().getLevel();
        Random rand = new Random();
        Dungeon dungeon = new Dungeon();

        //TODO: make a dungeon name generator
        dungeon.add(new Event("Entering a dungeon...", 100));

        int duration = rand.nextInt(8) + 7;
        for(int i = 0; i < duration; i++){
            int chance = rand.nextInt(3);
            if(chance > 0)dungeon.add(EventList.getInstance(context).getLevelledMonster(ActiveCharLevel));
            else dungeon.add(EventList.getInstance(context).getChest(ActiveCharLevel));
        }
        Event boss = EventList.getInstance(context).getLevelledBoss(ActiveCharLevel);
        boss.setDoNotify(ActiveCharacter.getInstance(context).getActiveCharacter().getName() + " slew a " + boss.parseMonsterName() + "!");
        dungeon.add(boss);

        Event dungeonTag = new Event(null, 0);
        dungeonTag.setEventClassTag(Event.DUNGEON_CLEAR);
        dungeon.add(dungeonTag);

        return dungeon;
    }

    public static Dungeon generateBackstory(Context context){
        Dungeon backstory = new Dungeon();
        Random rand = new Random();

        backstory.add(new Event("Waking up on your " + (rand.nextInt(10) + 16) + "th birthday...", 6));
        backstory.add(new Event("Greeting your family on this beautiful morning...", 6));
        Event gold = new Event("They give you a small bag of gold as a gift!", 4);
        gold.setGoldReward(133);
        backstory.add(gold);
        backstory.add(new Event("You hear a crash from outside...", 6));
        Event monster = EventList.getInstance(context).getLevelledMonster(ActiveCharacter.getInstance(context).getActiveCharacter().getLevel());
        String monsterName = monster.parseMonsterName();
        backstory.add(new Event("You open the door to see what's going on...", 4));
        backstory.add(new Event("There's a " + monsterName.toLowerCase() + " terrorizing your town!", 6)); //TODO: town name generator
        //Weapon weapon = WeaponList.getInstance(context).firstWeapon(ActiveCharacter.getInstance(context).getActiveCharacter().getVocation());
        //Event getWeapon = new Event("Your father throws you the family's " + weapon.getName().toLowerCase() + "...", 4);
        Event getWeapon = new Event("Your father throws you a sharpened stick!", 4);
        getWeapon.setItemReward(new Weapon());
        backstory.add(getWeapon);
        backstory.add(monster);
        backstory.add(new Event("Your family is very proud...", 4));
        Event leaveTown = new Event("You decide to set out on your own to find the source of the monster...", 10);
        leaveTown.setAdvancePlot(true);
        backstory.add(leaveTown);
        backstory.add(new Event("Leaving town...", 20));

        return backstory;
    }

    public static Dungeon finalBoss(Context context){
        Dungeon finalBoss = new Dungeon();

        Event approach = new Event("Cresting the mountain...", 100);
        approach.setAdvancePlot(true);
        finalBoss.add(approach);

        Event boss = new Event("In combat with the Eagle God...", Event.getChallengeRating(Event.amountOfCRs() - 1) + 10000);
        boss.setAdvancePlot(true);
        boss.setDoNotify(ActiveCharacter.getInstance(context).getActiveCharacter().getName() + " slew the Eagle God!");

        finalBoss.add(boss);
        return finalBoss;
    }
}
