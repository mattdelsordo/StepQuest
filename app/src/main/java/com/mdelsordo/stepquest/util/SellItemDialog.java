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
import com.mdelsordo.stepquest.model.Character;
import com.mdelsordo.stepquest.model.Weapon;

/**
 * Created by mdelsord on 5/28/17.
 * Dialog that makes sure the user wants to sell a weapon.
 */

public class SellItemDialog extends DialogFragment{
    public static final int REQUEST_CODE = 16;
    public static final String ARG_NAME = "arg_name", ARG_PRICE = "arg_priece";
    public static SellItemDialog newInstance(Weapon weapon, Character character){
        Bundle args = new Bundle();
        args.putString(ARG_NAME, weapon.getName());
        args.putInt(ARG_PRICE, weapon.getSalePrice(character));
        SellItemDialog dialog = new SellItemDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String name = args.getString(ARG_NAME);
        int price = args.getInt(ARG_PRICE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_long_dialog_text, null);
        TextView text = (TextView)view.findViewById(R.id.textview_longdialog);
        text.setText("Are you sure you want to sell one (1) "+ name + " for " + price + " gold?");
        return new AlertDialog.Builder(getActivity()).setView(view).setNegativeButton(getString(R.string.no_thanks), null)
                .setPositiveButton(getString(R.string.make_the_sale), new DialogInterface.OnClickListener() {
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
