package com.mdelsordo.stepquest.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.Weapon;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryDetailFragment extends Fragment {
    public static final String ARG_NAME = "arg_name";

    public static InventoryDetailFragment newInstance(Weapon w){
        Bundle bundle = new Bundle();
        bundle.putString(ARG_NAME, w.getName());
        InventoryDetailFragment fragment = new InventoryDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public InventoryDetailFragment() {
        // Required empty public constructor
    }


    private TextView mWeaponName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory_detail, container, false);

        mWeaponName = (TextView)view.findViewById(R.id.textview_masterdetail_title);

        Bundle args = getArguments();
        mWeaponName.setText("Equipped the " + args.getString(ARG_NAME));

        return view;
    }

}
