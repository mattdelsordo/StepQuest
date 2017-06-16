package com.mdelsordo.stepquest.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.Character;
import com.mdelsordo.stepquest.model.Weapon;

/**
 * Created by mdelsord on 5/28/17.
 * A dialog that displays info on and allows the user to equip a weapon.
 */

public class WeaponInfoDialog extends DialogFragment {

    public static final int REQUEST_CODE = 12;
    public static final int RESULT_EQUIP = 13, RESULT_SELL = 14;

    /** Dialog needs to display:
     * Icon
     * Name
     * Type (with accompanying bonus)
     * Material (with accompanying bonus)
     * Total bonus
     * Quantity
     * Buttons to: close, equip, and buy (for x amount of money)
     */

    private static final String ARG_WEAPON = "arg_weapon", ARG_QUANTITY = "arg_quantity", ARG_CHARACTER = "arg_character";

    public static WeaponInfoDialog newInstance(Weapon w, Character c, int quantity){
        Bundle args = new Bundle();
        args.putSerializable(ARG_WEAPON, w);
        args.putInt(ARG_QUANTITY, quantity);
        args.putSerializable(ARG_CHARACTER, c);

        WeaponInfoDialog dialog = new WeaponInfoDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        Weapon weapon = (Weapon)args.getSerializable(ARG_WEAPON);
        Character character = (Character)args.getSerializable(ARG_CHARACTER);
        int quantity = args.getInt(ARG_QUANTITY);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_weapon_info, null);
        //TODO: populate view
        String wName = weapon.getName();
        String wQuantity = " x" + quantity;
        String wPrice = "Sale price: " + weapon.getSalePrice(character) + "g";
        String wType = "Type: " + getString(weapon.getTypeString()) + " (x" + weapon.getProficiencyModifier(character.getVocation())+")";
        String wMaterial = "Material: " + getString(weapon.getMaterialString()) + " (x"+weapon.getMaterial() + ")";
        String wTotalMod = "Total Modifier: x" + weapon.getModifier(character);

        ImageView vIcon = (ImageView)view.findViewById(R.id.iv_weaponinfo_icon);
        vIcon.setImageResource(weapon.getDrawable());
        TextView vName = (TextView)view.findViewById(R.id.tv_weaponinfo_name);
        vName.setText(wName);
        TextView vQuantity = (TextView)view.findViewById(R.id.tv_weaponinfo_quantity);
        vQuantity.setText(wQuantity);
        TextView vPrice = (TextView)view.findViewById(R.id.tv_weaponinfo_saleprice);
        vPrice.setText(wPrice);
        TextView vType = (TextView)view.findViewById(R.id.tv_weaponinfo_type);
        vType.setText(wType);
        TextView vMaterial = (TextView)view.findViewById(R.id.tv_weaponinfo_material);
        vMaterial.setText(wMaterial);
        TextView vTotalMod = (TextView)view.findViewById(R.id.tv_weaponinfo_modifier);
        vTotalMod.setText(wTotalMod);

        return new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setView(view).setNegativeButton(getString(R.string.back), null)
                .setPositiveButton(getString(R.string.equip_weapon), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(
                                getTargetRequestCode(),
                                RESULT_EQUIP,
                                null
                        );
                    }
                }).setNeutralButton(getString(R.string.sell_one_weapon), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(
                                getTargetRequestCode(),
                                RESULT_SELL,
                                null
                        );
                    }
                }).create();
    }
}
