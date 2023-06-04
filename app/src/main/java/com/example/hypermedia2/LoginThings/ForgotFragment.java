package com.example.hypermedia2.LoginThings;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hypermedia2.DBaseHelper;
import com.example.hypermedia2.R;

public class ForgotFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ImageButton buttonback;
    TextView mainText;
    EditText editText;
    Button pushPassword;

    private String mParam1;
    private String mParam2;

    public ForgotFragment() {
        // Required empty public constructor
    }

    public static ForgotFragment newInstance(String param1, String param2) {
        ForgotFragment fragment = new ForgotFragment();
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
        return inflater.inflate(R.layout.fragment_forgot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonback = view.findViewById(R.id.back_to_login);
        mainText = view.findViewById(R.id.mainText_forgot_password);
        editText = view.findViewById(R.id.edit_secretword_forgot);
        pushPassword = view.findViewById(R.id.btn_recover_pass);
        buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_forgotFragment_to_mainLoginFragment);

            }
        });
        pushPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String secretWord = editText.getText().toString().trim();
                DBaseHelper dBaseHelper = new DBaseHelper(getActivity());
                dBaseHelper.openDataBase();
                String[] passSecret = dBaseHelper.checkUserRegistration();

                if (secretWord.isEmpty()) {
                    Toast.makeText(getActivity(), "Вы не ввели секретное слово для восстановления", Toast.LENGTH_SHORT).show();
                }else if (!secretWord.equals(passSecret[1])){
                    mainText.setText("Введено неверное слово для восстановления");
                    mainText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
                else {
                    Toast.makeText(getActivity(), "Ваш код доступа: "+passSecret[0], Toast.LENGTH_SHORT).show();
                }
                dBaseHelper.close();
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireView()).navigate(R.id.action_forgotFragment_to_mainLoginFragment);
            }
        });
    }
}