package com.acabias.digoreste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MenuApp extends AppCompatActivity {

    private Toolbar mToolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Digoreste");
        mToolbar.setSubtitle("Quiz de Física Ambiental");
        setSupportActionBar(mToolbar);

        sharedPreferences = getSharedPreferences(getString(R.string.logado), Context.MODE_PRIVATE);
        String userNome = sharedPreferences.getString("user-nome", "");
        TextView tv1 = (TextView)findViewById(R.id.textView1);
        tv1.setText(userNome);

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_exit){
            //Limpando
            sharedPreferences = getSharedPreferences(getString(R.string.logado), Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.remove(getString(R.string.logado));
            editor.apply();

            startActivity(new Intent(this, Login.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void pergunta(View view) {
        Intent intent = new Intent(this, Pergunta.class);
        intent.putExtra("modo-jogo", "treino");
        startActivity(intent);
        finish();
    }

    public void inscrever(View view) {
        Intent intent = new Intent(this, Inscrever.class);
        startActivity(intent);
    }

    public void minhasTurmas(View view) {
        Intent intent = new Intent(this, MinhasTurmas.class);
        startActivity(intent);
    }
}
