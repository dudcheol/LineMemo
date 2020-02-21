package com.example.linememo.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;

import com.example.linememo.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogUtil {
    public static MaterialAlertDialogBuilder makeDialog(Context context, @DrawableRes int icon, String title, String message) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context)
                .setIcon(icon)
                .setTitle(title);

        if (message != null)
            materialAlertDialogBuilder.setMessage(message);

        return materialAlertDialogBuilder.setMessage(message);
    }

    public static MaterialAlertDialogBuilder makeDialogWithView(Context context, @DrawableRes int icon, String title, String message, View view, boolean cancelable
            , String posBtnText, String negBtnText, DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = makeDialog(context, icon, title, message);
        materialAlertDialogBuilder.setView(view)
                .setCancelable(cancelable);
        if (posBtnText != null && posListener != null) {
            materialAlertDialogBuilder
                    .setPositiveButton(posBtnText, posListener);
        }

        if (negBtnText != null && negListener != null) {
            materialAlertDialogBuilder
                    .setNegativeButton(negBtnText, negListener);
        }
        return materialAlertDialogBuilder;
    }

    public static MaterialAlertDialogBuilder showDialog(Context context, @DrawableRes int icon, String title, String message, String posBtnText
            , String negBtnText, DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = makeDialog(context, icon, title, message);

        if (posBtnText != null && posListener != null) {
            materialAlertDialogBuilder
                    .setPositiveButton(posBtnText, posListener);
        }

        if (negBtnText != null && negListener != null) {
            materialAlertDialogBuilder
                    .setNegativeButton(negBtnText, negListener);
        }

        materialAlertDialogBuilder.show();
        return materialAlertDialogBuilder;
    }

    public static MaterialAlertDialogBuilder showDialogItems(Context context, @DrawableRes int icon, String title, String message, @ArrayRes int array, DialogInterface.OnClickListener listener) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = makeDialog(context, icon, title, message);
        materialAlertDialogBuilder.setItems(array, listener)
                .show();
        return materialAlertDialogBuilder;
    }

    public static MaterialAlertDialogBuilder showErrDialog(final Context context) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = showDialog(
                context
                , R.drawable.ic_error_black_24dp
                , context.getResources().getString(R.string.err_dialog_title)
                , context.getResources().getString(R.string.err_dialog_message)
                , null
                , context.getResources().getString(R.string.positiveBtn)
                , null
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                });
        materialAlertDialogBuilder.setCancelable(false)
                .show();
        return materialAlertDialogBuilder;
    }

    public static DialogInterface.OnClickListener onClickCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
}