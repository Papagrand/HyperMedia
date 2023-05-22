package com.example.hypermedia2.Music;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hypermedia2.R;

import java.util.ArrayList;

public class MusicFragment extends Fragment {
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private ArrayList<Music> musicArrayList;

    @Override
    public void onResume() {
        super.onResume();
    }

    public MusicFragment() {
        // Required empty public constructor
    }


    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
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

        View musicView = inflater.inflate(R.layout.fragment_music, container, false);

        recyclerView = musicView.findViewById(R.id.music_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(musicView.getContext()));
        return musicView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.music_recycler_view);
        MusicAdapter musicAdapter = new MusicAdapter(requireContext());
        ArrayList<Music> musicList = musicAdapter.getMusicArrayList(requireContext());
        musicAdapter.setMusicList(musicList);
        recyclerView.setAdapter(musicAdapter);

    }
}