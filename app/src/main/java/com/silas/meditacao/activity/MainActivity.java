package com.silas.meditacao.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabAdapter;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.io.ProcessaMeditacoesTask;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ThemedActivity implements
        Toolbar.OnMenuItemClickListener,
        DatePickerDialog.OnDateSetListener,
        TextToSpeech.OnInitListener {

    private Calendar dia = Calendar.getInstance();

    private TextToSpeech tts;
    private ViewPager mViewPager;
    private Integer[] tipos = {Meditacao.ADULTO, Meditacao.MULHER,
            Meditacao.JUVENIL, Meditacao.ABJANELAS};
    private ArrayList<Meditacao> meditacoes = new ArrayList<>();
    private AdView mAdView;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);

        setupAd();

        setupAnalytics();

        setupScreenKeepOn();

        setupToolbar();

        initMeditacoes();

        setupFAB();

        setupTTS();

    }

    private void setupScreenKeepOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setupAnalytics() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker trackerAd = analytics.newTracker("UA-64551284-1");
        Tracker tracker = analytics.newTracker("UA-64551284-2");
        trackerAd.enableExceptionReporting(true);
        tracker.enableExceptionReporting(true);
        trackerAd.enableAdvertisingIdCollection(true);
        tracker.enableAdvertisingIdCollection(true);
        trackerAd.enableAutoActivityTracking(true);
        tracker.enableAutoActivityTracking(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar_main);
        if (mToolbar != null) {
            mToolbar.setOnMenuItemClickListener(this);
            if (!PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(Preferences.DARK_THEME, false)) {
                mToolbar.setPopupTheme(R.style.Theme_Light_PopupOverlay);
            }
            mToolbar.inflateMenu(R.menu.main);
        }
    }


    private void setupTTS() {
        tts = new TextToSpeech(this, this);
    }

    public void initMeditacoes() {
        meditacoes.clear();
        new ProcessaMeditacoesTask(this, dia).execute(tipos);
    }

    public void setMeditacoes(ArrayList<Meditacao> meditacoes) {
        this.meditacoes = meditacoes;
    }

    public void setupViewPager() {
        mViewPager = findViewById(R.id.pager);

        if (mViewPager != null) {

            if (meditacoes.size() > 0) {

                TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), meditacoes);

//                //corrige a troca de data para atualizar todas as tabs FragmentPagerAdapter
//                mViewPager.setOffscreenPageLimit(meditacoes.size());

                mViewPager.setAdapter(tabAdapter);

                int tabDefault = (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(Preferences.TYPE_DEFAULT, "0")));

                if (tabDefault < tabAdapter.getCount()) {

                    mViewPager.setCurrentItem(tabDefault);

//                    recordTabDefaultAnalytics(tabDefault);

                }

                TabLayout mTablayout = findViewById(R.id.tablayout);

                mTablayout.setupWithViewPager(mViewPager);
            }
        }
    }

    public View getCoordnatorLayout() {
        return findViewById(R.id.coordinator_layout);
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
            case R.id.action_share_app:
                Intent s = new Intent();
                s.setAction(Intent.ACTION_SEND);
                s.putExtra(Intent.EXTRA_TEXT,
                        "Olhe que aplicativo bacana \"Meditação Cristã Adventista\"\n" +
                                "https://play.google.com/store/apps/details?id=com.silas.guiaes.app");
                s.setType("text/plain");
                startActivity(Intent.createChooser(s,
                        getResources().getText(R.string.send_to)));
            case R.id.action_speakout:
                speakOut();
                break;

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
        long[] dates = mdba.buscaDataMinMax(tipos[mViewPager.getCurrentItem()]);

        mDateDialog.getDatePicker().setMinDate(dates[0]);
        mDateDialog.getDatePicker().setMaxDate(dia.getTimeInMillis());
        mDateDialog.setTitle("Qual dia?");
        mDateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", mDateDialog);
        mDateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Escolher", mDateDialog);
        mDateDialog.show();
    }

    private void setupFAB() {
        FloatingActionButton fab = findViewById(R.id.fab_share);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, preparaCompartilhamento());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,
                        getResources().getText(R.string.send_to)));
            }
        });
    }

    private String preparaCompartilhamento() {
        Meditacao meditacao = meditacoes
                .get(mViewPager.getCurrentItem());

        if (meditacao == null) {
            return "Olhe que aplicativo bacana \"* Meditação Cristã Adventista *\"\n" +
                    "https://play.google.com/store/apps/details?id=com.silas.guiaes.app";
        }

        String sb = Meditacao.getDevotionalName(meditacao.getTipo()) +
                "\n\n*" +
                meditacao.getTitulo() +
                "*\n\n_" +
                meditacao.getDataPorExtenso() +
                "_\n\n*" +
                meditacao.getTextoBiblico() +
                "*\n\n" +
                meditacao.getTexto();

        //Analytics
        Bundle params = new Bundle();
        params.putString("devotional_type", Meditacao.getNomeTipo(meditacao.getTipo()));
        params.putString("devotional_date", meditacao.getData());
        mFirebaseAnalytics.logEvent("share_devotional", params);

        return sb;
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

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }

    private boolean notShabbat(Calendar hoje) {
        return !((hoje.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && hoje.get(Calendar.HOUR_OF_DAY) > 17) ||
                (hoje.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && hoje.get(Calendar.HOUR_OF_DAY) < 18));
    }

    private void setupAd() {
        mAdView = findViewById(R.id.ad_view);
        Calendar hoje = Calendar.getInstance();
        if (notShabbat(hoje)) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("B83B84C68C1C3930F91B91A13472E244")
                    .addTestDevice("FC5AAA3D1C3842A79510C4C83BC27DD9")
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dia.set(year, month, day);
        initMeditacoes();

        // Analytics
        Bundle params = new Bundle();
        params.putString("new_date", dia.toString());
        mFirebaseAnalytics.logEvent("change_date", params);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(new Locale("pt", "POR"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                ActionMenuItemView actionSpeakeout = findViewById(R.id.action_speakout);
                actionSpeakeout.setEnabled(true);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut() {
        tts.speak(prepareTextToSpeak(), TextToSpeech.QUEUE_FLUSH, null);
    }

    private String prepareTextToSpeak() {
        Meditacao meditacao = meditacoes
                .get(mViewPager.getCurrentItem());

        if (meditacao == null) {
            return "Texto indisponível no momento. Por favor tente novamente mais tarde.";
        }

        String tmpOut = Meditacao.getDevotionalName(meditacao.getTipo()) + "." +
                meditacao.getTitulo() + "." +
                meditacao.getDataPorExtenso() + "." +
                meditacao.getTextoBiblico() + "." +
                meditacao.getTexto();

        String out = tmpOut.replace(":", " ").replace("\n", "");

        return changeAbbreviation(out);
    }

    private String changeAbbreviation(String in) {
        Pattern pattern = Pattern.compile("[A-Za-z]{2}(?=\\s+\\d)");
        Matcher matcher = pattern.matcher(in);
        StringBuffer sb = new StringBuffer(in.length());
        Map<String, String> map = new HashMap<>();
        map.put("gn", "Gênisis");
        map.put("êx", "Êxodo");
        map.put("lv", "Levítico");
        map.put("nm", "Números");
        map.put("dt", "Deuteronômio");
        map.put("js", "Josué");
        map.put("jz", "Juízes");
        map.put("rt", "Rute");
        map.put("sm", "Samuel");
        map.put("rs", "Reis");
        map.put("cr", "Crônicas");
        map.put("ed", "Esdras");
        map.put("ne", "Neemias");
        map.put("et", "Ester");
        map.put("jó", "Jó");
        map.put("sl", "Salmos");
        map.put("pv", "Provérbios");
        map.put("ec", "Eclesiastes");
        map.put("ct", "Cantares");
        map.put("is", "Isaías");
        map.put("jr", "Jeremias");
        map.put("lm", "Lamentações de Jeremias");
        map.put("ez", "Ezequiel");
        map.put("dn", "Daniel");
        map.put("os", "Oséias");
        map.put("jl", "Joel");
        map.put("am", "Amós");
        map.put("ob", "Obadias");
        map.put("jn", "Jonas");
        map.put("mq", "Miquéias");
        map.put("na", "Naum");
        map.put("hc", "Habacuque");
        map.put("sf", "Sofonias");
        map.put("ag", "Ageu");
        map.put("zc", "Zacarias");
        map.put("ml", "Malaquias");
        map.put("mt", "Mateus");
        map.put("mc", "Marcos");
        map.put("lc", "Lucas");
        map.put("jo", "João");
        map.put("at", "Atos dos Apóstolos");
        map.put("rm", "Romanos");
        map.put("co", "Corintios");
        map.put("gl", "Gálatas");
        map.put("ef", "Efésios");
        map.put("fp", "Filipenses");
        map.put("cl", "Colossenses");
        map.put("ts", "Tessalonissenses");
        map.put("tn", "Timóteo");
        map.put("tt", "Tito");
        map.put("fm", "Filemon");
        map.put("hb", "Hebreus");
        map.put("tg", "Tiago");
        map.put("pe", "Pedro");
        map.put("jd", "Judas");
        map.put("ap", "Apocalipse");

        while (matcher.find()) {
            String abbreviation = matcher.group().toLowerCase();
            if (map.containsKey(abbreviation)) {
                abbreviation = map.get(abbreviation);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(abbreviation));
            }
//            Log.i("Encontrou", matcher.group());
        }

        matcher.appendTail(sb);
//        Log.i("Encontrou", sb.toString());
        return sb.toString();
    }
}
