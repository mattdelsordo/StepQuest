package project3.csc214.stepquest.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.Weapon;

/**
 * Maintains a recyclerview that handles the inventory
 */
public class InventoryFragment extends Fragment {

    private RecyclerView mRecycler;

    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        //get and set up recyclerview
        mRecycler = (RecyclerView)view.findViewById(R.id.recyclerview_inventory);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }

    public void updateUI(){
        WeaponAdapter refresh = new WeaponAdapter(ActiveCharacter.getInstance().getWeaponInventory());
        mRecycler.setAdapter(refresh);
    }

    //viewholder for the list of weapons
    public class WeaponViewHolder extends RecyclerView.ViewHolder{

        private Weapon mWeapon;
        private View mView;
        private ImageView mIcon;
        private TextView mName, mType, mMaterial, mBuff;

        public WeaponViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mIcon = (ImageView)itemView.findViewById(R.id.imageview_weapon_icon);
            mName = (TextView)itemView.findViewById(R.id.textview_weapon_name);
            mType = (TextView)itemView.findViewById(R.id.textview_weapon_type);
            mMaterial = (TextView)itemView.findViewById(R.id.textview_weapon_material);
            mBuff = (TextView)itemView.findViewById(R.id.textview_weapon_buff);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActiveCharacter.getInstance().setEquippedWeapon(mWeapon);
                    updateUI();
                }
            });
        }

        public void bindWeapon(Weapon weapon){
            mWeapon = weapon;

            mName.setText(weapon.getName());
            mType.setText(Weapon.getTypeString(mWeapon.getType()));
            mMaterial.setText(Weapon.getMaterialString(mWeapon.getMaterial()));
            double buff = mWeapon.calcBuff(ActiveCharacter.getInstance().getActiveCharacter().getVocation());
            mBuff.setText("+" + buff + "%");

            if(mWeapon.getId() == ActiveCharacter.getInstance().getEquippedWeapon().getId()){
                mView.findViewById(R.id.layout_weapon_background).setBackgroundColor(Color.rgb(255,215,0)); //should probably make this color a resource
            }
        }
    }

    //adapter for the list of weapons
    public class WeaponAdapter extends RecyclerView.Adapter<WeaponViewHolder>{
        private ArrayList<Weapon> mWeapons;

        public WeaponAdapter(Collection<Weapon> weapons){
            mWeapons = new ArrayList<>(weapons);
            Collections.sort(mWeapons, new Weapon.WeaponComparator());
        }

        @Override
        public WeaponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.view_weapon, parent, false);
            WeaponViewHolder holder = new WeaponViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(WeaponViewHolder holder, int position) {
            Weapon w = mWeapons.get(position);
            holder.bindWeapon(w);
        }

        @Override
        public int getItemCount() {
            return mWeapons.size();
        }
    }

}
