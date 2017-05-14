package project3.csc214.stepquest.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.WeaponList;
import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.Weapon;

/**
 * RecyclerView handler that displays weapons that the player can purchase.
 */
public class ShopWeaponFragment extends Fragment {


    public ShopWeaponFragment() {
        // Required empty public constructor
    }

    private static final int COLUMN_COUNT = 2;
    private RecyclerView mRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_weapon, container, false);

        mRecycler = (RecyclerView)view.findViewById(R.id.rv_shop_weapons);
        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN_COUNT));
        refreshList();

        return view;
    }

    public void refreshList(){
        ShopWeaponAdapter refresh = new ShopWeaponAdapter(WeaponList.getInstance(getContext()).getWood());
        mRecycler.setAdapter(refresh);
    }

    //viewholder that holds the views for the weapons
    private class ShopWeaponViewHolder extends RecyclerView.ViewHolder{

        private Weapon mWeapon;
        private View mView;
        private ImageView mWeaponIcon;
        private TextView mName, mPrice, mBuff;

        public ShopWeaponViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mWeaponIcon = (ImageView)mView.findViewById(R.id.iv_shop_weaponicon);
            mName = (TextView)mView.findViewById(R.id.tv_shop_weaponname);
            mPrice = (TextView)mView.findViewById(R.id.tv_shop_weaponprice);
            mBuff = (TextView)mView.findViewById(R.id.tv_shop_weaponbuff);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: prompt user to buy the thing here
                }
            });
        }

        public void bindWeapon(Weapon weapon){
            mWeapon = weapon;
            mName.setText(weapon.getName());
            double buff = mWeapon.calcBuff(ActiveCharacter.getInstance(getContext()).getActiveCharacter().getVocation());
            mBuff.setText((buff > 0.0 ? "+" : "") + buff + "%");
            mWeaponIcon.setImageResource(mWeapon.getDrawable());
            mPrice.setText(Integer.toString(mWeapon.getPrice()));
        }
    }

    //adapter for list of weapons
    private class ShopWeaponAdapter extends RecyclerView.Adapter<ShopWeaponViewHolder>{
        private ArrayList<Weapon> mWeapons;

        public ShopWeaponAdapter(Collection<Weapon> weapons){
            mWeapons = new ArrayList<>(weapons);
            Collections.sort(mWeapons, new Weapon.WeaponComparator());
        }

        @Override
        public ShopWeaponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.view_shop_weapon, parent, false);
            ShopWeaponViewHolder holder = new ShopWeaponViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ShopWeaponViewHolder holder, int position) {
            Weapon w = mWeapons.get(position);
            holder.bindWeapon(w);
        }

        @Override
        public int getItemCount() {
            return mWeapons.size();
        }
    }




}
