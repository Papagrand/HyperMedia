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

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoFragment;
import com.example.hypermedia2.Video.VideoModel;
import com.example.hypermedia2.Video.VideosAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VideosInPlaylistFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnLongClickListener {

    private ArrayList<VideoModel> selectedVideosArrayList;
    ArrayList<VideoModel> playlistSelectionArrayList = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String MY_SORT_PREF = "sortOrder";
    private ArrayList<VideoModel> videosInPlaylist;
    private VideosInPlaylistAdapter videosInPlaylistAdapter;
    private RecyclerView recyclerView;
    int count = 0;
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
        if (selectedVideosArrayList!=null){
            sendVideosToDataBase();
        }
        loadVideos();
    }
    private void sendVideosToDataBase(){
        DBaseHelper dBaseHelper = new DBaseHelper(getContext());
        dBaseHelper.openDataBase();
        ArrayList<VideoModel> tempArrayList = dBaseHelper.getVideosFromDataBase(name);
        for (VideoModel video: selectedVideosArrayList){
            boolean findFlag=false;
            for (VideoModel tempVideo: tempArrayList){
                if (tempVideo.getId().equals(video.getId())) {
                    findFlag = true;
                    break;
                }
            }
            if (!findFlag) {
                dBaseHelper.writeVideoToPlaylistDatabase(video, name);
            }
        }
        dBaseHelper.close();
    }
    private void deleteVideosFromDataBase(){
        DBaseHelper dBaseHelper = new DBaseHelper(getContext());
        dBaseHelper.openDataBase();
        ArrayList<VideoModel> tempArrayList = dBaseHelper.getVideosFromDataBase(name);

        for (VideoModel video: playlistSelectionArrayList){
            for (VideoModel tempVideo: tempArrayList){
                if (tempVideo.getId().equals(video.getId())) {
                    //Вызов функции для удаления текущего элемента из базы данных
                    dBaseHelper.deleteVideoFromDatabase(tempVideo,name);
                }
            }
        }
        dBaseHelper.close();
        loadVideos();
    }
    private void loadVideos() {
        DBaseHelper dBaseHelper = new DBaseHelper(getContext());
        dBaseHelper.openDataBase();

        if (name != null && dBaseHelper.getAllPlaylists() != null) {
            videosInPlaylist = getAllVideosFromDB(getContext(),dBaseHelper);
            videosInPlaylistAdapter = new VideosInPlaylistAdapter(videosInPlaylist, getContext(), this);
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
            }else{
                clearSelectingToolbar();
                videosInPlaylistAdapter.notifyDataSetChanged();
            }
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
                case R.id.add_to_playlist: {
                    Bundle bundle = new Bundle();
                    String fromVideosInPlaylist = "VIPString";
                    bundle.putString("VIP", fromVideosInPlaylist);
                    VideoFragment videoFragment = new VideoFragment();
                    videoFragment.setArguments(bundle);

                    NavController navController = Navigation.findNavController(requireView()); // Получаем NavController
                    navController.navigate(R.id.action_videosInPlaylistFragment_to_videoFragment, bundle);
                    break;
                }
                case R.id.from_playlist_replace:{
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("selectedList", playlistSelectionArrayList);
                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);

                    NavController navController = Navigation.findNavController(requireView()); // Получаем NavController
                    navController.navigate(R.id.action_videosInPlaylistFragment_to_homeFragment, bundle);

                    break;
                }
                case R.id.from_playlist_delete:{
                    deleteVideosFromDataBase();

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

    @Override
    public boolean onLongClick(View v) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.item_selected_in_playlist_menu);
        is_selectable = true;
        videosInPlaylistAdapter.notifyDataSetChanged();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
        return true;
    }

    public void prepareSelection(View v, int position) {
        if( ((CheckBox)v).isChecked()){
            playlistSelectionArrayList.add(videosInPlaylist.get(position));
            count++;
            updateCount(count);
        }else{
            playlistSelectionArrayList.remove(videosInPlaylist.get(position));
            count--;
            updateCount(count);
        }
    }
    private void updateCount(int counts) {
        if (counts == 0){
            countText.setText("Выбрано: 0");
        }else {
            countText.setText("Выбрано: "+counts);
        }
    }
    private void clearSelectingToolbar(){
        is_selectable = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.main_playlist_toolbar_menu);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
        int index = name.lastIndexOf("/");
        String onlyFolderName = name.substring(index+1);
        countText.setText(onlyFolderName);
        count = 0;
        playlistSelectionArrayList.clear();
    }
}