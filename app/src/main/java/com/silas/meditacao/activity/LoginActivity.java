package com.silas.meditacao.activity;

import org.apache.http.Header;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.CPBCliente;
import com.silas.meditacao.io.Extracao;
import com.silas.meditacao.io.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

public class LoginActivity extends ActionBarActivity {

    final CPBCliente client = CPBCliente.getInstace(this);

    EditText etCaptcha, etEmail, etSenha;
    ImageView ivCaptcha;
    ProgressBar pb;
    TextView tvCriar,tvEsqueceu;
    String recaptchaChallengeField = "";
    String recaptchaResponseField = "";
    String urlImagem, email, senha;
    SharedPreferences _sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

            ivCaptcha = (ImageView) findViewById(R.id.ivCaptcha);
            ivCaptcha.setVisibility(View.GONE);

            pb = (ProgressBar) findViewById(R.id.progressBar);

            etCaptcha = (EditText) findViewById(R.id.etCaptcha);
            etCaptcha.requestFocus();
            etEmail = (EditText) findViewById(R.id.etEmail);
            etSenha = (EditText) findViewById(R.id.etSenha);
            etSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());

            tvCriar = (TextView) findViewById(R.id.tvCriar);
            tvCriar.setMovementMethod(LinkMovementMethod.getInstance());
            tvEsqueceu = (TextView) findViewById(R.id.tvEsqueceu);
            tvEsqueceu.setMovementMethod(LinkMovementMethod.getInstance());

        if (internetDisponivel(getApplicationContext())) {
            conecta();

            _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    email = _sharedPreferences.getString("email", "");
                    senha = _sharedPreferences.getString("senha", "");
                    etEmail.setText(email);
                    etSenha.setText(senha);
                }
            };

            _sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

            email = _sharedPreferences.getString("email", "");
            senha = _sharedPreferences.getString("senha", "");

            if (!email.matches("")) {
                etEmail.setText(email);
            }
            if (!senha.matches("")) {
                etSenha.setText(senha);
            }

            final Button button = (Button) findViewById(R.id.bEntrar);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    recaptchaResponseField = etCaptcha.getText().toString();
                    if(!etEmail.getText().toString().matches("")
                            && !etSenha.getText().toString().matches("")
                            && !recaptchaResponseField.matches("")) {
                        Log.i("cliquei", recaptchaResponseField);

                        SharedPreferences.Editor editor = _sharedPreferences.edit();
    //                    Log.d("pmail", email);
                        if(email.matches("")) {
                            email = etEmail.getText().toString();
                            editor.putString("email", email);
                            editor.commit();

                        }

    //                    Log.d("ppass", senha);
                        if(senha.matches("")) {
                            senha = etSenha.getText().toString();
                            editor.putString("senha", senha);
                            editor.commit();
                        }

                        fazLogin();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Todos os campos são necessários!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            pb.setVisibility(View.GONE);
            TextView tvDica = (TextView) findViewById(R.id.tvDica);
            tvDica.setText("Sem internet!!!");
            Toast.makeText(getApplicationContext(),"Sem internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void conecta() {
        cookies();
        recaptcha();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_refresh:
                recaptcha();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }


    private void recaptcha() {
        etCaptcha.requestFocus();
        ivCaptcha.setVisibility(View.VISIBLE);

        etCaptcha.setText("");
        String url = null;
        try {
            url = "http://www.google.com/recaptcha/api/noscript?RecaptchaOptions="
                    + URLEncoder.encode("{theme:red,lang:pt,tabindex:2}", "utf-8") + "&k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Log.i("url ", url);
        client.get(url,
                null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Erro ao capturar captcha!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                       /* for (Header h : headers) {
                            System.out.println(h.getName() + " " + h.getValue());

                        }*/
                        Document html = Jsoup.parse(s);
                        Element challenge = html.select("input#recaptcha_challenge_field").first();
                        recaptchaChallengeField = challenge.attr("value");

                        Element imagem = html.select("img").first();
                        setUrlImagem("http://www.google.com/recaptcha/api/" + imagem.attr("src"));
//                        Log.i("r", getUrlImagem());


                        new Util().imagemDownload(getUrlImagem(), ivCaptcha);

                        pb.setVisibility(View.GONE);
                    }
                });

    }

    private void fazLogin() {
        ivCaptcha.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        params.add("email", email);
//        Log.i("email", email);
//        Log.i("senha",senha);
        params.add("senha", senha);
        params.add("recaptcha_challenge_field", recaptchaChallengeField);
        params.add("recaptcha_response_field", recaptchaResponseField);
        client.post("http://cpbmais.cpb.com.br/login/includes/autenticate.php",
                params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                        Toast.makeText(getApplicationContext(), "Ops! Deu zica! na senha ou email\n" +
                                "Tente novamente", Toast.LENGTH_SHORT).show();

                        recaptcha();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Calendar c = Calendar.getInstance();
                        int iAno = c.get(Calendar.YEAR);
                        String sAno = String.valueOf(iAno);
                        String url = "http://cpbmais.cpb.com.br/htdocs/periodicos/medmat/" + sAno + "/frmd" + sAno + ".php";
//                        Log.w(getClass().getSimpleName(),url);
                        client.get(url,
                                null, new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                                        Toast.makeText(getApplicationContext(), "ihh! Você errou!", Toast.LENGTH_SHORT).show();

                                        recaptcha();
                                    }

                                    @Override
                                    public void onSuccess(int i, Header[] headers, String s) {



                                        Extracao e = new Extracao(getApplicationContext());
                                        if(e.ePaginaMeditacao(s)) {
                                            e.extraiMeditacao(s);
                                            pb.setVisibility(View.GONE);
                                            startActivity(new Intent(getApplicationContext(), DiaMeditacaoActivity.class));
                                        }
                                        else {
                                            client.cancelAllRequests(true);
                                            Toast.makeText(getApplicationContext(), "Erro!", Toast.LENGTH_SHORT).show();
                                            conecta();
                                        }
                                    }
                                });
                    }
                });
    }

    private void cookies() {
        client.get("http://cpbmais.cpb.com.br/login/index.php", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
//                for (Header h : headers) {
//                    System.out.println(h.getName() + " " + h.getValue());
//
//                }
                /*for (Cookie c : myCookieStore.getCookies()) {
                    System.out.println(c.getName() + " " + c.getValue());
                }*/
//                Log.d("cookies", s );
            }
        });
    }

    public Boolean internetDisponivel(Context con) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
//                Log.i("TestaInternet", "Está conectado.");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.i("TestaInternet", "Não está conectado.");
        return false;
    }
}