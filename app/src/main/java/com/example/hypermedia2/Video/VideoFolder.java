package com.example.hypermedia2.Video;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.MainActivity;
import com.example.hypermedia2.R;

import java.util.ArrayList;
import java.util.Locale;


public class VideoFolder extends Fragment implements SearchView.OnQueryTextListener, View.OnLongClickListener {

    private static final String MY_SORT_PREF = "sortOrder";
    private RecyclerView recyclerView;
    private String name;
    private ArrayList<VideoModel> videoModelArrayList = new ArrayList<>();
    ArrayList<VideoModel> selectionArrayList = new ArrayList<>();
    int count = 0;
    private VideosAdapter videosAdapter;
    private boolean isBackPressed = false;
    public boolean is_selectable = false;
    TextView countText;

    Toolbar toolbar;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public VideoFolder() {
        // Required empty public constructor
    }

    public static VideoFolder newInstance(String param1, String param2) {
        VideoFolder fragment = new VideoFolder();
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
            name = getArguments().getString("folderName");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_video_folder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.videoFolder_recycleview);
        countText = view.findViewById(R.id.counter_textView);
        toolbar = view.findViewById(R.id.videoFolderToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!is_selectable){
                    isBackPressed = true;
                    Navigation.findNavController(requireView()).navigate(R.id.action_videoFolder_to_videoFragment);
                }else{
                    clearSelectingToolbar();
                    videosAdapter.notifyDataSetChanged();
                }
            }
        });

        int index = name.lastIndexOf("/");
        String onlyFolderName = name.substring(index+1);
        countText.setText(onlyFolderName);
        loadVideos();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!is_selectable){
                isBackPressed = true;
                Navigation.findNavController(requireView()).navigate(R.id.action_videoFolder_to_videoFragment);
            }else{
                clearSelectingToolbar();
                videosAdapter.notifyDataSetChanged();
            }
            return true;
        } else {
            SharedPreferences.Editor editor = ((AppCompatActivity) getActivity()).getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE).edit();
            switch (item.getItemId()) {
                case R.id.sort_by_date: {
                    editor.putString("sorting", "sortByDate");
                    editor.apply();
                    loadVideos(); // Обновляем список видео
                    return true;
                }
                case R.id.sort_by_name: {
                    editor.putString("sorting", "sortByName");
                    editor.apply();
                    loadVideos(); // Обновляем список видео
                    return true;
                }
                case R.id.sort_by_size: {
                    editor.putString("sorting", "sortBySize");
                    editor.apply();
                    loadVideos(); // Обновляем список видео
                    return true;
                }
                case R.id.add_to_playlistdir: {
                    refresh();
                    break;
                }
            }
        }
        return super.onOptionsItemSelected(item);
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

    // ...




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        ImageView imageView = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.themecolor), PorterDuff.Mode.SRC_IN);
        searchView.setQueryHint("Search file name");
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        ArrayList<VideoModel> searchList = new ArrayList<>();
        for(VideoModel model : videoModelArrayList){
            if(model.getTitle().toLowerCase().contains(input)){
                searchList.add(model);
            }
        }
        videosAdapter.updateSearchList(searchList);

        return false;
    }

    private void loadVideos() {//Если ошибка будет в этой функции, то вместо getContext(), getActivity()
        videoModelArrayList = getallVideoFromFolder(getContext(), name);
        if (name != null && videoModelArrayList.size() > 0) {
            videosAdapter = new VideosAdapter(videoModelArrayList, getContext(), this);
            recyclerView.setAdapter(videosAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        } else {
            Toast.makeText(getContext(), "can't find any videos", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<VideoModel> getallVideoFromFolder(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(MY_SORT_PREF,Context.MODE_PRIVATE);
        String sort = preferences.getString("sorting", "sortByName");
        String order = null;
        switch (sort){
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME+ " ASC";
                break;
            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED+ " ASC";
                break;
            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE+ " DESC";
                break;
        }
        ArrayList<VideoModel> list = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION
        };
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + name + "%"};//Ниче не пон
        //Здесь был context, а не getContext
        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, order);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String path = cursor.getString(1);
                    String title = cursor.getString(2);
                    int size = cursor.getInt(3);
                    String resolution = cursor.getString(4);
                    int duration = cursor.getInt(5);
                    String disName = cursor.getString(6);
                    String bucket_display_name = cursor.getString(7);
                    String width_heigh = cursor.getString(8);
                    String human_can_read = null;
                    if (size < 1024) {
                        human_can_read = String.format(context.getString(R.string.size_in_b), (double) size);
                    } else if (size < Math.pow(1024, 2)) {
                        human_can_read = String.format(context.getString(R.string.size_in_kb), (double) (size / 1024));
                    } else if (size < Math.pow(1024, 3)) {
                        human_can_read = String.format(context.getString(R.string.size_in_mb), (double) (size / Math.pow(1024, 2)));
                    } else {
                        human_can_read = String.format(context.getString(R.string.size_in_gb), (double) (size / Math.pow(1024, 3)));
                    }

                    String duration_formatted;
                    int sec = (duration/1000)%60;
                    int min = (duration/(1000*60))%60;
                    int hrs = duration/(1000 * 60 * 60);

                    if (hrs == 0){
                        duration_formatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d",sec)));
                    }else{
                        duration_formatted = String.valueOf(hrs).concat(":".concat(String.format(Locale.UK, "%02d",min)
                                .concat(":".concat(String.format(Locale.UK, "%02d",sec)))));
                    }
                    //До этого момента всё ок
                    VideoModel files = new VideoModel(id,path, title, human_can_read, resolution,
                            duration_formatted,disName,width_heigh);

                    if (name.endsWith(bucket_display_name))
                        list.add(files);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    private void refresh(){
        if(name != null && videoModelArrayList.size()>0){
            videoModelArrayList.clear();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadVideos();
                    videosAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                }
            },1500);
        }else{
            Toast.makeText(getContext(), "folder is empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.item_selected_menu);
        is_selectable = true;
        videosAdapter.notifyDataSetChanged();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
        return true;
    }

    public void prepareSelection(View v, int position) {
        if( ((CheckBox)v).isChecked()){
            selectionArrayList.add(videoModelArrayList.get(position));
            count++;
            updateCount(count);
        }else{
            selectionArrayList.remove(videoModelArrayList.get(position));
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
        toolbar.inflateMenu(R.menu.main_toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
        int index = name.lastIndexOf("/");
        String onlyFolderName = name.substring(index+1);
        countText.setText(onlyFolderName);
        count = 0;
        selectionArrayList.clear();
    }


}