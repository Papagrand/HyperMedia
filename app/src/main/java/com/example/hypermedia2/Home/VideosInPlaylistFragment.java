package com.example.hypermedia2.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoModel;
import com.example.hypermedia2.Video.VideosAdapter;

import java.util.ArrayList;

public class VideosInPlaylistFragment extends Fragment implements SearchView.OnQueryTextListener {

    private ArrayList<VideoModel> selectedVideosArrayList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String MY_SORT_PREF = "sortOrder";
    private VideosInPlaylistAdapter videosInPlaylistAdapter;
    private RecyclerView recyclerView;
    public boolean is_selectable = false;
    private boolean isBackPressed = false;
    TextView countText;

    Toolbar toolbar;
    private String name;

    private String mParam1;
    private String mParam2;

    public VideosInPlaylistFragment() {
        // Required empty public constructor
    }

    public static VideosInPlaylistFragment newInstance(String param1, String param2, ArrayList<VideoModel> selectedVideosArrayList) {
        VideosInPlaylistFragment fragment = new VideosInPlaylistFragment();
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
            name = getArguments().getString("playlistName");
            selectedVideosArrayList = getArguments().getParcelableArrayList("selectedList");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_videos_in_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.videoInPlaylist_recycleview);
        countText = view.findViewById(R.id.playlist_counter_textView);
        toolbar = view.findViewById(R.id.videoInPlaylistFolderToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!is_selectable){
                    isBackPressed = true;
                    Navigation.findNavController(requireView()).navigate(R.id.action_videosInPlaylistFragment_to_homeFragment);
                }
//                else{
//                    clearSelectingToolbar();
//                    videosAdapter.notifyDataSetChanged();
//                }
            }
        });
        int index = name.lastIndexOf("/");
        String onlyFolderName = name.substring(index+1);
        countText.setText(onlyFolderName);
        loadVideos();
    }
    private void loadVideos() {//Если ошибка будет в этой функции, то вместо getContext(), getActivity()

        if (name != null && selectedVideosArrayList != null) {
            videosInPlaylistAdapter = new VideosInPlaylistAdapter(selectedVideosArrayList, getContext(), this);
            recyclerView.setAdapter(videosInPlaylistAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        } else {
            Toast.makeText(getContext(), "can't find any videos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_playlist_toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.playlist_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        ImageView imageView = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.themecolor), PorterDuff.Mode.SRC_IN);
        searchView.setQueryHint("Search file name");
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!is_selectable){
                isBackPressed = true;
                Navigation.findNavController(requireView()).navigate(R.id.action_videosInPlaylistFragment_to_homeFragment);
            }
            return true;
        }else {
            SharedPreferences.Editor editor = ((AppCompatActivity) getActivity()).getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE).edit();
            switch (item.getItemId()) {
                case R.id.sort_by_date: {
                    editor.putString("sorting", "sortByDate");
                    editor.apply();
                    //loadVideos(); // Обновляем список видео
                    return true;
                }
                case R.id.sort_by_name: {
                    editor.putString("sorting", "sortByName");
                    editor.apply();
                    //loadVideos(); // Обновляем список видео
                    return true;
                }
                case R.id.sort_by_size: {
                    editor.putString("sorting", "sortBySize");
                    editor.apply();
                    //loadVideos(); // Обновляем список видео
                    return true;
                }
                case R.id.add_to_playlistdir: {
                    //refresh();
                    break;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}