package com.silas.meditacao.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabPagerAdapter;
import com.silas.meditacao.io.ProcessaMeditacoesTask;
import com.silas.meditacao.models.Meditacao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener {

    private MeditacaoDBAdapter mdba;
    private Meditacao mAdulto, mMulher, mJuvenil;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabPagerAdapter tabPagerAdapter;
    private String[] tabs = {"Adulto", "Mulher", "Juvenil"};
    private Calendar ca = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public String hoje = sdf.format(ca.getTime());
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!(ca.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && ca.get(Calendar.HOUR_OF_DAY) > 17) ||
                (ca.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && ca.get(Calendar.HOUR_OF_DAY) < 18)) {
            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) findViewById(R.id.ad_view);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        mdba = new MeditacaoDBAdapter(getApplication());

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);

        verificaMeditacao(this, viewPager);

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

    /**
     * Testa se esta gravada as meditacoes do dia
     * caso não as encontre tenta baixa-las!
     */

    private void verificaMeditacao(Context c, ViewPager vPager) {
        try {
            mAdulto = mdba.buscaMeditacao(hoje, Meditacao.ADULTO);

            mMulher = mdba.buscaMeditacao(hoje, Meditacao.MULHER);

            mJuvenil = mdba.buscaMeditacao(hoje, Meditacao.JUVENIL);


            if (mAdulto == null || mMulher == null || mJuvenil == null) {
                if (internetDisponivel(getApplication())) {
                    new ProcessaMeditacoesTask(c, vPager).execute(getURLs());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
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

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
