package com.silas.meditacao.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabPagerAdapter;
import com.silas.meditacao.io.ExtraiMeditacao;
import com.silas.meditacao.io.HTTPCliente;
import com.silas.meditacao.models.Meditacao;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener {
    final HTTPCliente client = HTTPCliente.getInstace(getApplication());
    private MeditacaoDBAdapter mdba;
    private Meditacao meditacao, mAdulto, mMulher, mJuvenil;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabPagerAdapter tabPagerAdapter;
    private String[] tabs = { "Adulto", "Mulher", "Juvenil" };
    private StringBuilder sbMeditacoes = new StringBuilder();
    private Calendar ca = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public String hoje = sdf.format(ca.getTime());
    private String share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdba = new MeditacaoDBAdapter(getApplication());

        verificaMeditacao();
        preparaMeditacoesParaCompartilhar();

        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        actionBar = getSupportActionBar(); //getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
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
            public void onPageScrolled(int arg0, float arg1, int arg2) { }

            @Override
            public void onPageScrollStateChanged(int arg0) { }
        });
    }

    private void preparaMeditacoesParaCompartilhar() {
        ArrayList<Meditacao> meditacoes = new ArrayList<Meditacao>();


        mAdulto = mdba.buscaMeditacao(hoje, Meditacao.ADULTO);
        meditacoes.add(mAdulto);
        mMulher = mdba.buscaMeditacao(hoje, Meditacao.MULHER);
        meditacoes.add(mMulher);
        mJuvenil = mdba.buscaMeditacao(hoje, Meditacao.JUVENIL);
        meditacoes.add(mJuvenil);

        Iterator<Meditacao> it = meditacoes.iterator();

        try {
            while (it.hasNext()) {
                meditacao = it.next();
                if (meditacao != null) {
                    sbMeditacoes.append(meditacao.getTitulo());
                    sbMeditacoes.append("\n\n");
                    sbMeditacoes.append(dataPorExtenso(ca));
                    sbMeditacoes.append("\n\n");
                    sbMeditacoes.append(meditacao.getTextoBiblico());
                    sbMeditacoes.append("\n\n");
                    sbMeditacoes.append(meditacao.getTexto());
                    sbMeditacoes.append("\n\n\n\n");

                    Log.d("mainteste", meditacao.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        share = sbMeditacoes.toString();
        sbMeditacoes.delete(0,sbMeditacoes.length());
        Log.d("share", share);
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
     * Testa se existe pelo menos a dos adultos caso não encontre tenta baixar!
     */

    private void verificaMeditacao() {


        meditacao = mdba.buscaMeditacao(hoje,1);
        if(meditacao == null) {
            if (internetDisponivel(getApplication())) {
                baixaMeditacoes();
            } else {
                Toast.makeText(getApplicationContext(), "OPS! É necessário INTERNET na primeira vez que usa no mês ;)", Toast.LENGTH_SHORT).show();
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
//        Log.i("TestaInternet", "Não está conectado.");
        return false;
    }

    private HashMap<Integer,String> getURLs() {
        String url = "http://iasdcolonial.org.br/index.php/"; //
        HashMap<Integer,String> urls = new HashMap<Integer,String>();
        //adultos
        urls.put(Meditacao.ADULTO, url + "meditacao-diaria/mensal");
        //mulher
        urls.put(Meditacao.MULHER, url + "meditacao-da-mulher/mensal");
        //juvenil
        urls.put(Meditacao.JUVENIL, url + "inspiracao-juvenil/mensal");

        return urls;
    }

    private void baixaMeditacoes() {
        HashMap<Integer,String> urls = getURLs();
        Iterator it = urls.entrySet().iterator();

        while (it.hasNext()) {
            final Map.Entry<Integer,String> par;
            par = (Map.Entry<Integer,String>) it.next();

            client.get(par.getValue(),
                    null, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                            Toast.makeText(getApplication(), "ZICA", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {

                            ExtraiMeditacao e = new ExtraiMeditacao(getApplication());
                            e.processaExtracao(s, par.getKey());

                        }
                    });
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
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
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
}
