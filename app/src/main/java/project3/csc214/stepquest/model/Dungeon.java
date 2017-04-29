package project3.csc214.stepquest.model;

import android.content.Context;

import java.util.ArrayList;

import project3.csc214.stepquest.data.EventList;

/**
 * Created by mdelsord on 4/15/17.
 * Handles random event generation
 *
 * A dungeon is just a series of events in a prescribed order.
 * A single event is just a one-long list of events
 */

public class Dungeon extends ArrayList<Event> {

    public static Dungeon newRandomDungeon(Context context){
        Dungeon dungeon = new Dungeon();

        //TODO: do shit in here
        //For now, just add one event
        dungeon.add(EventList.getInstance(context).getRandomEvent());

        return dungeon;
    }
}
