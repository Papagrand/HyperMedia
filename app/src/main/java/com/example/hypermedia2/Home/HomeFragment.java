package com.example.hypermedia2.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.ChildModeThings.ChildModeDialogFragment;
import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.ChildModeThings.ForChildModeActivity;
import com.example.hypermedia2.OrganizationOfParentControll.OrganizationActivity;
import com.example.hypermedia2.R;
import com.example.hypermedia2.Video.VideoModel;

import java.io.File;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements PlaylistDialog.PlaylistDialogListener, View.OnLongClickListener {

    private static String FILE_NAME;

    private ArrayList<String> folderList = new ArrayList<>();
    ArrayList<String> selectionFolderList = new ArrayList<>();
    private ArrayList<VideoModel> selectedVideosArrayList;
    PlaylistAdapter playlistAdapter;
    RecyclerView recyclerView;
    int count = 0;
    public boolean is_selectable = false;
    private boolean isBackPressed = false;
    TextView countText;
    ImageButton buttonDelete, buttonParentControll, cancelChanges, parentControll;
    ImageView kidsIndecator;
    LinearLayout goToChildMode;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2, ArrayList<VideoModel> selectedVideosArrayList) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putParcelableArrayList("selectionArrayList", selectedVideosArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            selectedVideosArrayList = getArguments().getParcelableArrayList("selectedList");

        }
    }
    private void displayFolders() {
        String parentPath = getActivity().getExternalFilesDir(null).getAbsolutePath();
        File parentFolder = new File(parentPath);
        File[] subFolders = parentFolder.listFiles(File::isDirectory);

        if (subFolders != null) {
            folderList.clear();
            for (File folder : subFolders) {
                folderList.add(folder.getAbsolutePath());
            }
        }
        playlistAdapter.updateData(folderList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View playlistView = inflater.inflate(R.layout.fragment_home, container, false);


        return playlistView;
    }

    @Override
    public void onResume() {
        displayFolders();
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.home_recycle_view);
//        DBaseHelper dBaseHelper = new DBaseHelper(getContext());
//        dBaseHelper.openDataBase();
//        playlistAdapter = new PlaylistAdapter(folderList, getActivity(), dBaseHelper.getAllPlaylists());
        playlistAdapter = new PlaylistAdapter(folderList, getActivity(), selectedVideosArrayList,this);
        recyclerView.setAdapter(playlistAdapter);
        ImageButton addPlaylistImageButton= view.findViewById(R.id.button_add_playlist);
        buttonDelete = view.findViewById(R.id.from_homefragment_delete);
        buttonParentControll = view.findViewById(R.id.add_delete_parent_controll);
        cancelChanges = view.findViewById(R.id.cancel_changes);
        parentControll = view.findViewById(R.id.add_delete_parent_controll);
        kidsIndecator = view.findViewById(R.id.kids_indicator);
        goToChildMode = view.findViewById(R.id.goToChildMode);
        goToChildMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBaseHelper dBaseHelper = new DBaseHelper(getContext());
                dBaseHelper.openDataBase();
                if (dBaseHelper.checkUserRegistration()==null) {
                    openDialogParentControll();
                } else {
                    Intent intent = new Intent(getActivity(), ForChildModeActivity.class);
                    startActivity(intent);
                }
                dBaseHelper.close();
            }
        });
        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelChanges.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
                buttonParentControll.setVisibility(View.GONE);
                clearSelectingToolbar();
                playlistAdapter.notifyDataSetChanged();
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Подтверждение удаления")
                        .setMessage("Вы уверены, что хотите удалить выбранные папки?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Пользователь нажал "Да"
                                performDeletePlaylistFolders();
                                cancelChanges.setVisibility(View.GONE);
                                buttonDelete.setVisibility(View.GONE);
                                buttonParentControll.setVisibility(View.GONE);
                                playlistAdapter.notifyDataSetChanged();
                                clearSelectingToolbar();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Пользователь нажал "Отмена"
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        parentControll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBaseHelper dBaseHelper = new DBaseHelper(getContext());
                dBaseHelper.openDataBase();
                for (String folder: selectionFolderList){
                    String isFolderForKids = dBaseHelper.getPlaylistForKidsStatus(folder);
                    dBaseHelper.makePlaylistForKidsInDatabase(folder, isFolderForKids);
                }
                dBaseHelper.close();
                cancelChanges.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
                buttonParentControll.setVisibility(View.GONE);
                clearSelectingToolbar();
                playlistAdapter.notifyDataSetChanged();
            }
        });



        addPlaylistImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAdd();
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void openDialogAdd() {
        PlaylistDialog playlistDialog = new PlaylistDialog();
        playlistDialog.setListener(this);
        playlistDialog.show(getActivity().getSupportFragmentManager(), "playlist dialog");
    }
    private void openDialogParentControll(){
        ChildModeDialogFragment childModeDialogFragment = new ChildModeDialogFragment();
        childModeDialogFragment.setListener(this);
        childModeDialogFragment.show(getActivity().getSupportFragmentManager(), "childcontroll dialog");
    }

    public void createPlaylistFolder(String playlistName) {
        File dir = new File(getActivity().getExternalFilesDir(null), playlistName);
        DBaseHelper dBaseHelper = new DBaseHelper(getContext());
        dBaseHelper.openDataBase();
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Toast.makeText(getActivity(), "Папка плейлиста создана", Toast.LENGTH_SHORT).show();
                dBaseHelper.makePlaylistDatabase((dir).toString());
                displayFolders(); // Обновить список папок
            } else {
                Toast.makeText(getActivity(), "Ошибка при создании папки плейлиста", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Папка плейлиста уже существует", Toast.LENGTH_SHORT).show();
        }
        dBaseHelper.close();
    }


    private void performDeletePlaylistFolders() {
        for (String folderName : selectionFolderList) {
            File folder = new File(folderName);
            if (folder.exists()) {
                deleteRecursive(folder);
            }
        }
        // Очистить список выбранных папок
        selectionFolderList.clear();
        Toast.makeText(getActivity(), "Выбранные папки удалены", Toast.LENGTH_SHORT).show();
        displayFolders(); // Обновить список папок
    }


    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }



    @Override
    public void applyParams(String playlistName) {
        createPlaylistFolder(playlistName);
    }

    @Override
    public boolean onLongClick(View v) {
        is_selectable = true;
        cancelChanges.setVisibility(View.VISIBLE);
        buttonDelete.setVisibility(View.VISIBLE);
        buttonParentControll.setVisibility(View.VISIBLE);
        playlistAdapter.notifyDataSetChanged();
        return true;
    }


    public void prepareSelection(View v, int position) {
        if( ((CheckBox)v).isChecked()){
            selectionFolderList.add(folderList.get(position));
        }
    }
    private void clearSelectingToolbar(){
        is_selectable = false;
        selectionFolderList.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        isBackPressed = false;
        clearSelectingToolbar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearSelectingToolbar();
        isBackPressed = false;
    }
}