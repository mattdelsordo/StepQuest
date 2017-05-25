package project3.csc214.stepquest.ui;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.EffectPlayer;
import project3.csc214.stepquest.model.Weapon;
import project3.csc214.stepquest.util.InventorySoundListener;

/**
 * Maintains a recyclerview that handles the inventory
 */
public class InventoryFragment extends Fragment implements ActiveCharacter.FundsUpdateListener, ActiveCharacter.WeaponUpdateListener{

    private RecyclerView mRecycler;
    private TextView mGoldCount;
    private InventorySoundListener mSoundListener;

    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory_md, container, false);

        //get goldcount
        mGoldCount = (TextView)view.findViewById(R.id.textview_inventory_goldcount);
        updateFunds(ActiveCharacter.getInstance(getContext()).getActiveCharacter().getFunds());

        //get and set up recyclerview
        mRecycler = (RecyclerView)view.findViewById(R.id.recyclerview_inventory);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }

    public void updateUI(){
        WeaponAdapter refresh = new WeaponAdapter(ActiveCharacter.getInstance(getContext()).getWeaponInventory());
        mRecycler.setAdapter(refresh);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActiveCharacter.getInstance(getContext()).bindFundsUpdater(this);
        ActiveCharacter.getInstance(getContext()).bindWeaponListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ActiveCharacter.getInstance(getContext()).unbindFundsUpdater();
        ActiveCharacter.getInstance(getContext()).unbindWeaponListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSoundListener = (InventorySoundListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSoundListener = null;
    }

    @Override
    public void updateFunds(int totalFunds) {
        mGoldCount.setText("Gold: " + totalFunds);
    }

    @Override
    public void updateList() {
        updateUI();
    }

    //viewholder for the list of weapons
    public class WeaponViewHolder extends RecyclerView.ViewHolder{

        private Weapon mWeapon;
        private View mView;
        private ImageView mIcon;
        private TextView mName, mBuff, mQuantity;

        public WeaponViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mIcon = (ImageView)itemView.findViewById(R.id.imageview_weapon_icon);
            mName = (TextView)itemView.findViewById(R.id.textview_weapon_name);
            mBuff = (TextView)itemView.findViewById(R.id.textview_weapon_buff);
            mQuantity = (TextView)itemView.findViewById(R.id.textview_weapon_quantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //play accompanying sound effect
                    switch(mWeapon.getType()){
                        case Weapon.BLADE:
                            mSoundListener.playEffect(EffectPlayer.SWORD);
                            break;
                        case Weapon.BOW:
                            mSoundListener.playEffect(EffectPlayer.BOW);
                            break;
                        case Weapon.BLUNT:
                            mSoundListener.playEffect(EffectPlayer.BLUNT);
                            break;
                        case Weapon.STAFF:
                            mSoundListener.playEffect(EffectPlayer.WAND);
                            break;
                        default:
                            break;
                    }

                    ActiveCharacter.getInstance(getContext()).setEquippedWeapon(mWeapon);
                    updateUI();

                    if(getActivity().findViewById(R.id.frame_inventory_detail) == null){
                        Toast.makeText(getContext(), "Equipped the " + mWeapon.getName() + "!", Toast.LENGTH_SHORT).show();
                    }else{
                        getChildFragmentManager().beginTransaction().replace(R.id.frame_inventory_detail, InventoryDetailFragment.newInstance(mWeapon)).commit();
                    }
                }
            });
        }

        public void bindWeapon(Weapon weapon){
            mWeapon = weapon;

            mName.setText(weapon.getName());
            double buff = mWeapon.calcBuff(ActiveCharacter.getInstance(getContext()).getActiveCharacter().getVocation());
            mBuff.setText((buff > 0.0 ? "+" : "") + buff + "%");
            mQuantity.setText("x" + ActiveCharacter.getInstance(getContext()).getWeaponQuantity(mWeapon));
            mIcon.setImageResource(mWeapon.getDrawable());

            if(mWeapon.getId() == ActiveCharacter.getInstance(getContext()).getEquippedWeapon().getId()){
                mView.findViewById(R.id.layout_weapon_background).setBackgroundColor(Color.rgb(255,215,0)); //should probably make this color a resource
            }
        }
    }

    //adapter for the list of weapons
    public class WeaponAdapter extends RecyclerView.Adapter<WeaponViewHolder>{
        private ArrayList<Weapon> mWeapons;
        private int lastPosition = -1;

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
            setAnimation(holder.itemView, position);
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
