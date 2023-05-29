package com.example.hypermedia2.Home;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.hypermedia2.Video.VideoModel;

import java.util.ArrayList;

public class Playlist implements Parcelable {
    int id;
    String type;
    String name;
    ArrayList<VideoModel> videos;

    public Playlist(int id, String name,String type, ArrayList<VideoModel> videos){
        this.id = id;
        this.type = type;
        this.name = name;
        this.videos = videos;
    }

    protected Playlist(Parcel in) {
        id = in.readInt();
        type = in.readString();
        name = in.readString();
        videos = in.createTypedArrayList(VideoModel.CREATOR);
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<VideoModel> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<VideoModel> videos) {
        this.videos = videos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(type);
        parcel.writeString(name);
        parcel.writeTypedList(videos);
    }
}
