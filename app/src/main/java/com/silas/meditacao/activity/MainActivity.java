package com.silas.meditacao.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabPagerAdapter;
import com.silas.meditacao.io.ExtraiMeditacao;
import com.silas.meditacao.models.Meditacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener {

    ProgressDialog progress;
    private MeditacaoDBAdapter mdba;
    private Meditacao meditacao, mAdulto, mMulher, mJuvenil;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabPagerAdapter tabPagerAdapter;
    private String[] tabs = {"Adulto", "Mulher", "Juvenil"};
    private StringBuilder sbMeditacoes = new StringBuilder();
    private Calendar ca = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public String hoje = sdf.format(ca.getTime());
    private String share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mdba = new MeditacaoDBAdapter(getApplication());

        progress = new ProgressDialog(this);
        progress.setIndeterminate(true);
        progress.setTitle("Recebendo poder!");
        progress.setMessage("Ore e aguarde...");
        progress.setCancelable(false);

        verificaMeditacao();

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        actionBar = getSupportActionBar(); //getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFC107")));

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * on swipe select the respective tab
             * */
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private String dataPorExtenso(Calendar ca) {
        int d = ca.get(Calendar.DAY_OF_MONTH);
        int m = ca.get(Calendar.MONTH);
        int a = ca.get(Calendar.YEAR);

        String mes[] = new String[]{"janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
        return d + " de " + mes[m] + " de " + a;
    }

    /**
     * Testa se esta gravada as meditacoes do dia
     * e prepara para compartilha-las
     * caso não as encontre tenta baixa-las!
     */

    private void verificaMeditacao() {
        ArrayList<Meditacao> meditacoes = new ArrayList<>();

        mAdulto = mdba.buscaMeditacao(hoje, Meditacao.ADULTO);
        meditacoes.add(mAdulto);
        mMulher = mdba.buscaMeditacao(hoje, Meditacao.MULHER);
        meditacoes.add(mMulher);
        mJuvenil = mdba.buscaMeditacao(hoje, Meditacao.JUVENIL);
        meditacoes.add(mJuvenil);

        Iterator<Meditacao> it = meditacoes.iterator();

        if (mAdulto != null && mMulher != null && mJuvenil != null) {
            try {
                while (it.hasNext()) {
                    meditacao = it.next();

                    switch (meditacao.getTipo()) {
                        case Meditacao.ADULTO:
                            sbMeditacoes.append("\n\nMeditação Matinal\n\n");
                            break;
                        case Meditacao.MULHER:
                            sbMeditacoes.append("\n\nMeditação da Mulher\n\n");
                            break;
                        case Meditacao.JUVENIL:
                            sbMeditacoes.append("\n\nInspiração Juvenil\n\n");
                            break;
                    }

                    if (meditacao != null) {
                        sbMeditacoes.append(meditacao.getTitulo());
                        sbMeditacoes.append("\n\n");
                        sbMeditacoes.append(dataPorExtenso(ca));
                        sbMeditacoes.append("\n\n");
                        sbMeditacoes.append(meditacao.getTextoBiblico());
                        sbMeditacoes.append("\n\n");
                        sbMeditacoes.append(meditacao.getTexto());
                        sbMeditacoes.append("\n\n\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            share = sbMeditacoes.toString();
            sbMeditacoes.delete(0, sbMeditacoes.length());

        } else {
            if (internetDisponivel(getApplication())) {
                new ProcessaMeditacoesTask().execute(getURLs());
            }
        }
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
        Toast.makeText(getApplicationContext(), "OPS! Parece que você está sem gravata (digo, INTERNET)" +
                        " asssim o poder (a meditação) não desce! ;)",
                Toast.LENGTH_SHORT).show();
//        Log.i("TestaInternet", "Não está conectado.");
        return false;
    }

    private HashMap<Integer, String> getURLs() {
        String url = "http://iasdcolonial.org.br/index.php/"; //
        HashMap<Integer, String> urls = new HashMap<>();
        //adultos
        urls.put(Meditacao.ADULTO, url + "meditacao-diaria/mensal");
        //mulher
        urls.put(Meditacao.MULHER, url + "meditacao-da-mulher/mensal");
        //juvenil
        urls.put(Meditacao.JUVENIL, url + "inspiracao-juvenil/mensal");

        return urls;
    }

    private void processaMeditacoes(Map.Entry<Integer, String> entry, int tentativa) {
        URL url;
        BufferedReader reader = null;
        StringBuffer stringBuffer;
        ExtraiMeditacao extrator = new ExtraiMeditacao(getApplication());

        try {

            // create the HttpURLConnection
            url = new URL(entry.getValue());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15 * 1000);
            connection.connect();

            // read the output from the server
            InputStream is = connection.getInputStream();

            if (is != null) {
                reader = new BufferedReader(new InputStreamReader(is));
                stringBuffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                String html = stringBuffer.toString();

                extrator.processaExtracao(html, entry.getKey());
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (tentativa <= 3) {
                tentativa++;
                processaMeditacoes(entry, tentativa);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dia_meditacao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                /*Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);*/
                break;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    class ProcessaMeditacoesTask extends AsyncTask<HashMap<Integer, String>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected Void doInBackground(HashMap<Integer, String>... tmp) {
            HashMap<Integer, String> urls = tmp[0];
            int tentativa = 1;

            for (Map.Entry<Integer, String> url : urls.entrySet()) {
                processaMeditacoes(url, tentativa);
//                publishProgress((int) ((totalSize / (float) count) * 100));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            viewPager.getAdapter().notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }
}
