package project3.csc214.stepquest.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import project3.csc214.stepquest.R;

public class CharacterCreationActivity extends AppCompatActivity implements CharacterCreationFragment.CreationCompleteListener{

    CharacterCreationFragment fragment;

    //stuff for a new intent
    public static final int REQUEST_CHARACTER_INFO = 3;
    public static Intent newInstance(AppCompatActivity a){
        return new Intent(a, CharacterCreationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_creation);

        //put fragment in frame
        fragment = (CharacterCreationFragment)getSupportFragmentManager().findFragmentById(R.id.frame_charactercreation);
        if(fragment == null){
            fragment = new CharacterCreationFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_charactercreation, fragment).commit();
        }
    }

    @Override
    public void creationComplete(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}
