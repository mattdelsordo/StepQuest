package project3.csc214.stepquest.model;

import java.util.ArrayList;

/**
 * Created by mdelsord on 4/15/17.
 * This class loads active events from the sql database and
 * serves one up one after the other.
 */

public class EventQueue {

    public static EventQueue sEventQueue;

    private ArrayList<Event> mQueue; //queue of events
    private int mElapsedTime;

    private EventQueue(){
        mQueue = new ArrayList<>();
        mElapsedTime = 0;
    }

    public EventQueue getInstance(){
        if(sEventQueue == null) sEventQueue = new EventQueue();
        return sEventQueue;
    }

    //returns the top event of the queue
    public Event getTopEvent(){
        Event topEvent = mQueue.get(0);
        if(topEvent == null) addEvents(Dungeon.newRandomDungeon());
        return topEvent;
    }

    //adds a dungeon's stuff to the queue
    private void addEvents(Dungeon dungeon){
        for(Event e : dungeon) mQueue.add(e);
    }

    //updates the queue based off of a new step reading
    public void updateQueue(int stepsTaken){
        //TODO: connect this to the pedometer in some way

        mElapsedTime += stepsTaken;
        Event currentEvent = getTopEvent();
        if(mElapsedTime >= currentEvent.getDuration()){
            //if sufficient time has passed, update EVERYTHING
            //TODO: implement updating exp, money, new weapons from event
            mQueue.remove(0); //remove the top event
        }
    }
}
