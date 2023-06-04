package com.example.hypermedia2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hypermedia2.Home.Playlist;
import com.example.hypermedia2.Video.VideoModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/com.example.hypermedia2/databases/";
    private static final String DATABASE_NAME = "Videos.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_VIDEO_ID = "id";
    private static final String TABLE_VIDEO_PATH = "path";
    private static final String TABLE_VIDEO_TITLE = "title";
    private static final String TABLE_VIDEO_SIZE = "size";
    private static final String TABLE_VIDEO_RESOLUTION = "resolution";
    private static final String TABLE_VIDEO_DURATION = "duration";
    private static final String TABLE_VIDEO_DISPLAYNAME = "displayName";
    private static final String TABLE_VIDEO_WH = "wh";

    private static final String TABLE_PLAYLIST_ID = "playlist_id";
    private static final String TABLE_PLAYLIST_NAME = "playlist_name";


    public Context context;
    static SQLiteDatabase sqliteDataBase;

    public DBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void createDataBase() throws IOException {
        //check if the database exists
        boolean databaseExist = checkDataBase();

        if (databaseExist) {
            // Do Nothing.
        } else {
            this.getWritableDatabase();
            copyDataBase();
        }// end if else dbExist
    } // end createDataBase().

    public boolean checkDataBase() {
        String path = DB_PATH + DATABASE_NAME;
        File databaseFile = new File(DB_PATH + DATABASE_NAME);
        return databaseFile.exists();
    }

    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        sqliteDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (sqliteDataBase != null)
            sqliteDataBase.close();
        super.close();
    }

    @SuppressLint("Range")
    public ArrayList<VideoModel> getAllPlaylists(){
        ArrayList<VideoModel> playlists = new ArrayList<>();
        @SuppressLint("Range") Cursor cursorVideo = sqliteDataBase.query("Videos", null, null,
                null, null, null, null);
        while(cursorVideo.moveToNext()) {
            playlists.add(new VideoModel(cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_ID)), cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_PATH)),
                    cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_TITLE)), cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_SIZE)),
                    cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_RESOLUTION)), cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_DURATION)),
                    cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_DISPLAYNAME)), cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_WH))));
        }
        cursorVideo.close();
        return playlists;

    }
    public String getPlaylistForKidsStatus(String playlistName) {
        String status = "";

        String[] projection = { "playlist_kids" };
        String selection = "playlist_name = ?";
        String[] selectionArgs = { playlistName };
        Cursor cursor = sqliteDataBase.query("VideoPlaylist", projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("playlist_kids");
            status = cursor.getString(columnIndex);
            cursor.close();
        }

        return status;
    }

    public void makeParentControllDatabase(String password, String secretWord){
        ContentValues cv = new ContentValues();
        cv.put("parent_password",password);
        cv.put("parent_secretword",secretWord);
        sqliteDataBase.insert("ParentControll", null, cv);
    }

    @SuppressLint("Range")
    public String[] checkUserRegistration() {
        String[] userInfo = new String[2];
        String[] columns = {"parent_password", "parent_secretword"};
        Cursor cursor = sqliteDataBase.query("ParentControll", columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            userInfo[0] = cursor.getString(cursor.getColumnIndex("parent_password"));
            userInfo[1] = cursor.getString(cursor.getColumnIndex("parent_secretword"));
        } else {
            userInfo = null; // Если пользователь не зарегистрирован, возвращаем null
        }

        cursor.close();
        return userInfo;
    }

    public void makePlaylistDatabase(String PlaylistName){
        ContentValues cv = new ContentValues();
        cv.put("playlist_name",PlaylistName);
        cv.put("playlist_kids","no");
        sqliteDataBase.insert("VideoPlaylist", null, cv);
    }
    public void makePlaylistForKidsInDatabase(String PlaylistName, String kidsControll){
        ContentValues cv = new ContentValues();
        if (kidsControll.equals("no")) {
            cv.put("playlist_kids", "yes");
        }else {
            cv.put("playlist_kids", "no");
        }
        String whereClause = "playlist_name = ?";
        String[] whereArgs = { PlaylistName };
        sqliteDataBase.update("VideoPlaylist", cv, whereClause, whereArgs);
    }
    public void writeVideoToPlaylistDatabase(VideoModel selectedVideo, String PlaylistName) {
        writeVideoToDatabase(selectedVideo);
        ContentValues cv= new ContentValues();
        cv.put("video_ID", selectedVideo.getId());
        cv.put("playlist_path", PlaylistName);
        sqliteDataBase.insert("VideosInPlaylists", null, cv);
    }
    public void writeVideoToDatabase(VideoModel selectedVideo) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_VIDEO_ID, selectedVideo.getId());
        cv.put(TABLE_VIDEO_PATH, selectedVideo.getPath());
        cv.put(TABLE_VIDEO_TITLE, selectedVideo.getTitle());
        cv.put(TABLE_VIDEO_SIZE, selectedVideo.getSize());
        cv.put(TABLE_VIDEO_RESOLUTION, selectedVideo.getResolution());
        cv.put(TABLE_VIDEO_DURATION, selectedVideo.getDuration());
        cv.put(TABLE_VIDEO_DISPLAYNAME, selectedVideo.getDisplayName());
        cv.put(TABLE_VIDEO_WH, selectedVideo.getWh());
        sqliteDataBase.insert("Videos", null, cv);
    }
    public void deleteVideoFromDatabase(VideoModel selectedVideo, String PlaylistName){
        sqliteDataBase.delete("VideosInPlaylists", "video_ID = ? AND playlist_path = ?", new String[]{String.valueOf(selectedVideo.getId()), PlaylistName});
    }
    @SuppressLint("Range")
    public ArrayList<VideoModel> getVideosFromDataBase(String PlaylistName){
        ArrayList<VideoModel> videos = new ArrayList<>();
        Cursor cursorVideoId = sqliteDataBase.query("VideosInPlaylists",null,"playlist_path = ?",
                new String[]{PlaylistName},null,null,null);
        while (cursorVideoId.moveToNext()){
            int videoId=cursorVideoId.getInt(0);
            @SuppressLint("Range") Cursor cursorVideo = sqliteDataBase.query("Videos", null, "id = ?",
                    new String[]{String.valueOf(videoId)}, null, null, null);
            while(cursorVideo.moveToNext()) {
                videos.add(new VideoModel(cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_ID)), cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_PATH)),
                        cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_TITLE)), cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_SIZE)),
                        cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_RESOLUTION)), cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_DURATION)),
                        cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_DISPLAYNAME)), cursorVideo.getString(cursorVideo.getColumnIndex(TABLE_VIDEO_WH))));
            }
            cursorVideo.close();
        }
        cursorVideoId.close();
        return videos;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
