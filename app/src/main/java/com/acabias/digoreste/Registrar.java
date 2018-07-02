package com.acabias.digoreste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Registrar extends AppCompatActivity {

    RequestQueue requestQueue;
    private Toolbar mToolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static final String REQ_TAG = "VACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar);
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Digoreste");
        mToolbar.setSubtitle("Quiz de Física Ambiental");
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void pergunta(View view) {
        Intent intent = new Intent(this, Pergunta.class);
        startActivity(intent);
    }

    public void iniciarRegistro(View view) throws IOException {

        escondeTeclado();

        EditText mEdit = (EditText) findViewById(R.id.editText5);
        EditText mEdit2 = (EditText) findViewById(R.id.editText6);
        EditText mEdit3 = (EditText) findViewById(R.id.editText8);

        final String nome = mEdit.getText().toString().trim();
        final String email = mEdit2.getText().toString().trim();
        final String senha = mEdit3.getText().toString().trim();

        if (email.matches("") || senha.matches("") || nome.matches("")) {
            Toast.makeText(Registrar.this, "Você precisa informar nome, usuário e senha.", Toast.LENGTH_SHORT).show();
        } else {

            String url = "http://digoreste.ic.ufmt.br/registrar.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, respostaRegistro(view), getPostErrorListener()) {
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nome", nome);
                    params.put("email", email);
                    params.put("senha", senha);
                    return params;
                }

                ;
            };
            stringRequest.setTag(REQ_TAG);
            requestQueue.add(stringRequest);
        }
    }

    private void escondeTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Response.Listener<String> respostaRegistro(View view) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respostaDoServidor = new JSONObject(response);

                    String status = respostaDoServidor.getString("status").trim();

                    if (status.contains("ok")) {
                        Toast.makeText(Registrar.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Registrar.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Registrar.this, "Erro: " + respostaDoServidor.getString("mensagem"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Registrar.this, "Erro ao fazer POST", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
