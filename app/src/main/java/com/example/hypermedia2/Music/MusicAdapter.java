package com.example.hypermedia2.Music;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypermedia2.R;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    ImageButton playPausePlayer, playPauseMiniPlayer;
    Context context;
    Resources resources;
    ArrayList<Music> musicArrayList;
    private int selectedPosition = -1;
    private int lastSelectedPosition = -1;

    public interface MusicAdapterListener{
        void onPlayerExpand(ArrayList<Music> musicArrayList);
    }
    private  MusicAdapterListener playerExpandListener;
    public void setMusicAdapterListener(MusicAdapterListener listener){
        this.playerExpandListener = listener;
    }


    public void setMusicList(ArrayList<Music> musicList) {
        this.musicArrayList = musicList;
        notifyDataSetChanged();
    }
    public MusicAdapter(Context context) {
        this.context = context;
        this.musicArrayList = new ArrayList<>();
        this.resources = context.getResources();
    }


    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.music_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.textViewSong.setText(musicArrayList.get(position).getMusicName());
        holder.textViewArtist.setText(musicArrayList.get(position).getArtistName());
        holder.imageViewSong.setImageResource(musicArrayList.get(position).getImgSongResource());
        holder.durationSong.setText(PlayerFragment.convertToMMSS(String.valueOf(musicArrayList.get(position).getDurationSong())));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectedPosition == holder.getAdapterPosition()){
                    if (MyMediaPlayer.getInstance().isPlaying()){
                        MyMediaPlayer.getInstance().pause();
                    }else{
                        MyMediaPlayer.getInstance().start();
                    }
                }else{
                    //Запуск bottomSheet с плеером
                    PlayerFragment playerBottomSheetFragment = new PlayerFragment(musicArrayList);
                    MyMediaPlayer.getInstance().reset();
                    playerBottomSheetFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), playerBottomSheetFragment.getTag());
                }
                int previousSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
                MyMediaPlayer.currentIndex = selectedPosition;
                lastSelectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
        if (selectedPosition == position) {
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.my_backgroundselectedcolor));
        } else {
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.my_backgroundcolor));
        }
    }
    public ArrayList<Music> getMusicArrayList() {
        return musicArrayList;
    }

    public ArrayList<Music> getMusicArrayList(Context context) {
        musicArrayList = new ArrayList<>();
        String[] projection = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA};
        String selection = MediaStore.Audio.Media.DURATION + " >= ?";
        String[] selectionArgs = {String.valueOf(1000)};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC ";
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri,projection,selection,selectionArgs,sortOrder);
        if(cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String musicName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                int durationSong = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String pathSong = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int imgSongResource = R.drawable.img;
                Music music = new Music(id, musicName, artistName, imgSongResource, durationSong,pathSong);
                musicArrayList.add(music);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return musicArrayList;
    }

    @Override
    public int getItemCount() {
        return musicArrayList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView textViewArtist;
        TextView textViewSong;
        TextView durationSong;
        ImageView imageViewSong;

        View view;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewArtist = itemView.findViewById(R.id.artistTextView);
            textViewSong = itemView.findViewById(R.id.songNameTextView);
            imageViewSong = itemView.findViewById(R.id.imageSongView);
            durationSong = itemView.findViewById(R.id.durationSongId);
            view = itemView;
        }
    }
}
