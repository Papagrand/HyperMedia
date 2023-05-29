package com.example.hypermedia2.Home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoFolder;
import com.example.hypermedia2.Video.VideoModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements PlaylistDialog.PlaylistDialogListener {

    private static String FILE_NAME;

    private ArrayList<String> folderList = new ArrayList<>();
    private ArrayList<VideoModel> selectedVideosArrayList;
    PlaylistAdapter playlistAdapter;
    RecyclerView recyclerView;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2, ArrayList<VideoModel> selectedVideosArrayList) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putParcelableArrayList("selectionArrayList", selectedVideosArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            selectedVideosArrayList = getArguments().getParcelableArrayList("selectedList");

        }
    }
    private void displayFolders() {
        String parentPath = getActivity().getExternalFilesDir(null).getAbsolutePath();
        File parentFolder = new File(parentPath);
        File[] subFolders = parentFolder.listFiles(File::isDirectory);

        if (subFolders != null) {
            folderList.clear();
            for (File folder : subFolders) {
                folderList.add(folder.getAbsolutePath());
            }
        }

        playlistAdapter.updateData(folderList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View playlistView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = playlistView.findViewById(R.id.home_recycle_view);

        playlistAdapter = new PlaylistAdapter(folderList, getActivity(), selectedVideosArrayList);
        recyclerView.setAdapter(playlistAdapter);

        return playlistView;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayFolders();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageButton addPlaylistImageButton= view.findViewById(R.id.button_add_playlist);

        addPlaylistImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAdd();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void openDialogAdd() {
        PlaylistDialog playlistDialog = new PlaylistDialog();
        playlistDialog.setListener(this);
        playlistDialog.show(getActivity().getSupportFragmentManager(), "playlist dialog");
    }

    public void createPlaylistFolder(String playlistName) {
        File dir = new File(getActivity().getExternalFilesDir(null), playlistName);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Toast.makeText(getActivity(), "Папка плейлиста создана", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Ошибка при создании папки плейлиста", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Папка плейлиста уже существует", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void applyParams(String playlistName) {
        createPlaylistFolder(playlistName);
    }

}