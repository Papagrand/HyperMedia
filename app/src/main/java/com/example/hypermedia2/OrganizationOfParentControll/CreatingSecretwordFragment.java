package com.example.hypermedia2.OrganizationOfParentControll;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.ChildModeThings.ForChildModeActivity;
import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.LoginThings.LoginActivity;
import com.example.hypermedia2.R;

public class CreatingSecretwordFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String password;
    ImageButton buttonback;
    TextView mainText;
    EditText editText;
    Button saveSettings;

    private String mParam1;
    private String mParam2;

    public CreatingSecretwordFragment() {
        // Required empty public constructor
    }


    public static CreatingSecretwordFragment newInstance(String param1, String param2) {
        CreatingSecretwordFragment fragment = new CreatingSecretwordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            password = getArguments().getString("password");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_creating_secretword, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonback = view.findViewById(R.id.back_to_pin);
        mainText = view.findViewById(R.id.mainText_creating_secretword);
        editText = view.findViewById(R.id.edit_secretword);
        saveSettings = view.findViewById(R.id.btn_save);
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String secretWord = editText.getText().toString().trim();

                if (secretWord.isEmpty()) {
                    Toast.makeText(getActivity(), "Вы не ввели секретное слово для восстановления", Toast.LENGTH_SHORT).show();
                }
                else if (secretWord.matches("[a-zA-Z]+") && !secretWord.contains(" ")) {
                    DBaseHelper dBaseHelper = new DBaseHelper(getActivity());
                    dBaseHelper.openDataBase();
                    dBaseHelper.makeParentControllDatabase(password,editText.getText().toString());
                    dBaseHelper.close();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    mainText.setText("Слово не должно содержать цифр, символов и пробелов");
                    mainText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainText.setText("Введите секретное слово для восстановления доступа к полной версии");
                            mainText.setTextColor(getResources().getColor(android.R.color.white));
                        }
                    }, 7000); // Задержка в миллисекундах (7 секунд = 7000 миллисекунд)
                }
            }
        });

    }
}