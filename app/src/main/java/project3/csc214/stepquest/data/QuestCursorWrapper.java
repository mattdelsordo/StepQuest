package project3.csc214.stepquest.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import project3.csc214.stepquest.model.Event;
import project3.csc214.stepquest.model.Race;
import project3.csc214.stepquest.model.Stats;
import project3.csc214.stepquest.model.Character;
import project3.csc214.stepquest.model.Weapon;

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

        return new CharacterBundle(character, weapon_id);
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
        int progress = getInt(getColumnIndex(QuestDbSchema.EventQueueTable.Params.PROGRESS));

        Event event = new Event(desc, duration);
        if(gold > 0) event.setGoldReward(gold);

        return new EventBundle(order, event, weapon, progress);
    }

    //returns an event from the event queue

    /** Bundles to help move around information **/
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

        public CharacterBundle(Character c, String wid){
            mCharacter = c;
            mWeaponId = wid;
        }
    }

    public static class EventBundle{
        public int order;
        public Event event;
        public String weapon;
        public int progress;

        public EventBundle(int order, Event event, String weapon, int progress) {
            this.order = order;
            this.event = event;
            this.weapon = weapon;
            this.progress = progress;
        }
    }
}
