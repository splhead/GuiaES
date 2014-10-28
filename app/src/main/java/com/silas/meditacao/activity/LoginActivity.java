package com.silas.meditacao.activity;

import org.apache.http.Header;

import org.apache.http.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
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
    String recaptchaChallengeField = "";
    String recaptchaResponseField = "";
    String urlImagem, email, senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etCaptcha = (EditText) findViewById(R.id.etCaptcha);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etSenha = (EditText) findViewById(R.id.etSenha);

        conecta();

    }

    private void conecta() {

        etSenha.setTransformationMethod(new PasswordTransformationMethod());
        SharedPreferences _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        email = _sharedPreferences.getString("email", "");
        senha = _sharedPreferences.getString("senha", "");

        //Log.d("sp", email + "\n" + senha);
        if (email.length() > 0) etEmail.setText(email);
        if (senha.length() > 0) etSenha.setText(senha);
        if (email.isEmpty() || senha.isEmpty()
                || !email.equalsIgnoreCase(etEmail.getText().toString())
                || !senha.equalsIgnoreCase(etSenha.getText().toString())) {
            _sharedPreferences.edit().putString("email", etEmail.getText().toString());
            _sharedPreferences.edit().putString("senha", etSenha.getText().toString()).apply();
        }
        cookies();
        recaptcha();

        final Button button = (Button) findViewById(R.id.bEntrar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                recaptchaResponseField = etCaptcha.getText().toString();
                Log.d("cliquei", recaptchaResponseField);
                if (recaptchaResponseField.length() > 0)
                    fazLogin(); //teste();

            }
        });
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
        etCaptcha.setText("");
        String url = null;
        try {
            url = "http://www.google.com/recaptcha/api/noscript?RecaptchaOptions="
                    + URLEncoder.encode("{theme:red,lang:pt,tabindex:2}", "utf-8") + "&k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("url ", url);
        client.get(url,
                null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        Toast toast = Toast.makeText(getBaseContext(), "Erro ao capturar captcha!", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        for (Header h : headers) {
                            System.out.println(h.getName() + " " + h.getValue());

                        }
                        Document html = Jsoup.parse(s);
                        Element challenge = html.select("input#recaptcha_challenge_field").first();
                        recaptchaChallengeField = challenge.attr("value");

                        Element imagem = html.select("img").first();
                        setUrlImagem("http://www.google.com/recaptcha/api/" + imagem.attr("src"));
                        Log.d("r", getUrlImagem());

                        final ImageView ivCaptcha = (ImageView) findViewById(R.id.ivCaptcha);
                        new Util().imagemDownload(getUrlImagem(), ivCaptcha);
                    }
                });

    }

    private void fazLogin() {
        RequestParams params = new RequestParams();
        params.add("email", email);
        params.add("senha", senha);
        params.add("recaptcha_challenge_field", recaptchaChallengeField);
        params.add("recaptcha_response_field", recaptchaResponseField);
        client.post("http://cpbmais.cpb.com.br/login/includes/autenticate.php",
                params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Calendar c = Calendar.getInstance();
                        int iAno = c.get(Calendar.YEAR);
                        String sAno = String.valueOf(iAno);
                        client.get("http://cpbmais.cpb.com.br/htdocs/periodicos/medmat/" + sAno + "/frmd" + sAno + ".php",
                                null, new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                        recaptcha();
                                    }

                                    @Override
                                    public void onSuccess(int i, Header[] headers, String s) {
                                        Extracao e = new Extracao(getApplicationContext());
                                        if(e.ePaginaMeditacao(s)) {
                                            e.extraiMeditacao(s);
                                            startActivity(new Intent(getApplicationContext(), DiaMeditacaoActivity.class));
                                        }
                                        else {
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
                for (Header h : headers) {
                    System.out.println(h.getName() + " " + h.getValue());

                }
                /*for (Cookie c : myCookieStore.getCookies()) {
                    System.out.println(c.getName() + " " + c.getValue());
                }*/
//                Log.d("cookies", s );
            }
        });
    }
}
