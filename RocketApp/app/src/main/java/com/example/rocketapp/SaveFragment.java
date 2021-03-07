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

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("saved!")
                .setMessage("you're information has been saved successfully")
                .setPositiveButton("ok", new DialogInterface.OnClickListener(){
                    @Override
                    public  void onClick(DialogInterface dialogInterface, int i ){

                    }

        });

        return builder.create();
    }
}
