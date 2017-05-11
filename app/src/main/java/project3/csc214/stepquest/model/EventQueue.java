package project3.csc214.stepquest.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import project3.csc214.stepquest.data.QuestCursorWrapper;
import project3.csc214.stepquest.data.QuestDatabaseHelper;
import project3.csc214.stepquest.data.QuestDbSchema;
import project3.csc214.stepquest.data.WeaponList;

/**
 * Created by mdelsord on 4/15/17.
 * This class loads active events from the sql database and
 * serves one up one after the other.
 */

public class EventQueue {
    private static final String TAG = "EventQueue";

    public static EventQueue sEventQueue;

    private ArrayList<Event> mQueue; //queue of events
    private double mProgress;
    private Context mAppContext;
    private SQLiteDatabase mDatabase;

    //listener to allow updating of the gui on progress changes
    public interface EventUpdateListener{
        void updateEvent(Event e, int progress);
    }
    private EventUpdateListener mUpdateListener;

    public interface MakeToastListener{
        void makeToast(String text, int duration);
        void playJingle();
    }
    private MakeToastListener mToastListener;

    public interface NotificationListener{
        void notifyUser(String message);
    }
    private NotificationListener mNotificationListener;
    public void bindNotificationListener(NotificationListener nl){
        mNotificationListener = nl;
    }
    public void unbindNotificationListener(){mNotificationListener = null;}

    private EventQueue(Context context){
        mAppContext = context.getApplicationContext();
        mQueue = new ArrayList<>();
        mProgress = 0;
        mDatabase = new QuestDatabaseHelper(mAppContext).getWritableDatabase();

        load();
    }

    public static synchronized EventQueue getInstance(Context context){
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
    public void addEvents(Dungeon dungeon){
        for(Event e : dungeon) mQueue.add(e);
        //Log.i(TAG, "Queue size: " + mQueue.size());
    }

    //handles incrementing the step on a detected step
    public void incrementProgress(NotificationListener listener){
        //Log.i(TAG, "Step taken (" + mProgress + ")");

        mProgress += oneStepValue();
        Event currentEvent = getTopEvent();
        if(mProgress >= currentEvent.getDuration()){
            //if the progress threshold has been met:
            //get active character
//            Character active = ActiveCharacter.getInstance().getActiveCharacter();
            //give the player exp
            int expGain = currentEvent.getExp();
            ActiveCharacter.getInstance(mAppContext).addExp(expGain);
            //give the player money
            int fundReward = currentEvent.getGoldReward();
            ActiveCharacter.getInstance(mAppContext).addFunds(fundReward);
            //give the player any weapon
            Weapon weaponReward = currentEvent.getItemReward();
            if(weaponReward != null) ActiveCharacter.getInstance(mAppContext).addWeaponToInventory(weaponReward);

            //notify player of event completion
            if(mToastListener != null){
                mToastListener.playJingle();
                mToastListener.makeToast("Task complete! +" + expGain + " exp!", Toast.LENGTH_SHORT);
                if(fundReward != 0) mToastListener.makeToast("You recieved " + fundReward + " gold!", Toast.LENGTH_SHORT);
                if(weaponReward != null) mToastListener.makeToast("You recieved a " + weaponReward.getName() + "!!", Toast.LENGTH_SHORT);
            }else if(listener != null){
                //check whether this event was important enough to notify about
                if(currentEvent.doNotify()){
                    listener.notifyUser(currentEvent.getNotificationText());
                }
                else if(weaponReward != null){
                    //else notify if the player found a weapon
                    listener.notifyUser(ActiveCharacter.getInstance(mAppContext).getActiveCharacter().getName() + " found a " + weaponReward.getName() + "!");
                }

            }
            Log.i(TAG, "Task complete +" + expGain);

            //reset progress
            mProgress -= currentEvent.getDuration();
            //remove current event from the queue
            popTopEvent();
        }

        //update the ui if the ui exists
        if(mUpdateListener != null) mUpdateListener.updateEvent(getTopEvent(), (int)mProgress);
        //else make sure to call getTopEvent() anyway so that there is some event in the queue
        else getTopEvent();
    }

    private double oneStepValue(){
        double step = 1.0;
        if(ActiveCharacter.getInstance(mAppContext).getExpModifier() > 0) step *= ActiveCharacter.getInstance(mAppContext).getExpModifier();
        return step;
    }

    public double getProgress() {
        return mProgress;
    }
    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public void bindUpdateListener(EventUpdateListener eul){
        mUpdateListener = eul;
    }
    public void unbindUpdateListener(){mUpdateListener = null;}

    public void bindToastListener(MakeToastListener tl){mToastListener = tl;}
    public void unbindToastListener(){mToastListener = null;};

    /** Database methods **/
    private QuestCursorWrapper queryEventQueue(String where, String args[]){
        Cursor cursor = mDatabase.query(
                QuestDbSchema.EventQueueTable.NAME,
                null,
                where,
                args,
                null,
                null,
                QuestDbSchema.EventQueueTable.Params.ORDER
        );
        return new QuestCursorWrapper(cursor);
    }

    private void load(){
        Log.i(TAG, "Loading event queue...");
        mQueue.clear();

        QuestCursorWrapper wrapper = queryEventQueue(null, null);
        try{
            if(wrapper.getCount() > 0){
                wrapper.moveToFirst();
                while(wrapper.isAfterLast() == false){
                    QuestCursorWrapper.EventBundle bundle = wrapper.getEvent();
                    Event e = bundle.event;
                    String weapon = bundle.weapon;
                    if(weapon.length() > 0) e.setItemReward(WeaponList.getInstance(mAppContext).getWeaponById(weapon));
                    Log.i(TAG, "Loading event " + e.getDescription());
                    mQueue.add(e);
                    mProgress = bundle.progress;

                    wrapper.moveToNext();
                }

            }
        }finally {
            wrapper.close();
        }
    }

    private ContentValues getEventContentValues(Event event, int order){
        ContentValues values = new ContentValues();
        values.put(QuestDbSchema.EventQueueTable.Params.ORDER, order);
        values.put(QuestDbSchema.EventQueueTable.Params.DESCRIPTION, event.getDescription());
        values.put(QuestDbSchema.EventQueueTable.Params.DURATION, event.getDuration());
        values.put(QuestDbSchema.EventQueueTable.Params.GOLD, event.getGoldReward());

        if(event.getItemReward() != null) values.put(QuestDbSchema.EventQueueTable.Params.WEAPON_ID, event.getItemReward().getId());
        else values.put(QuestDbSchema.EventQueueTable.Params.WEAPON_ID, "");

        values.put(QuestDbSchema.EventQueueTable.Params.PROGRESS, mProgress);

        return values;
    }

    //TODO: again, might want to put this in an asynctask
    public void save(){
        Log.i(TAG, "Saving EventQueue");
        mDatabase.delete(QuestDbSchema.EventQueueTable.NAME, null, null);
        for(int i = 0; i < mQueue.size(); i++){
            Event e = mQueue.get(i);
            Log.i(TAG, "Saving " + e.getDescription() + " at position " + i);
            mDatabase.insert(QuestDbSchema.EventQueueTable.NAME, null, getEventContentValues(mQueue.get(i), i));
        }
    }
}
