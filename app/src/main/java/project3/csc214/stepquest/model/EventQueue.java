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

    //listener to allow updating of the gui on progress changes
    public interface EventUpdateListener{
        void updateEvent(Event e, int progress);
    }
    private EventUpdateListener mUpdateListener;

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
        return e;
    }

    //removes top event from the queue
    public void popTopEvent(){
        mQueue.remove(0);
    }

    //adds a dungeon's stuff to the queue
    private void addEvents(Dungeon dungeon){
        for(Event e : dungeon) mQueue.add(e);
        //Log.i(TAG, "Queue size: " + mQueue.size());
    }

    //handles incrementing the step on a detected step
    public void incrementProgress(){
        Log.i(TAG, "Step taken (" + mProgress + ")");

        //TODO: implement the rest of this shit but I'm tired af
        mProgress+=10;
        Event currentEvent = getTopEvent();
        if(mProgress >= currentEvent.getDuration()){
            //if the progress threshold has been met:
            //get active character
            Character active = ActiveCharacter.getInstance().getActiveCharacter();
            //give the player exp
            active.addExp(currentEvent.getExp());
            //give the player money
            active.setFunds(active.getFunds() + currentEvent.getGoldReward());
            //give the player any weapon
            //TODO: implement adding a weapon to the weapon table

            //reset progress
            mProgress -= currentEvent.getDuration();
            //remove current event from the queue
            popTopEvent();
        }

        //update the ui if the ui exists
        if(mUpdateListener != null) mUpdateListener.updateEvent(getTopEvent(), mProgress);
        //else make sure to call getTopEvent() anyway so that there is some event in the queue
        else getTopEvent();


    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public void bindUpdateListener(EventUpdateListener eul){
        mUpdateListener = eul;
    }
    public void unbindUpdateListener(){mUpdateListener = null;}

}
