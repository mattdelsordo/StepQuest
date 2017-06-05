package com.mdelsordo.stepquest.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.mdelsordo.stepquest.model.Event;
import com.mdelsordo.stepquest.model.JournalEntry;
import com.mdelsordo.stepquest.model.Stats;
import com.mdelsordo.stepquest.model.Character;

/**
 * Created by mdelsord on 5/1/17.
 * Handles a cursor for the sql database
 */

public class QuestCursorWrapper extends CursorWrapper{

    private static final String TAG = "tag.cursorwrapper";

    public QuestCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    //returns a characterbundle from the database
    public CharacterBundle getCharacter(){
        //get parameters
        String name = getString(getColumnIndex(QuestDbSchema.CharacterTable.Params.NAME));
        int vocation = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.VOCATION));
        int race = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.RACE));
        int level = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.LEVEL));
        int stre = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.STR));
        int dext = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.DEX));
        int cons = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.CON));
        int inte = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.INT));
        int wisd = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.WIS));
        int chri = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.CHR));
        int gold = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.GOLD));
        int exp = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.EXP));
        int tokens = getInt(getColumnIndex(QuestDbSchema.CharacterTable.Params.LVL_TOKENS));
        double boost = getDouble(getColumnIndex(QuestDbSchema.CharacterTable.Params.BOOST));
        String weapon_id = getString(getColumnIndex(QuestDbSchema.CharacterTable.Params.WEAPON_ID));

        int[] stats = new int[Stats.STAT_VOLUME];
        stats[Stats.STR] = stre;
        stats[Stats.DEX] = dext;
        stats[Stats.CON] = cons;
        stats[Stats.INT] = inte;
        stats[Stats.WIS] = wisd;
        stats[Stats.CHR] = chri;

        Character character = new Character(name, level, vocation, race, stats);
        character.setFunds(gold);
        character.setExp(exp);
        character.setLevelUpTokens(tokens);

        return new CharacterBundle(character, weapon_id, boost);
    }

    //returns a weapon from the inventory database
    public WeaponBundle getWeapon(){
        String weapon_id = getString(getColumnIndex(QuestDbSchema.InventoryTable.Params.WEAPON_ID));
        int quantity = getInt(getColumnIndex(QuestDbSchema.InventoryTable.Params.QUANTITY));
        return new WeaponBundle(weapon_id, quantity);
    }

    //returns an event from the event queue database
    public EventBundle getEvent(){
        int order = getInt(getColumnIndex(QuestDbSchema.EventQueueTable.Params.ORDER));
        String desc = getString(getColumnIndex(QuestDbSchema.EventQueueTable.Params.DESCRIPTION));
        int duration = getInt(getColumnIndex(QuestDbSchema.EventQueueTable.Params.DURATION));
        int gold = getInt(getColumnIndex(QuestDbSchema.EventQueueTable.Params.GOLD));
        String weapon = getString(getColumnIndex(QuestDbSchema.EventQueueTable.Params.WEAPON_ID));
        double progress = getDouble(getColumnIndex(QuestDbSchema.EventQueueTable.Params.PROGRESS));

        int notifyInt = getInt(getColumnIndex(QuestDbSchema.EventQueueTable.Params.NOTIFY));
        boolean notify = (notifyInt == 1) ? true : false;
        String notification = getString(getColumnIndex(QuestDbSchema.EventQueueTable.Params.NOTIFICATION_TEXT));
        int advanceInt = getInt(getColumnIndex(QuestDbSchema.EventQueueTable.Params.ADVANCE_PLOT));
        boolean advance = (advanceInt == 1) ? true : false;
        int classTag = getInt(getColumnIndex(QuestDbSchema.EventQueueTable.Params.CLASS_TAG));

        Event event = new Event(desc, duration);
        if(gold > 0) event.setGoldReward(gold);
        event.setEventClassTag(classTag);
        event.setAdvancePlot(advance);
        if(notify){event.setDoNotify(notification);}

        return new EventBundle(order, event, weapon, progress);
    }

    //returns a string from the adventure log table
    public JournalEntry getJournalEntry(){
        String text = getString(getColumnIndex(QuestDbSchema.JournalQueueTable.Params.TEXT));
        String date = getString(getColumnIndex(QuestDbSchema.JournalQueueTable.Params.DATE));
        return new JournalEntry(text, date);
    }

    //get list of statistics
    public StatisticsBundle getStatistics(){
        StatisticsBundle out = new StatisticsBundle();
        out.steps = getInt(getColumnIndex(QuestDbSchema.StatisticsTable.Params.STEPS));
        out.monsters = getInt(getColumnIndex(QuestDbSchema.StatisticsTable.Params.MONSTERS));
        out.gold = getInt(getColumnIndex(QuestDbSchema.StatisticsTable.Params.GOLD));
        out.weapons = getInt(getColumnIndex(QuestDbSchema.StatisticsTable.Params.WEAPONS));
        out.dungeons = getInt(getColumnIndex(QuestDbSchema.StatisticsTable.Params.DUNGEONS));
        return out;
    }

    //get entry for plot queue
    public String getPlotEntry(){
        return getString(getColumnIndex(QuestDbSchema.PlotQueueTable.Params.TEXT));
    }

    /** Bundles to help move around information **/
    public static class StatisticsBundle{
        public int steps;
        public int monsters;
        public int gold;
        public int weapons;
        public int dungeons;
    }

    public static class WeaponBundle{
        public String weapon_id;
        public int quantity;
        public WeaponBundle(String id, int q){
            weapon_id = id;
            quantity = q;
        }
    }

    public static class CharacterBundle {
        public Character mCharacter;
        public String mWeaponId;
        public double mBoost;

        public CharacterBundle(Character c, String wid, double boost){
            mCharacter = c;
            mWeaponId = wid;
            mBoost = boost;
        }
    }

    public static class EventBundle{
        public int order;
        public Event event;
        public String weapon;
        public double progress;

        public EventBundle(int order, Event event, String weapon, double progress) {
            this.order = order;
            this.event = event;
            this.weapon = weapon;
            this.progress = progress;
        }
    }
}
