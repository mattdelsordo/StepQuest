package com.mdelsordo.stepquest.data;

/**
 * Created by mdelsord on 5/1/17.
 * SQL schema for storing game info
 */

public class QuestDbSchema {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "stepquest_database.db";

    /** Schema for the table containing the player character **/
    public static final class CharacterTable{
        public static final String NAME = "users";

        public static final class Params{
            public static final String NAME = "name";
            public static final String RACE = "race";
            public static final String VOCATION = "class";
            public static final String LEVEL = "level";
            public static final String STR = "str";
            public static final String DEX = "dex";
            public static final String CON = "con";
            public static final String WIS = "wis";
            public static final String INT = "int";
            public static final String CHR = "chr";
            public static final String GOLD = "gold";
            public static final String EXP = "exp";
            public static final String LVL_TOKENS = "tokens";
            public static final String WEAPON_ID = "weapon_id";
            public static final String BOOST = "boost";
        }
    }

    /** Schema for player inventory **/
    public static final class InventoryTable{
        public static final String NAME = "inventory";

        public static final class Params{
            public static final String WEAPON_ID = "weapon_id";
            public static final String QUANTITY = "quantity";
        }
    }

    /** Schema for the event queue **/
    public static final class EventQueueTable{
        public static final String NAME = "event_queue";

        public static final class Params{
            public static final String ORDER = "ordering";
            public static final String DESCRIPTION = "description";
            public static final String DURATION = "duration";
            public static final String GOLD = "gold";
            public static final String WEAPON_ID = "weapon_id";
            public static final String PROGRESS = "progress";
        }
    }

    /** Schema for Adventure Log **/
    public static final class JournalQueueTable{
        public static final String NAME = "journal_table";

        public static final class Params{
            public static final String ORDER = "ordering";
            public static final String TEXT = "text";
            public static final String DATE = "date";
        }
    }

    public static final class StatisticsTable{
        public static final String NAME = "statistics_table";

        public static final class Params{
            public static final String STEPS = "total_steps";
            public static final String MONSTERS = "monsters_slain";
            public static final String GOLD = "total_gold";
            public static final String WEAPONS = "total_weapons";
            public static final String DUNGEONS = "total_dungeons";
        }
    }
}
