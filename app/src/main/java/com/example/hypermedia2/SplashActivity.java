package com.example.hypermedia2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.hypermedia2.LoginThings.LoginActivity;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBaseHelper dBaseHelper = new DBaseHelper(this);
        dBaseHelper = new DBaseHelper(this);
        try {
            dBaseHelper.createDataBase();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        super.onCreate(savedInstanceState);
        dBaseHelper.openDataBase();
        if (dBaseHelper.checkUserRegistration()!=null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        dBaseHelper.close();
    }
}