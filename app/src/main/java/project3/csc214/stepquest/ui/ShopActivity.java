package project3.csc214.stepquest.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.Saver;
import project3.csc214.stepquest.util.UpdateShopGoldListener;
import project3.csc214.stepquest.model.ActiveCharacter;

/**
 * This activity handles functionality for the shop. User can buy/sell
 * items and buy boosts.
 */
public class ShopActivity extends AppCompatActivity implements UpdateShopGoldListener {

    private BottomNavigationView mBottomNav;
    private TextView mGoldTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        mGoldTotal = (TextView)findViewById(R.id.tv_shop_goldtotal);

        //handle menu navigation
        mBottomNav = (BottomNavigationView)findViewById(R.id.bnv_shop);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.m_shop_weapons:
                        swapFragment(new ShopWeaponFragment());
                        return true;
                    case R.id.m_shop_boosts:
                        swapFragment(new ShopBoostFragment());
                        return true;
                    default: return false;
                }
            }
        });

        if(getSupportFragmentManager().findFragmentById(R.id.fl_shop_mainframe) == null){
            mBottomNav.setSelectedItemId(R.id.m_shop_weapons);
        }

        //update to current gold total
        updateGoldTotal(ActiveCharacter.getInstance(this).getActiveCharacter().getFunds());

    }

    //swaps fragments in the main frame
    public void swapFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_shop_mainframe, fragment).commit();
    }

    @Override
    public void updateGoldTotal(int goldTotal) {
        mGoldTotal.setText(Integer.toString(goldTotal));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Saver.saveAll(this, false);
    }
}
