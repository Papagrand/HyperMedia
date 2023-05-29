package com.example.hypermedia2.Home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.hypermedia2.R;

public class PlaylistDialog extends AppCompatDialogFragment {
    private EditText editTextplaylistName;
    private PlaylistDialogListener listener;

    public void setListener(PlaylistDialogListener listener) {
        this.listener = listener;
    }
    Button cancelButton, addButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_playlist,null);

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

        editTextplaylistName = view.findViewById(R.id.edit_playlistname);
        cancelButton = view.findViewById(R.id.btn_cancel);
        addButton = view.findViewById(R.id.btn_add);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playlistName = editTextplaylistName.getText().toString();
                listener.applyParams(playlistName);
                dismiss();
            }
        });
        return builder.create();
    }


    public interface PlaylistDialogListener{
        void applyParams(String playlistName);
    }
}
