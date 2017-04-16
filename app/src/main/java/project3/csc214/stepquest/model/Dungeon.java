package project3.csc214.stepquest.model;

import java.util.ArrayList;

/**
 * Created by mdelsord on 4/15/17.
 * A dungeon is just a series of events in a prescribed order.
 * A single event is just a one-long list of events
 */

public class Dungeon extends ArrayList<Event> {

    public static Dungeon newRandomDungeon(){
        Dungeon dungeon = new Dungeon();


        //TODO: do shit in here
        //For now, just add one event
        dungeon.add(Event.randomEvent());


        return dungeon;
    }
}
