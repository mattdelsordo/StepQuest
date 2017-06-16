package com.mdelsordo.stepquest.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;

/**
 * Created by mdelsord on 5/2/17.
 * A dialog that informs the user if their device has no pedometer
 */

public class NoPedometerDialog extends DialogFragment {
    public static final String TAG = "NoPedometer";

    public NoPedometerDialog(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_long_dialog_text, null);
        TextView text = (TextView)view.findViewById(R.id.textview_longdialog);
        text.setText(getString(R.string.no_pedometer_message));

        return new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setView(view).setPositiveButton(R.string.no_pedometer_confirm, null).create();
    }
}
