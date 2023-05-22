package com.example.hypermedia2.Video;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hypermedia2.R;

import java.util.ArrayList;

public class VideoFragment extends Fragment {


    private ArrayList<String> folderList = new ArrayList<>();
    private ArrayList<VideoModel> videoList = new ArrayList<>();
    VideoFolderAdapter videoFolderAdapter;
    RecyclerView recyclerView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VideoFragment() {
        // Required empty public constructor
    }

    private ArrayList<VideoModel> fetchAllVideos(Context context){
        ArrayList<VideoModel> videoModels = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,null, null, orderBy);
        if (cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String path = cursor.getString(1);
                    String title = cursor.getString(2);
                    String size = cursor.getString(3);
                    String resolution = cursor.getString(4);
                    String duration = cursor.getString(5);
                    String disname = cursor.getString(6);
                    String width_heigh = cursor.getString(7);

                    VideoModel videoFiles = new VideoModel(id, path, title, size, resolution, duration, disname, width_heigh);

                    int slashFirstIndex = path.lastIndexOf("/");
                    String subString = path.substring(0, slashFirstIndex);

                    if (!folderList.contains(subString)) {
                        folderList.add(subString);
                    }
                    videoModels.add(videoFiles);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return videoModels;
    }

    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
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
        View videoView = inflater.inflate(R.layout.fragment_video,container,false);
        recyclerView = videoView.findViewById(R.id.video_recycle_view);
        videoList = fetchAllVideos(getActivity());
        if(folderList != null && folderList.size()>0 && videoList != null){
            videoFolderAdapter = new VideoFolderAdapter(folderList, videoList, getActivity());
            recyclerView.setAdapter(videoFolderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        }else{
            Toast.makeText(getActivity(), "can't find any videos folder", Toast.LENGTH_SHORT).show();
        }
        return videoView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}