package com.mdelsordo.stepquest.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;

/**
 * Created by mdelsord on 5/2/17.
 * A dialog that informs the user if their device has no pedometer
 */

public class NoPedometerDialog extends DialogFragment {
    public static final String TAG = "NoPedometer";

    public static final String PREF_DONT_SHOW = "pref_dont_show_pedometer_alert";

    public NoPedometerDialog(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_no_pedometer, null);
        //TextView text = (TextView)view.findViewById(R.id.textview_longdialog);
        //text.setText(getString(R.string.no_pedometer_message));
        final CheckBox cb = (CheckBox)view.findViewById((R.id.cb_nopedometer_dontshow));

        return new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setView(view).setTitle(R.string.no_pedometer_detected).setMessage(R.string.no_pedometer_message).setPositiveButton(R.string.no_pedometer_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                prefs.edit().putBoolean(PREF_DONT_SHOW, cb.isChecked()).apply();
            }
        }).create();
    }
}
