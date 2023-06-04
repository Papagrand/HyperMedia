package com.example.hypermedia2.ChildModeThings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.hypermedia2.Home.PlaylistDialog;
import com.example.hypermedia2.OrganizationOfParentControll.OrganizationActivity;
import com.example.hypermedia2.R;

public class ChildModeDialogFragment extends AppCompatDialogFragment {
    private PlaylistDialog.PlaylistDialogListener listener;

    public void setListener(PlaylistDialog.PlaylistDialogListener listener) {
        this.listener = listener;
    }
    Button cancelButton, yesButton;

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_do_you_wanna,null);

        builder.setView(view);
//                .setTitle("Добавить Плейлист")
//                .setNegativeButton("отменить", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
//                .setPositiveButton("добавить", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })

        cancelButton = view.findViewById(R.id.btn_controll_cancel);
        yesButton = view.findViewById(R.id.btn_yes);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OrganizationActivity.class);
                startActivity(intent);
                dismiss();
            }
        });
        return builder.create();
    }


    public interface PlaylistDialogListener{
    }
}
