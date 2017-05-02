package project3.csc214.stepquest.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.WeaponList;
import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.Character;
import project3.csc214.stepquest.model.Dungeon;
import project3.csc214.stepquest.model.EventQueue;
import project3.csc214.stepquest.model.Race;
import project3.csc214.stepquest.model.Vocation;
import project3.csc214.stepquest.model.Weapon;

public class LoadingActivity extends AppCompatActivity {
    public static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //TODO: load all information from database, if it isn't there, make a new game
        Character activeCharacter = ActiveCharacter.getInstance().getActiveCharacter();
        if(activeCharacter == null){
            Log.i(TAG, "No character found, creating new one.");
            startActivityForResult(CharacterCreationActivity.newInstance(this), CharacterCreationActivity.REQUEST_CHARACTER_INFO);
        }
        else startActivityForResult(new Intent(this, MainActivity.class), MainActivity.RC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CharacterCreationActivity.REQUEST_CHARACTER_INFO && resultCode == RESULT_OK){

            String name = data.getStringExtra(CharacterCreationFragment.ARG_NAME);
            int race = data.getIntExtra(CharacterCreationFragment.ARG_RACE, Race.BIRDPERSON);
            int vocation = data.getIntExtra(CharacterCreationFragment.ARG_CLASS, Vocation.FIGHTER);
            int[] stats = data.getIntArrayExtra(CharacterCreationFragment.ARG_STATS);

            Race bigRace = Race.newRace(race);
            Vocation bigVocation = Vocation.newVocation(vocation);

            Character newGuy = new Character(name, bigVocation, bigRace, stats);
            ActiveCharacter.getInstance().setActiveCharacter(newGuy);

            EventQueue.getInstance(this).addEvents(Dungeon.generateBackstory(this));

            //start main activity
            startActivityForResult(new Intent(this, MainActivity.class), MainActivity.RC);

        }
        else finish();
    }
}
