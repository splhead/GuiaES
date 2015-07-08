package com.silas.meditacao.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabPagerAdapter;
import com.silas.meditacao.models.Meditacao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    private MeditacaoDBAdapter mdba;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabPagerAdapter tabPagerAdapter;
    private String[] tabs = {"Adulto", "Mulher", "Juvenil"};
    private Calendar dia = Calendar.getInstance();
    public String sDia = converteData(dia);
    private DatePickerDialog mDateDialog;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.inflateMenu(R.menu.main);
        setSupportActionBar(mToolbar);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.action_about:
                        Intent i = new Intent(getApplication(), AboutActivity.class);
                        startActivity(i);
                        break;
                    case R.id.action_date:
                        mDateDialog = new DatePickerDialog(MainActivity.this, MainActivity.this, dia.get(Calendar.YEAR),
                                dia.get(Calendar.MONTH), dia.get(Calendar.DAY_OF_MONTH));
                        mDateDialog.setTitle("Qual dia?");
                        mDateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", mDateDialog);
                        mDateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Escolher", mDateDialog);
                        mDateDialog.show();
                        break;
                }
                return true;
            }
        });

        mToolbar.inflateMenu(R.menu.main);

        if (!(dia.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && dia.get(Calendar.HOUR_OF_DAY) > 17) ||
                (dia.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && dia.get(Calendar.HOUR_OF_DAY) < 18)) {
            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) findViewById(R.id.ad_view);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        mdba = new MeditacaoDBAdapter(getApplication());

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), sDia);
        viewPager.setAdapter(tabPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        verificaMeditacao(this, viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        for (String tabName : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tabName));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    private String converteData(Calendar ca) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(ca.getTime());
    }

    /**
     * Testa se esta gravada as meditacoes do sDia
     * caso não as encontre tenta baixa-las!
     */

    private void verificaMeditacao(Context c, ViewPager vPager) {
        try {
            Meditacao mAdulto = mdba.buscaMeditacao(sDia, Meditacao.ADULTO);

            Meditacao mMulher = mdba.buscaMeditacao(sDia, Meditacao.MULHER);

            Meditacao mJuvenil = mdba.buscaMeditacao(sDia, Meditacao.JUVENIL);


            if (mAdulto == null || mMulher == null || mJuvenil == null) {
                if (internetDisponivel(getApplication())) {
//                    new ProcessaMeditacoesTask(c, vPager).execute(getURLs());
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
                Toast.LENGTH_LONG).show();
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
        dia = Calendar.getInstance();
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dia.set(year, month, day);
        tabPagerAdapter.setDia(converteData(dia));
        tabPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
