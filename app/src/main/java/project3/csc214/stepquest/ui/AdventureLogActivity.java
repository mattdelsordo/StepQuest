package project3.csc214.stepquest.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import project3.csc214.stepquest.R;

public class AdventureLogActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure_log);

        //handle menu navigation
        mBottomNav = (BottomNavigationView)findViewById(R.id.bnv_shop);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.m_log_journal:
                        swapFragment(new JournalFragment());
                        return true;
                    case R.id.m_log_statistics:
                        swapFragment(new StatisticFragment());
                        return true;
                    default: return false;
                }
            }
        });

        if(getSupportFragmentManager().findFragmentById(R.id.fl_log_mainframe) == null){
            mBottomNav.setSelectedItemId(R.id.m_log_journal);
        }
    }

    //swaps fragments in the main frame
    public void swapFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_log_mainframe, fragment).commit();
    }
}
