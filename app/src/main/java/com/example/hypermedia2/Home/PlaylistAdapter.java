package com.example.hypermedia2.Home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoModel;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private ArrayList<String> playlistName;
    private ArrayList<VideoModel> playlistNewVideos;
    HomeFragment homeFragment;
    DBaseHelper dBaseHelper;
    private Context context;

    public PlaylistAdapter(ArrayList<String> playlistName, Context context,ArrayList<VideoModel> playlistNewVideos, HomeFragment homeFragment) {
        this.playlistName = playlistName;
        this.context = context;
        this.playlistNewVideos = playlistNewVideos;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_view, parent, false);
        return new PlaylistViewHolder(view, homeFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        int index = playlistName.get(position).lastIndexOf("/");
        String folderName = playlistName.get(position).substring(index+1);
        holder.name.setText(folderName);
        dBaseHelper = new DBaseHelper(context);
        dBaseHelper.openDataBase();
        String isFolderForKids = dBaseHelper.getPlaylistForKidsStatus(playlistName.get(position));
        if (isFolderForKids.equals("yes")) {
            holder.kidsControll.setVisibility(View.VISIBLE);
        } else {
            holder.kidsControll.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("playlistName", playlistName.get(holder.getPosition()));
                if(playlistNewVideos != null){
                    bundle.putParcelableArrayList("selectedList", playlistNewVideos);
                }
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_videosInPlaylistFragment, bundle);
            }
        });


        if(homeFragment.is_selectable){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }else{
            holder.checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return playlistName.size();
    }

    public void updateData(ArrayList<String> folderList) {
        this.playlistName = folderList;
        notifyDataSetChanged();

    }


    public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, countVideos;
        CheckBox checkBox;
        HomeFragment homeFragment;
        ImageView kidsControll;


        public PlaylistViewHolder(@NonNull View itemView, HomeFragment homeFragment) {
            super(itemView);
            name = itemView.findViewById(R.id.folderName);
            countVideos = itemView.findViewById(R.id.videosCount);
            checkBox = itemView.findViewById(R.id.playlist_folder_checkbox);
            kidsControll = itemView.findViewById(R.id.kids_indicator);
            this.homeFragment = homeFragment;

            itemView.setOnLongClickListener(homeFragment);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            homeFragment.prepareSelection(v,getAdapterPosition());
        }
    }

}
