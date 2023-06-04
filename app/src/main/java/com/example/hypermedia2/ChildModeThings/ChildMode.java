package com.example.hypermedia2.ChildModeThings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.Home.PlaylistAdapter;
import com.example.hypermedia2.LoginThings.LoginActivity;
import com.example.hypermedia2.MainActivity;
import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoModel;

import java.io.File;
import java.util.ArrayList;

public class ChildMode extends Fragment {

    private static String FILE_NAME;

    private ArrayList<String> folderList = new ArrayList<>();
    ChildModePlaylistAdapter playlistAdapter;
    RecyclerView recyclerView;
    int count = 0;
    public boolean is_selectable = false;
    private boolean isBackPressed = false;
    TextView countText;
    LinearLayout goToMainMode;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ChildMode() {
    }

    public static ChildMode newInstance(String param1, String param2) {
        ChildMode fragment = new ChildMode();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_mode, container, false);
    }
    //Изменить, чтобы отображались только с детским контроллем
    private void displayFolders() {
        String parentPath = getActivity().getExternalFilesDir(null).getAbsolutePath();
        File parentFolder = new File(parentPath);
        File[] subFolders = parentFolder.listFiles(File::isDirectory);
        DBaseHelper dBaseHelper = new DBaseHelper(getContext());
        dBaseHelper.openDataBase();
        if (subFolders != null) {
            folderList.clear();
            for (File folder : subFolders) {
                String isFolderForKids = dBaseHelper.getPlaylistForKidsStatus(folder.toString());
                if (isFolderForKids.equals("yes")) {
                    folderList.add(folder.getAbsolutePath());
                }
            }
        }
        playlistAdapter.updateData(folderList);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.child_mode_recycle_view);
        playlistAdapter = new ChildModePlaylistAdapter(folderList, getActivity(),this);
        recyclerView.setAdapter(playlistAdapter);
        ImageButton addPlaylistImageButton= view.findViewById(R.id.button_add_playlist);
        goToMainMode = view.findViewById(R.id.goToMainMode);
        goToMainMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        displayFolders();
        super.onResume();
    }


}