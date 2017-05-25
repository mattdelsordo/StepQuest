package project3.csc214.stepquest.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.Boost;
import project3.csc214.stepquest.model.EffectPlayer;
import project3.csc214.stepquest.services.BoostTimerService;
import project3.csc214.stepquest.util.BoostOverListener;
import project3.csc214.stepquest.util.CenteredItemDecoration;
import project3.csc214.stepquest.util.PurchaseDialog;
import project3.csc214.stepquest.util.ShopFragmentListener;

/**
 * Shop panel that handles boost sales
 */
public class ShopBoostFragment extends Fragment implements BoostOverListener{
    private static final String TAG = "BoostShop";

    public ShopBoostFragment() {
        // Required empty public constructor
    }


    private static final int COLUMN_COUNT = 2, COLUMN_SPACING = 30;
    private RecyclerView mRecycler;
    private ShopFragmentListener mGoldListener;
    private Boost mQueuedBoost;
    private ArrayList<Boost> mBoosts;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_boost, container, false);
        mBoosts = Boost.generatePossibleBoostList(ActiveCharacter.getInstance(getContext()).getActiveCharacter());

        mRecycler = (RecyclerView)view.findViewById(R.id.rv_shop_boosts);
        //GridLayoutManager layout = new GridLayoutManager(getActivity(), COLUMN_COUNT);
        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN_COUNT));

        mRecycler.addItemDecoration(new CenteredItemDecoration(COLUMN_COUNT, COLUMN_SPACING));
        refreshList();

        return view;
    }

    public void refreshList(){
        ShopBoostAdapter refresh = new ShopBoostAdapter(mBoosts);
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
                mGoldListener.playEffect(EffectPlayer.BOOST);

                //make boost happen
                //add weapon to player inventory
                ActiveCharacter active = ActiveCharacter.getInstance(getContext());
                active.setBoost(mQueuedBoost);
                Log.i(TAG, "Duration = " + mQueuedBoost.getDuration());
                getContext().startService(BoostTimerService.newInstance(getContext(), mQueuedBoost.getDuration()));

                //subtract gold
                int newGoldAmnt = active.getActiveCharacter().getFunds() - mQueuedBoost.getPrice();
                active.getActiveCharacter().setFunds(newGoldAmnt);
                mGoldListener.updateGoldTotal(newGoldAmnt);
                refreshList();
            }else{
                mQueuedBoost = null;
            }
        }
        else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void boostOver() {
        refreshList();
    }

    @Override
    public void onPause() {
        super.onPause();
        ActiveCharacter.getInstance(getContext()).bindBoostOverListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActiveCharacter.getInstance(getContext()).unbindBoostOVerListener();
    }

    //viewholder that holds the views for the weapons
    private class ShopBoostViewHolder extends RecyclerView.ViewHolder{

        private Boost mBoost;
        private ImageView mBoostIcon;
        private TextView mName, mPrice, mBuff;
        private Button mPurchase;
        public View mView;

        public ShopBoostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mBoostIcon = (ImageView)itemView.findViewById(R.id.iv_shop_boosticon);
            mName = (TextView)itemView.findViewById(R.id.tv_shop_boostname);
            mPrice = (TextView)itemView.findViewById(R.id.tv_shop_boostprice);
            mBuff = (TextView)itemView.findViewById(R.id.tv_shop_boostbuff);
            mPurchase = (Button)itemView.findViewById(R.id.b_shop_purchaseBoost);

            mPurchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mQueuedBoost = mBoost;
                    AppCompatActivity parent = (AppCompatActivity)v.getContext();
                    FragmentManager manager = parent.getSupportFragmentManager();
                    PurchaseDialog dialog = PurchaseDialog.newInstance(mQueuedBoost.getName(), mQueuedBoost.getPrice());
                    dialog.setTargetFragment(ShopBoostFragment.this, PurchaseDialog.REQUEST_CODE);
                    dialog.show(manager, "DialogPurchase");
                }
            });
        }

        public void bindBoost(Boost boost){
            mBoost = boost;
            mName.setText(mBoost.getName());
            mBuff.setText(mBoost.getDesc());
            mBoostIcon.setImageResource(R.drawable.ic_boost_dark);
            mPrice.setText(Integer.toString(mBoost.getPrice()));

            //disable button if there isn't enough money
            ActiveCharacter active = ActiveCharacter.getInstance(getContext());
            if(active.getActiveCharacter().getFunds() < mBoost.getPrice() || active.getBoostMultiplier() > 1.0){
                mPurchase.setEnabled(false);
            }
        }
    }

    //adapter for list of weapons
    private class ShopBoostAdapter extends RecyclerView.Adapter<ShopBoostViewHolder>{
        private ArrayList<Boost> mBoostList;
        private int lastPosition = -1;

        public ShopBoostAdapter(ArrayList<Boost> boosts){
            mBoostList = boosts;
        }

        @Override
        public ShopBoostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.view_shop_boost, parent, false);
            ShopBoostViewHolder holder = new ShopBoostViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ShopBoostViewHolder holder, int position) {
            Boost b = mBoostList.get(position);
            holder.bindBoost(b);
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
            return mBoostList.size();
        }
    }

}
