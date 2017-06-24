package com.mdelsordo.stepquest.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.Weapon;

/**
 * Created by mdelsord on 5/15/17.
 * Dialog that pops up to confirm some purchase.
 */

public class PurchaseDialog extends DialogFragment{

    public static final int REQUEST_CODE = 11;
    public static final String ARG_NAME = "arg_name", ARG_PRICE = "arg_priece";
    private Weapon mWeapon;

    public static PurchaseDialog newInstance(String name, int price) {

        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putInt(ARG_PRICE, price);
        PurchaseDialog fragment = new PurchaseDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String name = args.getString(ARG_NAME);
        int price = args.getInt(ARG_PRICE);

        //View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_long_dialog_text, null);
        //TextView text = (TextView)view.findViewById(R.id.textview_longdialog);
        //text.setText("Are you sure you want to buy that " + name + " for " + price + "g?");
        return new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle("Purchase " + name + "?").setMessage("Are you sure you want to buy that " + name + " for " + price + "g?").setNegativeButton(R.string.no_thanks, null).setPositiveButton(R.string.yes_please, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTargetFragment().onActivityResult(
                        getTargetRequestCode(),
                        Activity.RESULT_OK,
                        null
                );
            }
        }).create();
    }
}
