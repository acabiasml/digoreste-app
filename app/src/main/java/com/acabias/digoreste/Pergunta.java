package com.acabias.digoreste;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Pergunta extends AppCompatActivity {

    private static String TAG = "LOG";
    private Toolbar mToolbar;
    private Toolbar mToolbarBottom;
    private MediaPlayer ring;
    private SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    private TextView enunciado, pontuacao, apresentadas, erros, pulos;
    private Button opcao1, opcao2, opcao3, opcao4, opcao5;
    private String opcert1, opcert2, opcert3, opcert4, opcert5, dicaPergunta;
    private int pontos, apresenta, erro, pulo;
    ArrayList<String> listaPerguntas;
    int contaPergunta;
    ProgressBar carregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pergunta);
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        pontos = 0;
        apresenta = 0;
        erro = 0;
        pulo = 0;

        pontuacao = findViewById(R.id.textView5);
        apresentadas  = findViewById(R.id.textView14);
        erros = findViewById(R.id.textView16);
        pulos = findViewById(R.id.textView20);

        listaPerguntas = new ArrayList<>();
        contaPergunta = 0;

        carregando = (ProgressBar)findViewById(R.id.progressBar);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Digoreste");
        mToolbar.setSubtitle("Quiz de Física Ambiental");
        setSupportActionBar(mToolbar);

        ring = MediaPlayer.create(Pergunta.this, R.raw.audio);
        ring.start();
        ring.setLooping(true);

        sharedPreferences = getSharedPreferences(getString(R.string.logado), Context.MODE_PRIVATE);
        String userNome = sharedPreferences.getString("user-nome", "");

        TextView tv1 = (TextView)findViewById(R.id.textView1);
        tv1.setText(userNome);

        String modoJogo = getIntent().getStringExtra("modo-jogo");
        if(modoJogo.contains("treino")){
            TextView tv9 = (TextView)findViewById(R.id.textView9);
            tv9.setText("Modo Treino");
        }

        enunciado = (TextView)findViewById(R.id.textView11);
        opcao1 = (Button)findViewById(R.id.button4);
        opcao2 = (Button)findViewById(R.id.button5);
        opcao3 = (Button)findViewById(R.id.button6);
        opcao4 = (Button)findViewById(R.id.button9);
        opcao5 = (Button)findViewById(R.id.button10);

        botoesInvisiveis();
        geraPergunta();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_second_activity){
            startActivity(new Intent(this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void geraPergunta() {

        carregando.setVisibility(View.VISIBLE);

        String url = "http://digoreste.ic.ufmt.br/site/consultas/pergunta.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, respostaPergunta(), getPostErrorListener()) {
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                }
            };
            stringRequest.setTag(REQ_TAG);
            requestQueue.add(stringRequest);
    }

    private Response.Listener<String> respostaPergunta() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respostaDoServidor = new JSONObject(response);

                    String status = respostaDoServidor.getString("status").trim();
                    JSONObject pergunta = new JSONArray(respostaDoServidor.getString("mensagem")).getJSONObject(0);

                    if (status.contains("ok")) {

                        if(listaPerguntas.contains(pergunta.getString("id"))){
                            contaPergunta = contaPergunta + 1;
                            if(contaPergunta >= 50){
                                listaPerguntas.removeAll(listaPerguntas);
                                contaPergunta = 0;
                            }
                            geraPergunta();
                        }else{

                            if(contaPergunta > 0){
                                contaPergunta = 0;
                            }

                            listaPerguntas.add(pergunta.getString("id"));

                            apresenta = apresenta + 1;
                            apresentadas.setText(String.valueOf(apresenta));

                            enunciado.setText(pergunta.getString("descricao"));

                            if(TextUtils.isEmpty(pergunta.getString("dica"))){
                                dicaPergunta = "Sem dica cadastrada para esta pergunta";
                            }else{
                                dicaPergunta = pergunta.getString("dica");
                            }

                            JSONArray respostas = new JSONArray(pergunta.getString("opcoes"));

                            for(int i = 0; i < respostas.length(); i++){

                                JSONObject opc = new JSONObject(respostas.get(i).toString());

                                switch (i) {
                                    case 0:
                                        opcert1 = opc.getString("correta");
                                        opcao1.setText(opc.getString("descricao"));
                                        opcao1.setVisibility(View.VISIBLE);
                                        break;
                                    case 1:
                                        opcert2 = opc.getString("correta");
                                        opcao2.setText(opc.getString("descricao"));
                                        opcao2.setVisibility(View.VISIBLE);
                                        break;
                                    case 2:
                                        opcert3 = opc.getString("correta");
                                        opcao3.setText(opc.getString("descricao"));
                                        opcao3.setVisibility(View.VISIBLE);
                                        break;
                                    case 3:
                                        opcert4 = opc.getString("correta");
                                        opcao4.setText(opc.getString("descricao"));
                                        opcao4.setVisibility(View.VISIBLE);
                                        break;
                                    case 4:
                                        opcert5 = opc.getString("correta");
                                        opcao5.setText(opc.getString("descricao"));
                                        opcao5.setVisibility(View.VISIBLE);
                                        break;
                                }
                            }

                            carregando.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Toast.makeText(Pergunta.this, "Erro: " + respostaDoServidor.getString("mensagem"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Pergunta.this, "Erro ao fazer POST", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void botoesInvisiveis(){
        opcert1 = "";
        opcert2 = "";
        opcert3 = "";
        opcert4 = "";
        opcert5 = "";
        opcao1.setVisibility(View.GONE);
        opcao2.setVisibility(View.GONE);
        opcao3.setVisibility(View.GONE);
        opcao4.setVisibility(View.GONE);
        opcao5.setVisibility(View.GONE);
    }

    public void clicouPular(View view){
        pulo = pulo + 1;
        pulos.setText(String.valueOf(pulo));

        proxima(view);
    }

    public void proxima(View view){
        botoesInvisiveis();
        geraPergunta();
    }

    public void dica(View view){
        Toast.makeText(Pergunta.this, dicaPergunta, Toast.LENGTH_SHORT).show();
    }

    public void respondendo(View view){

        int daique = 0;

        switch(view.getId()){
            case R.id.button4:
                if(opcert1.contains("s")){
                    daique = 1;
                }
                break;
            case R.id.button5:
                if(opcert2.contains("s")){
                    daique = 1;
                }
                break;
            case R.id.button6:
                if(opcert3.contains("s")){
                    daique = 1;
                }
                break;
            case R.id.button9:
                if(opcert4.contains("s")){
                    daique = 1;
                }
                break;
            case R.id.button10:
                if(opcert5.contains("s")){
                    daique = 1;
                }
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }

        acertou(view, daique);
        proxima(view);
    }

    public void acertou(View view, int resposta){

        ImageView image = new ImageView(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        if(resposta == 1){
            pontos = pontos + 1;
            pontuacao.setText(String.valueOf(pontos));
            builder.setTitle("Bom trabalho");
            builder.setMessage("Você acertou! =D");
            image.setImageResource(R.drawable.acertou);
        }else{
            erro = erro + 1;
            erros.setText(String.valueOf(erro));
            builder.setTitle("Eita");
            builder.setMessage("Você errou :(");
            image.setImageResource(R.drawable.errou);
        }

        builder.setView(image);
        builder.setCancelable(true);

        final AlertDialog dlg = builder.create();

        dlg.show();

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss();
                t.cancel();
            }
        }, 2000);
    }
}
