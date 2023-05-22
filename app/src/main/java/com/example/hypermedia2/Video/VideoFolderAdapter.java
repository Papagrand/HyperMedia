package com.example.hypermedia2.Video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypermedia2.R;

import java.util.ArrayList;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.MyViewHolder> {

    private ArrayList<String> folderName;
    private ArrayList<VideoModel> videoModels;
    private Context context;

    public VideoFolderAdapter(ArrayList<String> folderName, ArrayList<VideoModel> videoModels, Context context) {
        this.folderName = folderName;
        this.videoModels = videoModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int index = folderName.get(position).lastIndexOf("/");
        String folderNames = folderName.get(position).substring(index+1);

        holder.name.setText(folderNames);
        holder.countVideos.setText(String.valueOf(countVideos(folderName.get(position))));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("folderName", folderName.get(holder.getPosition()));
                Navigation.findNavController(v).navigate(R.id.action_videoFragment_to_videoFolder, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, countVideos;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.folderName);
            countVideos = itemView.findViewById(R.id.videosCount);
        }
    }


    int countVideos(String folders){
        int count = 0;
        for (VideoModel model: videoModels){
            if(model.getPath().substring(0, model.getPath().lastIndexOf("/")).endsWith(folders)){
                count++;
            }
        }
        return count;
    }
}
