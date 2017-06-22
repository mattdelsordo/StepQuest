package com.mdelsordo.stepquest.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.data.WeaponList;
import com.mdelsordo.stepquest.model.Character;
import com.mdelsordo.stepquest.model.EffectPlayer;
import com.mdelsordo.stepquest.util.CenteredItemDecoration;
import com.mdelsordo.stepquest.util.ShopFragmentListener;
import com.mdelsordo.stepquest.model.ActiveCharacter;
import com.mdelsordo.stepquest.model.Weapon;
import com.mdelsordo.stepquest.util.PurchaseDialog;

/**
 * RecyclerView handler that displays weapons that the player can purchase.
 */
public class ShopWeaponFragment extends Fragment {


    public ShopWeaponFragment() {
        // Required empty public constructor
    }

    private static final int COLUMN_COUNT = 2, COLUMN_SPACING = 30;
    private RecyclerView mRecycler;
    private ShopFragmentListener mGoldListener;
    private Weapon mQueuedWeapon;
    private CheckBox mEquipYN;
    private static final String PREF_EQUIP_ON_BUY = "pref_equip_on_buy";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_weapon, container, false);

        mEquipYN = (CheckBox)view.findViewById(R.id.cb_shop_equip);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEquipYN.setChecked(prefs.getBoolean(PREF_EQUIP_ON_BUY, true));
        mEquipYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putBoolean(PREF_EQUIP_ON_BUY, isChecked).apply();
            }
        });

        mRecycler = (RecyclerView)view.findViewById(R.id.rv_shop_weapons);
        //GridLayoutManager layout = new GridLayoutManager(getActivity(), COLUMN_COUNT);
        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN_COUNT));

        mRecycler.addItemDecoration(new CenteredItemDecoration(COLUMN_COUNT, COLUMN_SPACING));
        refreshList();

        return view;
    }

    public void refreshList(){
        ShopWeaponAdapter refresh = new ShopWeaponAdapter(WeaponList.getInstance(getContext()).getLevelledWeaponList(ActiveCharacter.getInstance(getContext()).getActiveCharacter().getLevel()));
        mRecycler.setAdapter(refresh);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGoldListener = (ShopFragmentListener)context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PurchaseDialog.REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                mGoldListener.playEffect(EffectPlayer.ANVIL);

                //add weapon to player inventory
                ActiveCharacter active = ActiveCharacter.getInstance(getContext());
                Weapon bought = new Weapon(mQueuedWeapon);
                active.addWeaponToInventory(bought);
                if(mEquipYN.isChecked())active.setEquippedWeapon(bought);
                //subtract gold
                int newGoldAmnt = active.getActiveCharacter().getFunds() - mQueuedWeapon.getPrice(active.getActiveCharacter());
                active.getActiveCharacter().setFunds(newGoldAmnt);
                mGoldListener.updateGoldTotal(newGoldAmnt);
                refreshList();
            }else{
                mQueuedWeapon = null;
            }
        }
        else super.onActivityResult(requestCode, resultCode, data);
    }

    //viewholder that holds the views for the weapons
    private class ShopWeaponViewHolder extends RecyclerView.ViewHolder{

        private Weapon mWeapon;
        private ImageView mWeaponIcon;
        private TextView mName, mPrice, mBuff;
        private Button mPurchase;
        public View mView;
        private Character mActive;

        public ShopWeaponViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mWeaponIcon = (ImageView)itemView.findViewById(R.id.iv_shop_weaponicon);
            mName = (TextView)itemView.findViewById(R.id.tv_shop_weaponname);
            mPrice = (TextView)itemView.findViewById(R.id.tv_shop_weaponprice);
            mBuff = (TextView)itemView.findViewById(R.id.tv_shop_weaponbuff);
            mPurchase = (Button)itemView.findViewById(R.id.b_shop_purchase);
            mActive = ActiveCharacter.getInstance(getContext()).getActiveCharacter();

            mPurchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mQueuedWeapon = mWeapon;
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    PurchaseDialog dialog = PurchaseDialog.newInstance(mQueuedWeapon.getName(), mQueuedWeapon.getPrice(mActive));
                    dialog.setTargetFragment(ShopWeaponFragment.this, PurchaseDialog.REQUEST_CODE);
                    dialog.show(manager, "DialogPurchase");
                    mGoldListener.playEffect(EffectPlayer.DIALOG);
                }
            });
        }

        public void bindWeapon(Weapon weapon){
            mWeapon = weapon;
            mName.setText(weapon.getName());
            double buff = mWeapon.calcBuff(ActiveCharacter.getInstance(getContext()).getActiveCharacter());
            mBuff.setText((buff > 0.0 ? "+" : "") + buff + "%");
            mWeaponIcon.setImageResource(mWeapon.getDrawable());
            mPrice.setText(Integer.toString(mWeapon.getPrice(mActive)));

            //disable button if there isn't enough money
            if(ActiveCharacter.getInstance(getContext()).getActiveCharacter().getFunds() < mWeapon.getPrice(mActive)) mPurchase.setEnabled(false);
        }
    }

    //adapter for list of weapons
    private class ShopWeaponAdapter extends RecyclerView.Adapter<ShopWeaponViewHolder>{
        private ArrayList<Weapon> mWeapons;
        private int lastPosition = -1;

        public ShopWeaponAdapter(Collection<Weapon> weapons){
            mWeapons = new ArrayList<>(weapons);
            Collections.sort(mWeapons, new Weapon.WeaponComparator());
        }

        @Override
        public ShopWeaponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.view_shop_weapon, parent, false);
            //view.setElevation(10);
            ShopWeaponViewHolder holder = new ShopWeaponViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ShopWeaponViewHolder holder, int position) {
            Weapon w = mWeapons.get(position);
            holder.bindWeapon(w);
            //setAnimation(holder.itemView, position);
        }

        private void setAnimation(View viewToAnimate, int position){
            if(position > lastPosition){
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_top);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public int getItemCount() {
            return mWeapons.size();
        }
    }
}
