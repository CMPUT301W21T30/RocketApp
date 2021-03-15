package com.example.rocketapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

public class SaveFragment extends AppCompatDialogFragment {

    /**
     * Popup to confirm information has been saved on the user profile page
     * @param savedInstanceState
     *          Bundle with saved information relevant to current Activity state
     * @return an AlertDialog with the arguments supplied to this builder.      //https://developer.android.com/reference/android/app/AlertDialog.Builder
     */
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("saved!")
                .setMessage("your information has been saved successfully")
                .setPositiveButton("ok", new DialogInterface.OnClickListener(){
                    @Override
                    public  void onClick(DialogInterface dialogInterface, int i ){

                    }

        });

        return builder.create();
    }
}
