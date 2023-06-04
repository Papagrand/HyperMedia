package com.example.hypermedia2.LoginThings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hypermedia2.ChildModeThings.ForChildModeActivity;
import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.MainActivity;
import com.example.hypermedia2.R;

public class MainLoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    ImageButton button1,button2,button3,button4,button5,
            button6,button7,button8,button9,button0, backspace;
    TextView mainText, forgotText;
    EditText editText;
    LinearLayout goToChildModeLayout;
    String firstTry="";


    public MainLoginFragment() {
        // Required empty public constructor
    }

    public static MainLoginFragment newInstance(String param1, String param2) {
        MainLoginFragment fragment = new MainLoginFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button0=view.findViewById(R.id.login_button_0);
        button1=view.findViewById(R.id.login_button_1);
        button2=view.findViewById(R.id.login_button_2);
        button3=view.findViewById(R.id.login_button_3);
        button4=view.findViewById(R.id.login_button_4);
        button5=view.findViewById(R.id.login_button_5);
        button6=view.findViewById(R.id.login_button_6);
        button7=view.findViewById(R.id.login_button_7);
        button8=view.findViewById(R.id.login_button_8);
        button9=view.findViewById(R.id.login_button_9);
        backspace=view.findViewById(R.id.backspace_login);
        editText = view.findViewById(R.id.pin_code_edittext_login);
        mainText =view.findViewById(R.id.mainText_login);
        forgotText = view.findViewById(R.id.text_forgot_password);
        goToChildModeLayout = view.findViewById(R.id.goToChildModeFromLogin);

        goToChildModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForChildModeActivity.class);
                startActivity(intent);
            }
        });

        // Установка обработчиков нажатий на кнопки
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("0",v);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("1",v);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("2",v);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("3",v);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("4",v);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("5",v);
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("6",v);
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("7",v);
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("8",v);
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEditText("9",v);
            }
        });

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastCharacter();
            }
        });
        forgotText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_mainLoginFragment_to_forgotFragment);
            }
        });

    }
    private void updateEditText(String digit, View view) {
        String currentText = editText.getText().toString();

        if (currentText.length() < 4) {
            editText.setText(currentText + digit);

            if (editText.length() == 4) {
                // Введено 4 символа, сохраняем в firstTry и очищаем EditText
                if (firstTry=="") {
                    firstTry = editText.getText().toString();
                    DBaseHelper dBaseHelper = new DBaseHelper(getContext());
                    dBaseHelper.openDataBase();
                    String[] passSecret = dBaseHelper.checkUserRegistration();
                    if (passSecret!=null){
                        if (firstTry.equals(passSecret[0])){
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }else{
                            mainText.setText("Введен неверный пароль, повторите попытку");
                            editText.setText("");
                            firstTry = "";
                        }
                    }
                    dBaseHelper.close();
                }
            }
        }
    }


    private void removeLastCharacter() {
        String currentText = editText.getText().toString();

        if (!currentText.isEmpty()) {
            editText.setText(currentText.substring(0, currentText.length() - 1));
        }
    }
}