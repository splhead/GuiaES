package com.silas.guiaes.activity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.graphics.Bitmap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



import android.widget.ImageView;

import com.silas.guiaes.io.Util;

public class LoginActivity extends ActionBarActivity {

    static CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL );
    List<String> cookiesHeader;
    public static final String PREFS_NAME = "LoginPrefs";
    final String EMAIL = "silas_ladislau@yahoo.com.br";
    final String SENHA = "spl#e@d";
    String recaptchaChallengeField = "";
    String recaptchaResponseField = "";
    String sessionId;
    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CookieHandler.setDefault(cookieManager);
        new LoginTask().execute();


        /*new CaptchaTask().execute("");
        final Button button = (Button) findViewById(R.id.bEntrar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText etCaptcha = (EditText) findViewById(R.id.etCaptcha);
                recaptchaResponseField = etCaptcha.getText().toString();
                Log.d("cliquei", recaptchaResponseField);

                new LoginTask().execute("");

            }
        });*/
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String leFluxo(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    private void captcha() {
        URL url;
        HttpURLConnection urlConnection = null;
        InputStream in = null;


        try {
            getCookies(urlConnection);

            //pegar o captcha
            url = new URL("http://www.google.com/recaptcha/api/noscript?k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7");
            urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());
            Document html = Jsoup.parse(leFluxo(in));
            Element imagem = html.select("img").first();
            Log.d("capt", html.html() + imagem.attr("src"));
            ImageView ivCaptcha;
            ivCaptcha = (ImageView) findViewById(R.id.ivCaptcha);
            new Util().imagemDownload("http://www.google.com/recaptcha/api/" + imagem.attr("src"), ivCaptcha);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }


        /*final String url ="http://www.google.com/recaptcha/api/noscript?k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7";
        final RequestParams params = new RequestParams();
        Document doc = new ExtraiDocument(url).html();
        Log.d("ca", doc.html());*/
       /* cliente.get("http://cpbmais.cpb.com.br/login/index.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.d("sadf", "Erro ao carregar login/index.php");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Document html = Jsoup.parse(s);
                Log.d("asdf", html.html());


            }
        });
        url = "http://www.google.com/recaptcha/api/noscript?k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7";
        cliente.get(url,
                params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Log.d("asdfl", "url " + url);
                        Document html = Jsoup.parse(s);
                        Element imagem = html.select("img").first();
                        Element desafio = html.select("input#recaptcha_challenge_field")
                                .first();
                        recaptchaChallengeField = desafio.attr("value");
                        Log.d("sdf", imagem.attr("abs:src"));

                        ImageView ivCaptcha;
                        ivCaptcha = (ImageView) findViewById(R.id.ivCaptcha);
                        new Util().imagemDownload(imagem.attr("abs:src"), ivCaptcha);
                    }
                });

        params.put("email", EMAIL);
        params.put("senha", SENHA);*/
    }

    private HttpURLConnection getCookies(HttpURLConnection urlConnection) throws IOException {
        URL url;//primeira requisição pegar o cookie
        url = new URL("http://cpbmais.cpb.com.br/login/index.php");
        urlConnection = (HttpURLConnection) url.openConnection();
        //in = new BufferedInputStream(urlConnection.getInputStream());
        //System.out.println(urlConnection.getHeaderField("PHPSESSID"));
        cookiesHeader = urlConnection.getHeaderFields().get("Set-Cookie");
        for(String cookie : cookiesHeader ){
            System.out.println("cookie: " + cookie);
        }
        return urlConnection;
    }

    private Connection getConexao() {
        if (con == null)
        return con = Jsoup.connect("http://cpbmais.cpb.com.br/");
        return con;
    }

    class CaptchaTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... arg0) {
            recaptcha();
            return null;
        }
    }

    class LoginTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... arg0) {
            captcha(); return null;
            /*try {
                con = getConexao();
                con.url("http://cpbmais.cpb.com.br/login/includes/autenticate.php")
                        .timeout(10000)
                        .userAgent(
                                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
                        .data("email", EMAIL, "senha", SENHA,
                                "recaptcha_challenge_field",
                                recaptchaChallengeField,
                                "recaptcha_response_field", recaptchaResponseField)
                        .method(Method.POST)
                        .cookie("PHPSESSID", sessionId);

                Document doc = con.execute().parse();



                Log.d("login", doc.title() + " ");

           *//* res = Jsoup.connect("http://cpbmais.cpb.com.br/htdocs/periodicos/lesjovens2014.php")
                    .cookie("PHPSESSID", sessionId)
                    .execute();
            doc = res.parse();
            Log.d("logado-request", doc.html() + " ");*//*

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;*/
        }
    }

    private void recaptcha() {
        try {
            con = getConexao();
            if (sessionId == null) {
                //para pegar o cookie
                con.url(
                        "http://cpbmais.cpb.com.br/login/index.php")
                        .timeout(10000)
                        .userAgent(
                                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
                sessionId = con.execute().cookie("PHPSESSID");
                Log.d("cookie", sessionId);
            }

            // abre o captch
            con.url("http://www.google.com/recaptcha/api/noscript?k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7");
            /*con = Jsoup
                    .connect(
                            "http://www.google.com/recaptcha/api/noscript?k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7")
                    .userAgent(
                            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
                            // .data("email", EMAIL, "senha", SENHA)
                            // .method(Method.POST)
                    .execute();*/
            Document doc = con.execute().parse();

            Element imagem = doc.select("img").first();
            Element desafio = doc.select("input#recaptcha_challenge_field")
                    .first();
            recaptchaChallengeField = desafio.attr("value");
            // String sessionId = res.cookie("PHPSESSID");+ sessionId

            //Log.d("login2", doc.html() + " ");
            Log.d("recaptcha", imagem.attr("abs:src"));

            ImageView ivCaptcha = (ImageView) findViewById(R.id.ivCaptcha);
            new Util().imagemDownload(imagem.attr("abs:src"), ivCaptcha);
            //new baixaImagemTask().execute(imagem.attr("abs:src"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private class baixaImagemTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            return new Util().baixaImagem(urls[0]);

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ImageView image = (ImageView) findViewById(R.id.ivCaptcha);
            image.setImageBitmap(result);
        }

    }*/

    private void login() {
        try {
            con = getConexao();
            con.url(
                            "http://cpbmais.cpb.com.br/login/includes/autenticate.php")
                    .timeout(10000)
                    .userAgent(
                            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
                    .data("email", EMAIL, "senha", SENHA,
                            "recaptcha_challenge_field",
                            recaptchaChallengeField,
                            "recaptcha_response_field", recaptchaResponseField)
                    .method(Method.POST)
                    .cookie("PHPSESSID", sessionId)
                    .execute();
            Document doc = con.execute().parse();



            Log.d("login", doc.html() + " ");

           /* res = Jsoup.connect("http://cpbmais.cpb.com.br/htdocs/periodicos/lesjovens2014.php")
                    .cookie("PHPSESSID", sessionId)
                    .execute();
            doc = res.parse();
            Log.d("logado-request", doc.html() + " ");*/

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
