package com.example.hypermedia2.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoFolder;
import com.example.hypermedia2.Video.VideoModel;
import com.example.hypermedia2.VideoPlayerActivity;

import java.util.ArrayList;

public class VideosInPlaylistAdapter extends RecyclerView.Adapter<VideosInPlaylistAdapter.VidInPlaylistHolder> {

    public static ArrayList<VideoModel> playlistNewVideos = new ArrayList<>();
    private Context context;
    VideosInPlaylistFragment videosInPlaylistFragment;

    public VideosInPlaylistAdapter(ArrayList<VideoModel> playlistNewVideos,Context context, VideosInPlaylistFragment videosInPlaylistFragment) {
        this.playlistNewVideos = playlistNewVideos;
        this.context = context;
        this.videosInPlaylistFragment = videosInPlaylistFragment;
    }

    @NonNull
    @Override
    public VidInPlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videofiles_view, parent, false);
        return new VidInPlaylistHolder(view, videosInPlaylistFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosInPlaylistAdapter.VidInPlaylistHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.video_img)
                .error(R.drawable.video_img);
        Glide.with(context).load(playlistNewVideos.get(position).getPath()).apply(options).into(holder.thumbnail);
        holder.title.setText(playlistNewVideos.get(position).getTitle());
        holder.duration.setText(playlistNewVideos.get(position).getDuration());
        holder.size.setText(playlistNewVideos.get(position).getSize());
        holder.resolution.setText(playlistNewVideos.get(position).getResolution());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("p", holder.getPosition());
                intent.putExtra("video_title", playlistNewVideos.get(holder.getPosition()).getTitle());
                context.startActivity(intent);
            }
        });


        if(videosInPlaylistFragment.is_selectable){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.menu.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }else{
            holder.checkBox.setVisibility(View.GONE);
            holder.menu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (playlistNewVideos!=null) {
            return playlistNewVideos.size();
        }
        return 0;
    }
    public void updateSearchList(ArrayList<VideoModel> searchList) {
        playlistNewVideos = new ArrayList<>();
        playlistNewVideos.addAll(searchList);
        notifyDataSetChanged();

    }

    public class VidInPlaylistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnail, menu;
        TextView title, size, duration, resolution;
        CheckBox checkBox;
        VideosInPlaylistFragment videosInPlaylistFragment;

        public VidInPlaylistHolder(@NonNull View itemView, VideosInPlaylistFragment videosInPlaylistFragment) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.video_title);
            size = itemView.findViewById(R.id.video_size);
            duration = itemView.findViewById(R.id.video_duration);
            resolution = itemView.findViewById(R.id.video_quality);
            menu = itemView.findViewById(R.id.video_menu);
            checkBox = itemView.findViewById(R.id.video_folder_checkbox);
            this.videosInPlaylistFragment = videosInPlaylistFragment;

            itemView.setOnLongClickListener(videosInPlaylistFragment);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            videosInPlaylistFragment.prepareSelection(view,getAdapterPosition());
        }
    }
}
