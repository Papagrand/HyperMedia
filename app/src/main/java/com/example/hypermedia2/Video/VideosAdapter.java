package com.example.hypermedia2.Video;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hypermedia2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyVideoHolder> {

    private ArrayList<VideoModel> videoFolder = new ArrayList<>();
    private Context context;

    public VideosAdapter(ArrayList<VideoModel> videoFolder, Context context) {
        this.videoFolder = videoFolder;
        this.context = context;
    }


    @NonNull
    @Override
    public MyVideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videofiles_view, parent, false);
        return new  MyVideoHolder(view);
    }
    //проблема здесь
    @Override
    public void onBindViewHolder(@NonNull MyVideoHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.video_img)
                .error(R.drawable.video_img);
        Glide.with(context).load(videoFolder.get(position).getPath()).apply(options).into(holder.thumbnail);
        holder.title.setText(videoFolder.get(position).getTitle());
        holder.duration.setText(videoFolder.get(position).getDuration());
        holder.size.setText(videoFolder.get(position).getSize());
        holder.resolution.setText(videoFolder.get(position).getResolution());
        holder.menu.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogThemeVideoMenu);
            View bottomSheetVideoView = LayoutInflater.from(context).inflate(R.layout.videofile_menu, null);
            bottomSheetDialog.setContentView(bottomSheetVideoView);
            bottomSheetDialog.show();

            bottomSheetVideoView.findViewById(R.id.videomenu_share).setOnClickListener(v1 -> {
                shareFile(position);
            });

            bottomSheetVideoView.findViewById(R.id.videomenu_rename).setOnClickListener(v2 -> {

            });

            bottomSheetVideoView.findViewById(R.id.videomenu_delete).setOnClickListener(v3 -> {
                deleteFile(holder.getPosition(),v);
            });
            bottomSheetVideoView.findViewById(R.id.videomenu_properties).setOnClickListener(v4 -> {
                showProperties(position);
            });
        });

    }


    private void shareFile(int position){
        Uri uri = Uri.parse(videoFolder.get(position).getPath());
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, "share"));
        Toast.makeText(context, "loading...", Toast.LENGTH_SHORT).show();
    }

    private void renameFiles(int position, View view){

    }

    private void deleteFile(int position, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("удалить")
                .setMessage(videoFolder.get(position).getTitle())
                .setNegativeButton("отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton("да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(videoFolder.get(position).getId()));
                        File file = new File(videoFolder.get(position).getPath());

                        if (deleteFile2(file)){
                            context.getApplicationContext().getContentResolver().delete(contentUri,
                                    null,null);
                            videoFolder.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,videoFolder.size());
                            Snackbar.make(view, "Файл удален", Snackbar.LENGTH_SHORT).show();
                        }else {
                            Snackbar.make(view, "Файл не удален", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    private boolean deleteFile2(File file) {
        boolean isDeleted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            String selection = MediaStore.Files.FileColumns.RELATIVE_PATH + "=?";
            String[] selectionArgs = new String[]{Environment.DIRECTORY_MOVIES + "/" + file.getName()};

            int rowsDeleted = contentResolver.delete(uri, selection, selectionArgs);
            isDeleted = rowsDeleted > 0;
        } else {
            isDeleted = file.delete();
        }
        System.out.println(isDeleted);
        return isDeleted;
    }
    private void showProperties(int p){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.file_properties);

        String name = videoFolder.get(p).getTitle();
        String path = videoFolder.get(p).getPath();
        String size = videoFolder.get(p).getSize();
        String duration = videoFolder.get(p).getDuration();
        String resolution = videoFolder.get(p).getResolution();

        TextView tit = dialog.findViewById(R.id.pro_title);
        TextView st = dialog.findViewById(R.id.pro_storage);
        TextView siz = dialog.findViewById(R.id.pro_size);
        TextView dur = dialog.findViewById(R.id.pro_duration);
        TextView res = dialog.findViewById(R.id.pro_resolution);

        tit.setText(name);
        st.setText(path);
        siz.setText(size);
        dur.setText(duration);
        res.setText(resolution+"p");

        dialog.show();

    }
    @Override
    public int getItemCount() {
        return videoFolder.size();
    }

    public void updateSearchList(ArrayList<VideoModel> searchList) {
        videoFolder = new ArrayList<>();
        videoFolder.addAll(searchList);
        notifyDataSetChanged();

    }

    public class MyVideoHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail, menu;
        TextView title, size, duration, resolution;

        public MyVideoHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.video_title);
            size = itemView.findViewById(R.id.video_size);
            duration = itemView.findViewById(R.id.video_duration);
            resolution = itemView.findViewById(R.id.video_quality);
            menu = itemView.findViewById(R.id.video_menu);
        }
    }
}
