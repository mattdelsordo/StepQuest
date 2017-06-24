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

/**
 * Created by mdelsord on 5/2/17.
 * Forces the user to confirm that they want to delete their character
 */

public class SureYouWantToDeleteDialog extends DialogFragment{

    public static final int REQUEST_CODE = 5;

    public SureYouWantToDeleteDialog(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_long_dialog_text, null);
        //TextView text = (TextView)view.findViewById(R.id.textview_longdialog);
        //text.setText(getString(R.string.delete_character_confirmation));

        //TextView textView = new TextView();
        return new AlertDialog.Builder(getActivity(), R.style.DialogTheme).setTitle(R.string.delete_save_file).setMessage(getString(R.string.delete_character_confirmation)).setNegativeButton(R.string.refuse_delete, null).setPositiveButton(R.string.confirm_delete, new DialogInterface.OnClickListener() {
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
