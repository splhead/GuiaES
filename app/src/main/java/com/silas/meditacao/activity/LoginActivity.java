package com.silas.meditacao.activity;

import org.apache.http.Header;

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

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.CPBCliente;
import com.silas.meditacao.io.Util;

public class LoginActivity extends ActionBarActivity {

    final CPBCliente client = CPBCliente.getInstace(this);

//    public static final String PREFS_NAME = "LoginPrefs";
//    final String EMAIL = "silas_ladislau@yahoo.com.br";
//    final String SENHA = "spl#e@d";
    String recaptchaChallengeField = "";
    String recaptchaResponseField = "";
    String urlImagem, email, senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences _sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        cookies();
        recaptcha();
        final EditText etCaptcha = (EditText) findViewById(R.id.etCaptcha);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etSenha = (EditText) findViewById(R.id.etSenha);
        etSenha.setTransformationMethod(new PasswordTransformationMethod());

        email = _sharedPreferences.getString("email","");
        senha = _sharedPreferences.getString("senha", "");
        //Log.d("sp", email + "\n" + senha);
        if (email.length() > 0) etEmail.setText(email);
        if (senha.length() > 0) etSenha.setText(senha);

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
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
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

        client.get("http://www.google.com/recaptcha/api/noscript?k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7",
                null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
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
                client.get("http://cpbmais.cpb.com.br/htdocs/periodicos/medmat/2014/frmd2014.php",
                null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Log.d("t", s);
                    }
                });
            }
        });
    }

    private void teste() {

    }


    private void cookies() {
        client.get("http://cpbmais.cpb.com.br/login/index.php",null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                for(Header h : headers) {
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
