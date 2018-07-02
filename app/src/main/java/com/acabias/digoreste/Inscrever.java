package com.acabias.digoreste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.layout.simple_spinner_item;

public class Inscrever extends AppCompatActivity {

    private Toolbar mToolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    private Spinner dropLista;
    private EditText campoSenha;
    private ArrayAdapter<String> adapter;
    private java.util.ArrayList<String> strings;
    private java.util.ArrayList<String> indices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscrever);
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        sharedPreferences = getSharedPreferences(getString(R.string.logado), Context.MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Digoreste");
        mToolbar.setSubtitle("Quiz de Física Ambiental");
        setSupportActionBar(mToolbar);

        campoSenha = (EditText)findViewById(R.id.editText4);
        dropLista = (Spinner)findViewById(R.id.spinner);
        buscarListaDeTurmas();
    }

    //menu superior
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }


    //botão de sair
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_exit){
            editor = sharedPreferences.edit();
            editor.remove(getString(R.string.logado));
            editor.apply();

            startActivity(new Intent(this, Login.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void voltar(View view) {
        onBackPressed();
        finish();
    }

    public void buscarListaDeTurmas() {

        String url = "http://digoreste.ic.ufmt.br/site/consultas/turm-list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, listarTurmasExistentes(), getPostErrorListener()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        stringRequest.setTag(REQ_TAG);
        requestQueue.add(stringRequest);
    }

    private Response.Listener<String> listarTurmasExistentes() {
        return new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respostaDoServidor = new JSONObject(response);

                    String status = respostaDoServidor.getString("status").trim();

                    if (status.contains("ok")) {

                        JSONArray mensagem = respostaDoServidor.getJSONArray("mensagem");

                        strings = new java.util.ArrayList<>();
                        indices = new java.util.ArrayList<>();

                        for(int i = 0; i < mensagem.length(); i++){
                            strings.add(mensagem.getJSONObject(i).getString("descricao"));
                            indices.add(mensagem.getJSONObject(i).getString("id"));
                        }

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(Inscrever.this, android.R.layout.simple_spinner_item, strings);

                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dropLista.setAdapter(spinnerAdapter);

                    } else {
                        Toast.makeText(Inscrever.this, "Erro: " + respostaDoServidor.getString("mensagem"), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener getPostErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Inscrever.this, "Erro ao fazer POST", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void prepararMatricula(View view){

        String turma = null, senha = null, usuario = null;

        String aux = dropLista.getSelectedItem().toString();

        for(int i = 0; i < strings.size(); i++){
            if(aux.contains(strings.get(i))){
                turma = indices.get(i);
            }
        }

        senha = campoSenha.getText().toString();

        usuario = sharedPreferences.getString("user-id", "");

        tentarMatricula(turma, senha, usuario);
    }

    public void tentarMatricula(final String turma, final String senha, final String usuario) {

        String url = "http://digoreste.ic.ufmt.br/site/consultas/matr-inse.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, respostaTentativaMatricula(), getPostErrorListener()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("turma", turma);
                params.put("senha", senha);
                params.put("usuario", usuario);

                return params;
            }
        };
        stringRequest.setTag(REQ_TAG);
        requestQueue.add(stringRequest);
    }

    private Response.Listener<String> respostaTentativaMatricula() {
        return new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respostaDoServidor = new JSONObject(response);

                    String status = respostaDoServidor.getString("status").trim();

                    if (status.contains("ok")) {

                        Toast.makeText(Inscrever.this, "Matrícula efetuada com sucesso!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();

                    } else {
                        Toast.makeText(Inscrever.this, "Não foi possível matricular. Verifique a senha digitada.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
