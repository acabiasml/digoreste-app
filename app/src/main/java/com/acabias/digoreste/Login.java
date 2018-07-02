package com.acabias.digoreste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class Login extends AppCompatActivity {

    RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static final String REQ_TAG = "VACTIVITY";
    private Toolbar mToolbar;
    private Toolbar mToolbarBottom;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Digoreste");
        mToolbar.setSubtitle("Quiz de Física Ambiental");
        setSupportActionBar(mToolbar);

        mToolbarBottom = (Toolbar) findViewById(R.id.inc_tb_bottom);
        mToolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent it = null;

                switch (menuItem.getItemId()) {
                    case R.id.action_facebook:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("https://www.facebook.com/ppgfa.ufmt/"));
                        break;
                    case R.id.action_youtube:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("https://www.youtube.com/channel/UCVBdB3kbOef0ig4dhiqdpdQ"));
                        break;
                }

                startActivity(it);
                return true;
            }
        });
        mToolbarBottom.inflateMenu(R.menu.menu_bottom);

        mToolbarBottom.findViewById(R.id.iv_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setData(Uri.parse("http://digoreste.ic.ufmt.br/"));
                startActivity(it);
                //Toast.makeText(Login.this, "Buscando por atualizações...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_second_activity) {
            startActivity(new Intent(this, Pergunta.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void abrirMenu(View view) throws IOException {

        escondeTeclado();

        EditText mEdit = (EditText) findViewById(R.id.editText);
        EditText mEdit2 = (EditText) findViewById(R.id.editText2);

        final String email = mEdit.getText().toString().trim();
        final String senha = mEdit2.getText().toString().trim();

        if (email.matches("") || senha.matches("")) {
            Toast.makeText(Login.this, "Você precisa informar usuário e senha.", Toast.LENGTH_SHORT).show();
        } else {

            String url = "http://digoreste.ic.ufmt.br/login.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, respostaLogin(view), getPostErrorListener()) {
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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

    private Response.Listener<String> respostaLogin(View view) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respostaDoServidor = new JSONObject(response);
                    JSONObject mensagem = respostaDoServidor.getJSONObject("mensagem");

                    String status = respostaDoServidor.getString("status").trim();

                    if (status.contains("ok")) {
                        //LOGOU!!
                        sharedPreferences = getSharedPreferences(getString(R.string.logado), Context.MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.logado), "sim");
                        editor.putString("user-id", mensagem.getString("id"));
                        editor.putString("user-nome", mensagem.getString("nome"));
                        editor.putString("user-perfil", mensagem.getString("perfil_id"));
                        editor.apply();

                        Intent intent = new Intent(Login.this, MenuApp.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Combinação e-mail e senha não encontrados. Status: " + status, Toast.LENGTH_SHORT).show();
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
                Log.e("VOLLEY", error.getMessage());
                Toast.makeText(Login.this, "Erro ao fazer POST", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void abrirRegistro(View view) throws IOException {
        Intent intent = new Intent(Login.this, Registrar.class);
        startActivity(intent);
    }
}
