package com.silas.meditacao.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabAdapter;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.io.ProcessaMeditacoesTask;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends ThemedActivity implements
        Toolbar.OnMenuItemClickListener,
        DatePickerDialog.OnDateSetListener {

    private Calendar dia = Calendar.getInstance();
    private AdView mAdView;
    private ViewPager mViewPager;
    private TabAdapter tabAdapter;
    private MeditacaoDBAdapter mdba;
    private ArrayList<Integer> queeToDownload = new ArrayList<>();
    private int[] tipos = {Meditacao.ADULTO, Meditacao.MULHER,
            Meditacao.JUVENIL, Meditacao.ABJANELAS};
    private ArrayList<Meditacao> meditacoes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);

        mdba = new MeditacaoDBAdapter(this);

        setupGoogleAnalytics();

        setupScreenKeepOn();

        setupNotification();

        setupToolbar();

        initMeditacoes();


        setupAd();
    }


    private void setupNotification() {
        Intent myIntent = new Intent("AGENDADOR");
        sendBroadcast(myIntent);
    }

    private void setupScreenKeepOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setupGoogleAnalytics() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker trackerAd = analytics.newTracker("UA-64551284-1");
        Tracker tracker = analytics.newTracker("UA-64551284-2");
        trackerAd.enableExceptionReporting(true);
        tracker.enableExceptionReporting(true);
        trackerAd.enableAdvertisingIdCollection(true);
        tracker.enableAdvertisingIdCollection(true);
        trackerAd.enableAutoActivityTracking(true);
        tracker.enableAutoActivityTracking(true);
    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        if (mToolbar != null) {
            mToolbar.setTitle(getString(R.string.app_name));
            mToolbar.setOnMenuItemClickListener(this);
            if (!PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(Preferences.DARK_THEME, false)) {
                mToolbar.setPopupTheme(R.style.Theme_Light_PopupOverlay);
            }
            mToolbar.inflateMenu(R.menu.main);
        }
    }

    public void initMeditacoes() {
        meditacoes.clear();
        for (int tipo : tipos) {
            try {
                Meditacao meditacao = mdba.buscaMeditacao(dia, tipo);

                if (meditacao != null) {
                    meditacoes.add(meditacao);
                } else {
                    queeToDownload.add(tipo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (meditacoes.size() > 0) {
            setupViewPager();
        }

        if (queeToDownload.size() > 0) {
            Integer[] tipos = queeToDownload.toArray(new Integer[queeToDownload.size()]);
            new ProcessaMeditacoesTask(this, this, dia).execute(tipos);
            queeToDownload.clear();
        }

    }

    private void setupViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);

        if (mViewPager != null) {

            if (meditacoes.size() > 0) {

                tabAdapter = new TabAdapter(getSupportFragmentManager(), meditacoes);

//                //corrige a troca de data para atualizar todas as tabs FragmentPagerAdapter
//                mViewPager.setOffscreenPageLimit(meditacoes.size());

                mViewPager.setAdapter(tabAdapter);

                mViewPager.setCurrentItem((Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(Preferences.TYPE_DEFAULT, "0"))));

                TabLayout mTablayout = (TabLayout) findViewById(R.id.tablayout);

                mTablayout.setupWithViewPager(mViewPager);
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                break;
            case R.id.action_date:
                setupDatePicker();
                break;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, preparaCompartilhamento());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,
                        getResources().getText(R.string.send_to)));
                break;
            case R.id.action_share_app:
                Intent s = new Intent();
                s.setAction(Intent.ACTION_SEND);
                s.putExtra(Intent.EXTRA_TEXT,
                        "Olhe que aplicativo bacana \"Meditação Cristã Adventista\"\n" +
                                "https://play.google.com/store/apps/details?id=com.silas.guiaes.app");
                s.setType("text/plain");
                startActivity(Intent.createChooser(s,
                        getResources().getText(R.string.send_to)));

        }
        return false;
    }

    private void setupDatePicker() {
        dia = Calendar.getInstance();
        DatePickerDialog mDateDialog;

        if (!PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Preferences.DARK_THEME, false)) {
            mDateDialog = new DatePickerDialog(this,
                    R.style.AppTheme_DialogTheme, this
                    , dia.get(Calendar.YEAR),
                    dia.get(Calendar.MONTH), dia.get(Calendar.DAY_OF_MONTH));
        } else {
            mDateDialog = new DatePickerDialog(this,
                    R.style.AppTheme_DialogThemeInverse, this
                    , dia.get(Calendar.YEAR),
                    dia.get(Calendar.MONTH), dia.get(Calendar.DAY_OF_MONTH));
        }

        MeditacaoDBAdapter mdba = new MeditacaoDBAdapter(this);
        long[] dates = mdba.buscaDataMinMax(mViewPager.getCurrentItem() + 1);

        mDateDialog.getDatePicker().setMinDate(dates[0]);
        mDateDialog.getDatePicker().setMaxDate(dates[1]);
        mDateDialog.setTitle("Qual dia?");
        mDateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", mDateDialog);
        mDateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Escolher", mDateDialog);
        mDateDialog.show();
    }

    private String preparaCompartilhamento() {
        Meditacao meditacao = tabAdapter.getContentFragmentList()
                .get(mViewPager.getCurrentItem()).getMeditacao();

        if (meditacao == null) {
            return "Olhe que aplicativo bacana \"Meditação Cristã Adventista\"\n" +
                    "https://play.google.com/store/apps/details?id=com.silas.guiaes.app";
        }

        StringBuilder out = new StringBuilder();
        switch (meditacao.getTipo()) {
            case Meditacao.ADULTO:
                out.append("Meditação Matinal\n\n");
                break;
            case Meditacao.MULHER:
                out.append("Meditação da Mulher\n\n");
                break;
            case Meditacao.JUVENIL:
                out.append("Inspiração Juvenil\n\n");
                break;
            case Meditacao.ABJANELAS:
                out.append("Janelas para Vida\n\n");
                break;
        }

        out.append(meditacao.getTitulo());
        out.append("\n\n");
        out.append(meditacao.getTextoBiblico());
        out.append("\n\n");
        out.append(meditacao.getDataPorExtenso());
        out.append("\n\n");
        out.append(meditacao.getTexto());

        return out.toString();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private boolean notShabbat(Calendar hoje) {
        return !((hoje.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && hoje.get(Calendar.HOUR_OF_DAY) > 17) ||
                (hoje.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && hoje.get(Calendar.HOUR_OF_DAY) < 18));
    }

    private void setupAd() {
        Calendar hoje = Calendar.getInstance();
        if (notShabbat(hoje)) {
            // if not Shabbat load advertise
            mAdView = (AdView) findViewById(R.id.ad_view);

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("1DDC6B87A119F01DE92D910C6F5B9F5C")
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dia.set(year, month, day);
        initMeditacoes();
        tabAdapter.updateFragments();
    }
}
