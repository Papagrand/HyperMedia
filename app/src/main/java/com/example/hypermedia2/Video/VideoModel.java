package com.example.hypermedia2.Video;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class VideoModel implements Parcelable {
    private boolean isSelected;

    String id;
    String path;
    String title;
    String size;
    String resolution;
    String duration;
    String displayName;
    String wh;

    protected VideoModel(Parcel in) {
        id = in.readString();
        path = in.readString();
        title = in.readString();
        size = in.readString();
        resolution = in.readString();
        duration = in.readString();
        displayName = in.readString();
        wh = in.readString();
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWh() {
        return wh;
    }

    public void setWh(String wh) {
        this.wh = wh;
    }

    public VideoModel(String id, String path, String title, String size, String resolution, String duration, String displayName, String wh) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.size = size;
        this.resolution = resolution;
        this.duration = duration;
        this.displayName = displayName;
        this.wh = wh;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(path);
        parcel.writeString(title);
        parcel.writeString(size);
        parcel.writeString(resolution);
        parcel.writeString(duration);
        parcel.writeString(displayName);
        parcel.writeString(wh);
    }
}
