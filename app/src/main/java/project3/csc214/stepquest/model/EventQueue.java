package project3.csc214.stepquest.model;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mdelsord on 4/15/17.
 * This class loads active events from the sql database and
 * serves one up one after the other.
 */

public class EventQueue {
    private static final String TAG = "EventQueue";

    public static EventQueue sEventQueue;

    private ArrayList<Event> mQueue; //queue of events
    private int mProgress;
    private Context mAppContext;

    private EventQueue(Context context){
        mAppContext = context;
        mQueue = new ArrayList<>();
        mProgress = 0;

        //TODO: would load queue here but otherwise just add a new event
//        addEvents(Dungeon.newRandomDungeon(mAppContext));
//        Log.i(TAG, mQueue.get(0).toString());
    }

    public static EventQueue getInstance(Context context){
        if(sEventQueue == null) sEventQueue = new EventQueue(context);
        return sEventQueue;
    }

    //returns the top event of the queue
    public Event getTopEvent(){
        if(mQueue.isEmpty()) addEvents(Dungeon.newRandomDungeon(mAppContext));
        Event e = mQueue.get(0);
        Log.i(TAG, "Duration " + e.getDuration());
        return e;
    }

    //adds a dungeon's stuff to the queue
    private void addEvents(Dungeon dungeon){
        for(Event e : dungeon) mQueue.add(e);
        //Log.i(TAG, "Queue size: " + mQueue.size());
    }

    //updates the queue based off of a new step reading
    public void updateQueue(int stepsTaken){
        //TODO: connect this to the pedometer in some way

        mProgress += stepsTaken;
        Event currentEvent = getTopEvent();
        if(mProgress >= currentEvent.getDuration()){
            //if sufficient time has passed, update EVERYTHING
            //TODO: implement updating exp, money, new weapons from event
            mQueue.remove(0); //remove the top event
            mProgress -= currentEvent.getDuration(); //subtract elapsed time
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
    }


}
