package project3.csc214.stepquest.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import project3.csc214.stepquest.R;

/**
 * This activity handles functionality for the shop. User can buy/sell
 * items and buy boosts.
 */
public class ShopActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //handle menu navigation
        mBottomNav = (BottomNavigationView)findViewById(R.id.bnv_shop);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.m_shop_weapons:
                        swapFragmnent(new ShopWeaponFragment());
                        return true;
                    case R.id.m_shop_boosts:
                        return true;
                    default: return false;
                }
            }
        });

        if(getSupportFragmentManager().findFragmentById(R.id.fl_shop_mainframe) == null){
            mBottomNav.setSelectedItemId(R.id.m_shop_weapons);
        }

    }

    //swaps fragments in the main frame
    public void swapFragmnent(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_shop_mainframe, fragment).commit();
    }
}
