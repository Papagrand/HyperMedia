package com.example.hypermedia2;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class Music implements Parcelable {
    private String musicName;
    private String artistName;
    private int idArtist;
    private int imgSongResource;
    private int durationSong;
    private String path;


    public Music(int idArtist,String musicName, String artistName, int imgSongResource, int durationSong, String path) {
        this.idArtist = idArtist;
        this.musicName = musicName;
        this.artistName = artistName;
        this.imgSongResource = imgSongResource;
        this.durationSong = durationSong;
        this.path = path;
    }

    protected Music(Parcel in) {
        idArtist = in.readInt();
        musicName = in.readString();
        artistName = in.readString();
        imgSongResource = in.readInt();
        durationSong = in.readInt();
        path = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public String getPath() {
        return path;
    }

    public String getMusicName() {
        return musicName;
    }


    public String getArtistName() {
        return artistName;
    }

    public int getIdArtist() {
        return idArtist;
    }

    public int getImgSongResource() {
        return imgSongResource;
    }

    public int getDurationSong() {
        return durationSong;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(idArtist);
        dest.writeString(musicName);
        dest.writeString(artistName);
        dest.writeInt(imgSongResource);
        dest.writeInt(durationSong);
        dest.writeString(path);
    }
}
