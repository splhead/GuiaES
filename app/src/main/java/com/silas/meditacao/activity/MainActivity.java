package com.silas.meditacao.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabAdapter;
import com.silas.meditacao.fragments.DiaMeditacaoFragment;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.io.ProcessaMeditacoesTask;
import com.silas.meditacao.io.Util;
import com.silas.meditacao.models.Meditacao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ThemedActivity implements DiaMeditacaoFragment.Updatable,
        Toolbar.OnMenuItemClickListener,
        DatePickerDialog.OnDateSetListener {
    public static final String DIA = "dia";
    public static final String MES_ANTERIOR = "mes_anterior";
    public static final String MES_ATUAL = "mes_atual";
    private Calendar dia = Calendar.getInstance();
    private int mesAnterior = dia.get(Calendar.MONTH);
    private int mesAtual = dia.get(Calendar.MONTH);
    private AdView mAdView;
    private ViewPager mViewPager;
    private TabAdapter tabAdapter;
    private List<Meditacao> mTabs = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);
        if (savedInstanceState == null) {
//            updateFragment();
        }

        init();

        setupGoogleAnalytics();

        setupScreenKeepOn();

        setupNotification();

        setupToolbar();

        setupViewPager();

        setupTabs();


        setupAd();
    }

    private void init() {
//        mTabs.clear();
        MeditacaoDBAdapter mdba = new MeditacaoDBAdapter(this);

//            String sDia = args.getString(MainActivity.DIA);
//            mesAnterior = args.getInt(MainActivity.MES_ANTERIOR);
//            int mesAtual = args.getInt(MainActivity.MES_ATUAL);
        String sDia = sdf.format(dia.getTime());
        ArrayList<Meditacao> meditacoes = new ArrayList<>();

        try {

            meditacoes.add(mdba.buscaMeditacao(sDia, Meditacao.ADULTO));
            meditacoes.add(mdba.buscaMeditacao(sDia, Meditacao.MULHER));
            meditacoes.add(mdba.buscaMeditacao(sDia, Meditacao.JUVENIL));
            meditacoes.add(mdba.buscaMeditacao(sDia, Meditacao.ABJANELAS));

            for (int i = 0; i < meditacoes.size(); i++) {
                Meditacao meditacao = meditacoes.get(i);
                if (meditacao == null) {
                    //solução para não tentar baixar de outros meses quando escolhe outro dia
                    if (mesAnterior == mesAtual) {
                        if (Util.internetDisponivel(this)) {
                            //noinspection unchecked
                            new ProcessaMeditacoesTask(this, this, mesAnterior)
                                    .execute(i + 1);
                        }
                    }
                } else {
                    mTabs.add(meditacao);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void setupViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);

        if (mViewPager != null) {
            tabAdapter = new TabAdapter(getSupportFragmentManager());
            tabAdapter.setList(mTabs);
            mViewPager.setAdapter(tabAdapter);
        }
    }

    private void setupTabs() {
        TabLayout mTablayout = (TabLayout) findViewById(R.id.tablayout);

        mTablayout.setupWithViewPager(mViewPager);

        //        tira a imagem de erro do fundo

        if (mTabs.size() > 0) {
            ImageView ivErro = (ImageView) findViewById(R.id.iVerro);
            ivErro.setVisibility(View.GONE);
        }
    }

    /*public void updateFragment() {
//        Log.d("teste","aslkdjflaçksdjfçlkajsdfçlkjasdçlfjadsf");
        *//*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        String sDia = sdf.format(dia.getTime());
        args.putString(DIA, sDia);
        args.putInt(MES_ANTERIOR, mesAnterior);
        args.putInt(MES_ATUAL, mesAtual);
        DiaMeditacaoFragment fragment = new DiaMeditacaoFragment();
        fragment.setArguments(args);
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();*//*
    }*/

    @Override
    public void onUpdate(Calendar c, int ma) {
        mesAtual = c.get(Calendar.MONTH);
        mesAnterior = ma;
        dia = c;
        //updateFragment();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //        Log.d("onMenuItemClick frag", item.toString());
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                break;
            case R.id.action_date:
                dia = Calendar.getInstance();
                DatePickerDialog mDateDialog;
//                Preferences preferences = new Preferences(this);
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
                Meditacao meditacao = mTabs.get(mViewPager.getCurrentItem());
                MeditacaoDBAdapter mdba = new MeditacaoDBAdapter(this);
                long[] dates = mdba.buscaDataMinMax(meditacao.getTipo());
                mDateDialog.getDatePicker().setMinDate(dates[0]);
                mDateDialog.getDatePicker().setMaxDate(dates[1]);
                mDateDialog.setTitle("Qual dia?");
                mDateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", mDateDialog);
                mDateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Escolher", mDateDialog);
                mDateDialog.show();
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
//            http://bit.ly/1knCbjW

        }
        return false;
    }

    private String preparaCompartilhamento() {
        Meditacao meditacao = mTabs.get(mViewPager.getCurrentItem());

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
        updateFragments();
//        init();
        //onUpdate(dia, mesAnterior);
    }

    private void updateFragments() {
        tabAdapter.updateFragments();
    }
}
