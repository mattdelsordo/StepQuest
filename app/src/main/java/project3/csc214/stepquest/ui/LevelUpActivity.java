package project3.csc214.stepquest.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.Saver;

public class LevelUpActivity extends AppCompatActivity implements LevelUpFragment.LevelUpDoneListener{

    public static boolean sIsRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);

        setTitle(getString(R.string.you_levelled_up_allocate_your_stat_points));

        //put fragment in thing
        LevelUpFragment frag = (LevelUpFragment)getSupportFragmentManager().findFragmentById(R.id.frame_levelupfragment);
        if(frag == null){
            frag = new LevelUpFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_levelupfragment, frag).commit();
        }
    }

    @Override
    public void lvlUpDone() {
        Saver.saveAll(this.getApplicationContext(), true);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sIsRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        sIsRunning = false;
    }
}
