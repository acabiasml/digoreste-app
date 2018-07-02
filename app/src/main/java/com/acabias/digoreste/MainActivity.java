package com.acabias.digoreste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //temporizador
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {

                sharedPreferences = getSharedPreferences(getString(R.string.logado), Context.MODE_PRIVATE);
                String result = sharedPreferences.getString(getString(R.string.logado), "");

                if(result == ""){
                    mostrarLogin();
                }else{
                    mostrarMenu();
                }
            }
        }, 3500);

    }

    private void mostrarLogin() {
        Intent intent = new Intent(MainActivity.this,
                Login.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMenu() {
        Intent intent = new Intent(MainActivity.this,
                MenuApp.class);
        startActivity(intent);
        finish();
    }
}
