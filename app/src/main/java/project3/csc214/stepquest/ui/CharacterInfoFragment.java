package project3.csc214.stepquest.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project3.csc214.stepquest.R;


/**
 * A simple {@link Fragment} subclass.
 * This class displays all character info.
 */
public class CharacterInfoFragment extends Fragment {


    public CharacterInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_info, container, false);
    }

}
