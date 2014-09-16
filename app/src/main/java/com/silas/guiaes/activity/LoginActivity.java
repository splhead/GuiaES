package com.silas.guiaes.activity;

import org.apache.http.Header;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.silas.guiaes.io.CPBCliente;
import com.silas.guiaes.io.Util;

public class LoginActivity extends ActionBarActivity {

    final CPBCliente client = CPBCliente.getInstace(this);
//    final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);


//    static CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL );
//    List<String> cookiesHeader;
    public static final String PREFS_NAME = "LoginPrefs";
    final String EMAIL = "silas_ladislau@yahoo.com.br";
    final String SENHA = "spl#e@d";
    String recaptchaChallengeField = "";
    String recaptchaResponseField = "";
    String sessionId;
    Connection con;
    String urlImagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        client.setCookieStore(myCookieStore);
        cookies();
        recaptcha();
        final EditText etCaptcha = (EditText) findViewById(R.id.etCaptcha);
        final Button button = (Button) findViewById(R.id.bEntrar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                recaptchaResponseField = etCaptcha.getText().toString();
                Log.d("cliquei", recaptchaResponseField);
                if (!recaptchaResponseField.isEmpty())
                    fazLogin(); teste();
//                    new LoginTask().execute();
            }
        });
       /* if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }

        CookieHandler.setDefault(cookieManager);
        final EditText etCaptcha = (EditText) findViewById(R.id.etCaptcha);

        new CookieCaptchaTask().execute();
//        new LoginTask().execute();

        final Button button = (Button) findViewById(R.id.bEntrar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                recaptchaResponseField = etCaptcha.getText().toString();
                Log.d("cliquei", recaptchaResponseField);
                if (!recaptchaResponseField.isEmpty())
                    new LoginTask().execute();
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

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    /*private String leFluxo(InputStream is) {
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
*/
    /*private void captcha() {
        URL url;
        HttpURLConnection urlConnection = null;
        InputStream in;


        try {

            //pegar o captcha
            url = new URL("http://www.google.com/recaptcha/api/noscript?k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");

            in = new BufferedInputStream(urlConnection.getInputStream());
            Document html = Jsoup.parse(leFluxo(in));

            Element challenge = html.select("input#recaptcha_challenge_field").first();
            recaptchaChallengeField = challenge.attr("value");

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
    }*/

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
        params.add("email", EMAIL);
        params.add("senha", SENHA);
        params.add("recaptcha_challenge_field", recaptchaChallengeField);
        params.add("recaptcha_response_field", recaptchaResponseField);
        client.post("http://cpbmais.cpb.com.br/login/includes/autenticate.php",
                params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Document html = Jsoup.parse(s);
                Log.d("fl", html.title());
            }
        });
    }

    private void teste() {
        client.get("http://cpbmais.cpb.com.br/htdocs/periodicos/lesjovens2014.php",
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
    /*private void login() {
        final DefaultHttpClient cliente = new DefaultHttpClient();

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", EMAIL));
            params.add(new BasicNameValuePair("senha", SENHA));
            params.add(new BasicNameValuePair("recaptcha_challenge_field", recaptchaChallengeField));
            params.add(new BasicNameValuePair("recaptcha_response_field", recaptchaResponseField));
            final HttpPost post = new HttpPost("http://cpbmais.cpb.com.br/login/includes/autenticate.php");
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(params);
            post.setEntity(uefe);
            System.out.println(leFluxo(uefe.getContent()));

            post.setHeader(cookiesHeader.get(0).split(";")[0].split("=")[0], cookiesHeader.get(0).split(";")[0].split("=")[1]);
            System.out.println(cookiesHeader.get(0).split(";")[0].split("=")[0]+" " + cookiesHeader.get(0).split(";")[0].split("=")[1]);
            HttpResponse res = cliente.execute(post);
            System.out.println(res.getStatusLine().getStatusCode());
            for(Header h: res.getAllHeaders()) {
                System.out.println(h.getName() + " " + h.getValue());
            }
            System.out.println("html: " + leFluxo(res.getEntity().getContent()));

        }catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        System.out.println(result.toString());
        return result.toString();
    }*/

    /*private List<String> getCookies(){
        URL url;//primeira requisição pegar o cookie
        HttpURLConnection urlConnection = null;

        if(cookiesHeader == null) try {
            url = new URL("http://cpbmais.cpb.com.br/login/index.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
            urlConnection.setInstanceFollowRedirects(false);
            cookiesHeader = urlConnection.getHeaderFields().get("Set-Cookie");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        for(String cookie : cookiesHeader ){
            System.out.println("cookie: " + cookie.split(";")[0]);
        }

        return cookiesHeader;
    }*/

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

    /*class CookieCaptchaTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... arg0) {
            getCookies();
            captcha();
            return null;
        }

    }*/

    /*class LoginTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... arg0) {
            getCookies();
            login();
            return null;
        }

    }*/


    /*private void login_olld() {
        try {

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
