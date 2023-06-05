package com.example.hypermedia2.ChildModeThings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.Home.HomeFragment;
import com.example.hypermedia2.Home.VideosInPlaylistAdapter;
import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoFragment;
import com.example.hypermedia2.Video.VideoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VideosInChildModePlaylistFragment extends Fragment implements SearchView.OnQueryTextListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String MY_SORT_PREF = "sortOrder";
    private ArrayList<VideoModel> videosInPlaylist;
    private VideosInChildModePlaylistAdapter videosInPlaylistAdapter;
    private RecyclerView recyclerView;
    private boolean isBackPressed = false;
    TextView countText;

    Toolbar toolbar;
    private String name;

    private String mParam1;
    private String mParam2;

    public VideosInChildModePlaylistFragment() {
        // Required empty public constructor
    }


    public static VideosInChildModePlaylistFragment newInstance(String param1, String param2) {
        VideosInChildModePlaylistFragment fragment = new VideosInChildModePlaylistFragment();
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
            name = getArguments().getString("playlistName");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_videos_in_child_mode_playlist, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.videoInChildModePlaylist_recycleview);
        countText = view.findViewById(R.id.childModePlaylist_counter_textView);
        toolbar = view.findViewById(R.id.videoInChildModePlaylistFolderToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
        int index = name.lastIndexOf("/");
        String onlyFolderName = name.substring(index+1);
        countText.setText(onlyFolderName);
        loadVideos();

    }
    private void loadVideos() {
        DBaseHelper dBaseHelper = new DBaseHelper(getContext());
        dBaseHelper.openDataBase();

        if (name != null && dBaseHelper.getAllPlaylists() != null) {
            videosInPlaylist = getAllVideosFromDB(getContext(),dBaseHelper);
            videosInPlaylistAdapter = new VideosInChildModePlaylistAdapter(videosInPlaylist, getContext(), this);
            recyclerView.setAdapter(videosInPlaylistAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        } else {
            Toast.makeText(getContext(), "can't find any videos", Toast.LENGTH_SHORT).show();
        }
        dBaseHelper.close();
    }


    private ArrayList<VideoModel> getAllVideosFromDB(Context context, DBaseHelper dBaseHelper){
        SharedPreferences preferences = context.getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE);
        String sort = preferences.getString("sorting", "sortByName");
        ArrayList<VideoModel> list = new ArrayList<>();
        if (dBaseHelper.getAllPlaylists() != null) {
            list = dBaseHelper.getVideosFromDataBase(name);

            switch (sort) {
                case "sortByName":
                    // Сортировка по названию
                    Collections.sort(list, new Comparator<VideoModel>() {
                        @Override
                        public int compare(VideoModel video1, VideoModel video2) {
                            return video1.getTitle().compareToIgnoreCase(video2.getTitle());
                        }
                    });
                    break;
                case "sortBySize":
                    // Сортировка по размеру
                    Collections.sort(list, new Comparator<VideoModel>() {
                        @Override
                        public int compare(VideoModel video1, VideoModel video2) {
                            return video1.getSize().compareTo(video2.getSize());
                        }
                    });
                    break;
            }
        }
        return list;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_playlist_toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.playlist_search);
        MenuItem itemDelete = menu.findItem(R.id.add_to_playlist);
        itemDelete.setVisible(false);
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
            isBackPressed = true;
            Navigation.findNavController(requireView()).navigate(R.id.action_videosInChildModePlaylistFragment_to_childMode);
            return true;
        }else {
            SharedPreferences.Editor editor = ((AppCompatActivity) getActivity()).getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE).edit();
            switch (item.getItemId()) {
                case R.id.playlist_sort_by_name: {
                    editor.putString("sorting", "sortByName");
                    editor.apply();
                    loadVideos(); // Обновляем список видео
                    return true;
                }
                case R.id.playlist_sort_by_size: {
                    editor.putString("sorting", "sortBySize");
                    editor.apply();
                    loadVideos(); // Обновляем список видео
                    return true;
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
        String input = newText.toLowerCase();
        ArrayList<VideoModel> searchList = new ArrayList<>();
        DBaseHelper dBaseHelper = new DBaseHelper(getContext());
        dBaseHelper.openDataBase();
        for(VideoModel model : dBaseHelper.getVideosFromDataBase(name)){
            if(model.getTitle().toLowerCase().contains(input)){
                searchList.add(model);
            }
        }
        videosInPlaylistAdapter.updateSearchList(searchList);
        dBaseHelper.close();
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        isBackPressed = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isBackPressed = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isBackPressed = false;
    }
}