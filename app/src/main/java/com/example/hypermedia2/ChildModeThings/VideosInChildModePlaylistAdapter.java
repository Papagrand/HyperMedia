package com.example.hypermedia2.ChildModeThings;

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
import com.example.hypermedia2.Home.VideosInPlaylistAdapter;
import com.example.hypermedia2.Home.VideosInPlaylistFragment;
import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoModel;
import com.example.hypermedia2.VideoPlayerActivity;

import java.util.ArrayList;

public class VideosInChildModePlaylistAdapter extends RecyclerView.Adapter<VideosInChildModePlaylistAdapter.VideosInChildModePlaylistHolder>{

    public static ArrayList<VideoModel> videosForChild = new ArrayList<>();
    private Context context;
    VideosInChildModePlaylistFragment videosInChildModePlaylistFragment;

    public VideosInChildModePlaylistAdapter(ArrayList<VideoModel> videosForChild,Context context, VideosInChildModePlaylistFragment videosInChildModePlaylistFragment) {
        this.videosForChild = videosForChild;
        this.context = context;
        this.videosInChildModePlaylistFragment = videosInChildModePlaylistFragment;
    }

    @NonNull
    @Override
    public VideosInChildModePlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videofiles_view, parent, false);
        return new VideosInChildModePlaylistHolder(view, videosInChildModePlaylistFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosInChildModePlaylistHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.video_img)
                .error(R.drawable.video_img);
        Glide.with(context).load(videosForChild.get(position).getPath()).apply(options).into(holder.thumbnail);
        holder.title.setText(videosForChild.get(position).getTitle());
        holder.duration.setText(videosForChild.get(position).getDuration());
        holder.size.setText(videosForChild.get(position).getSize());
        holder.resolution.setText(videosForChild.get(position).getResolution());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("p", holder.getPosition());
                intent.putExtra("video_title", videosForChild.get(holder.getPosition()).getTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (videosForChild!=null) {
            return videosForChild.size();
        }
        return 0;
    }
    public void updateSearchList(ArrayList<VideoModel> searchList) {
        videosForChild = new ArrayList<>();
        videosForChild.addAll(searchList);
        notifyDataSetChanged();

    }

    public class VideosInChildModePlaylistHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, menu;
        TextView title, size, duration, resolution;
        VideosInChildModePlaylistFragment videosInPlaylistFragment;

        public VideosInChildModePlaylistHolder(@NonNull View itemView, VideosInChildModePlaylistFragment videosInPlaylistFragment) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.video_title);
            size = itemView.findViewById(R.id.video_size);
            duration = itemView.findViewById(R.id.video_duration);
            resolution = itemView.findViewById(R.id.video_quality);
            menu = itemView.findViewById(R.id.video_menu);
            this.videosInPlaylistFragment = videosInPlaylistFragment;
        }

    }
}
