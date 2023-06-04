package com.example.hypermedia2.OrganizationOfParentControll;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.R;

public class CreatingPasswordFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageButton button1,button2,button3,button4,button5,
            button6,button7,button8,button9,button0, backspace;
    TextView mainText;
    EditText editText;
    public static String firstTry="";
    public static String secondTry="";


    private String mParam1;
    private String mParam2;

    public CreatingPasswordFragment() {
        // Required empty public constructor
    }

    public static CreatingPasswordFragment newInstance(String param1, String param2) {
        CreatingPasswordFragment fragment = new CreatingPasswordFragment();
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
        return inflater.inflate(R.layout.fragment_creating_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button0=view.findViewById(R.id.creating_button_0);
        button1=view.findViewById(R.id.creating_button_1);
        button2=view.findViewById(R.id.creating_button_2);
        button3=view.findViewById(R.id.creating_button_3);
        button4=view.findViewById(R.id.creating_button_4);
        button5=view.findViewById(R.id.creating_button_5);
        button6=view.findViewById(R.id.creating_button_6);
        button7=view.findViewById(R.id.creating_button_7);
        button8=view.findViewById(R.id.creating_button_8);
        button9=view.findViewById(R.id.creating_button_9);
        backspace=view.findViewById(R.id.backspace);
        editText = view.findViewById(R.id.pin_code_edittext);
        mainText =view.findViewById(R.id.mainText_creating_password);



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

        // ...

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastCharacter();
            }
        });
    }

    // Метод для обновления EditText при нажатии на кнопку
    private void updateEditText(String digit, View view) {
        String currentText = editText.getText().toString();

        if (currentText.length() < 4) {
            editText.setText(currentText + digit);

            if (editText.length() == 4) {
                // Введено 4 символа, сохраняем в firstTry и очищаем EditText
                if (firstTry.isEmpty()) {
                    firstTry = editText.getText().toString();
                    mainText.setText("Повторите последний введенный код-пароль");
                    editText.setText("");
                } else {
                    secondTry = editText.getText().toString();
                    if (!firstTry.equals(secondTry)) {
                        // firstTry и secondTry не совпадают, повторить попытку
                        firstTry = "";
                        secondTry = "";
                        editText.setText("");
                        mainText.setText("Введен неверный пароль. Попробуйте еще раз");
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("password", firstTry);
                        Navigation.findNavController(view).navigate(R.id.action_creatingPasswordFragment_to_creatingSecretwordFragment, bundle);
                    }
                }
            }
        }
    }

    // Метод для удаления последнего символа из EditText
    private void removeLastCharacter() {
        String currentText = editText.getText().toString();

        if (!currentText.isEmpty()) {
            editText.setText(currentText.substring(0, currentText.length() - 1));
        }
    }
}