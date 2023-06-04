package com.example.hypermedia2.ChildModeThings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.Home.HomeFragment;
import com.example.hypermedia2.Home.PlaylistAdapter;
import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoModel;

import java.util.ArrayList;

public class ChildModePlaylistAdapter extends RecyclerView.Adapter<ChildModePlaylistAdapter.ChildModePlaylistViewHolder> {
    private ArrayList<String> playlistName;
    ChildMode childModeFragment;
    DBaseHelper dBaseHelper;
    private Context context;

    public ChildModePlaylistAdapter(ArrayList<String> playlistName, Context context, ChildMode childModeFragment) {
        this.playlistName = playlistName;
        this.context = context;
        this.childModeFragment = childModeFragment;
    }

    @NonNull
    @Override
    public ChildModePlaylistAdapter.ChildModePlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_view, parent, false);
        return new ChildModePlaylistAdapter.ChildModePlaylistViewHolder(view, childModeFragment);
    }


    @Override
    public void onBindViewHolder(@NonNull ChildModePlaylistViewHolder holder, int position) {

            int index = playlistName.get(position).lastIndexOf("/");
            String folderName = playlistName.get(position).substring(index+1);
            holder.name.setText(folderName);
            holder.countVideos.setText("");


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("playlistName", playlistName.get(holder.getPosition()));
                    Navigation.findNavController(view).navigate(R.id.action_childMode_to_videosInChildModePlaylistFragment, bundle);
                }
            });

    }

    @Override
    public int getItemCount() {
        return playlistName.size();
    }

    public void updateData(ArrayList<String> folderList) {
        this.playlistName = folderList;
        notifyDataSetChanged();

    }


    public class ChildModePlaylistViewHolder extends RecyclerView.ViewHolder{
        TextView name, countVideos;
        ChildMode childModeFragment;
        ImageView kidsControll;


        public ChildModePlaylistViewHolder(@NonNull View itemView, ChildMode childModeFragment) {
            super(itemView);
            name = itemView.findViewById(R.id.folderName);
            countVideos = itemView.findViewById(R.id.videosCount);
            kidsControll = itemView.findViewById(R.id.kids_indicator);
            this.childModeFragment = childModeFragment;

        }

    }

}
