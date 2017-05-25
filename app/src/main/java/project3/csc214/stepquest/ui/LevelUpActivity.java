package project3.csc214.stepquest.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.Saver;
import project3.csc214.stepquest.model.EventQueue;

public class LevelUpActivity extends AppCompatActivity implements LevelUpFragment.LevelUpDoneListener, EventQueue.MakeToastListener{

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
        Saver.saveAll(this.getApplicationContext(), false);
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

    @Override
    public void makeToast(String text, int duration) {

    }

    @Override
    public void playJingle() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventQueue.getInstance(this).bindToastListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventQueue.getInstance(this).unbindToastListener();
    }
}
