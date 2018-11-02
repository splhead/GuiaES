package com.silas.meditacao.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabAdapter;
import com.silas.meditacao.fragments.SettingsFragment;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.io.ProcessaMeditacoesTask;
import com.silas.meditacao.io.Util;
import com.silas.meditacao.models.Meditacao;

import java.lang.ref.WeakReference;
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

    private static final int MY_DATA_CHECK_CODE = 7;
    private static final int FAVORITES_ACTIVITY_CODE = 4;
    private static final String FIRST_TIME_NOTIFICATION_KEY = "first_time";
    private Calendar dia = Calendar.getInstance();

    private TextToSpeech tts;
    private MenuItem menuItem;
    private ProgressDialog progress;
    private ViewPager mViewPager;
    private TabAdapter tabAdapter;
    private FloatingActionButton fabFavorite;
    public static final Integer[] TYPES = {Meditacao.ADULTO, Meditacao.MULHER,
            Meditacao.JUVENIL, Meditacao.ABJANELAS};
    private AdView mAdView;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);

        initMeditacoes();

        setupToolbar();

        setupTTS();

        setupScreenKeepOn();

        setupAd();

        setupAnalytics();

        setupFirstTimeNotifications();
    }

    private void setupFirstTimeNotifications() {
        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(FIRST_TIME_NOTIFICATION_KEY, true)) {
            sendBroadcast(new Intent("com.silas.meditacao.AGENDADOR"));
            sharedPreferences.edit().putBoolean(FIRST_TIME_NOTIFICATION_KEY, false).apply();
        }

    }

    public void showProgressDialog() {

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setCanceledOnTouchOutside(true);
        progress.setTitle("Recebendo poder!");
        progress.setMessage("Ore pelo meu criador e aguarde...");
        progress.setCancelable(false);
        progress.show();
    }

    public void dismissProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    public ProgressDialog getProgressDialog() {
        return progress;
    }

    private void setupScreenKeepOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setupAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar_main);
        if (mToolbar != null) {
//            changeFontToolbar(mToolbar);
            mToolbar.setOnMenuItemClickListener(this);
            if (!PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(Preferences.DARK_THEME, false)) {
                mToolbar.setPopupTheme(R.style.Theme_Light_PopupOverlay);
            }
            mToolbar.inflateMenu(R.menu.main);
        }
    }

    /*private void changeFontToolbar(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView
                    && ((TextView) view).getText() == getString(R.string.app_name)) {
                ((TextView) view).setTypeface(
                        Typeface.createFromAsset(view.getFavoritesActivity().getAssets()
                                , "fonts/GreatVibes-Regular.ttf"));
            }
        }
    }*/

    public void initMeditacoes() {
        ArrayList<Meditacao> meditacoes = this.getIntent()
                .getParcelableArrayListExtra(LauncherActivity.DEVOTIONALS);

        setupTabAdapter(meditacoes);
        setupViewPager();

        int tabDefault = (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Preferences.TYPE_DEFAULT, "0")));
        setupTab(tabDefault);

        if (meditacoes == null || meditacoes.size() < TYPES.length) {

            new ProcessaMeditacoesTask(this, dia).execute(TYPES);
        } else {
            setupFABs();
        }

    }

    public TabAdapter getTabAdapter() {
        return tabAdapter;
    }

    public void setupTabAdapter(ArrayList<Meditacao> meditacoes) {
        if (meditacoes != null) {
            tabAdapter = new TabAdapter(getSupportFragmentManager(), meditacoes);
        } else {
            tabAdapter = new TabAdapter(getSupportFragmentManager());
        }
    }

    public void setupViewPager() {
        mViewPager = findViewById(R.id.pager);

        if (mViewPager != null) {

//                //corrige a troca de data para atualizar todas as tabs FragmentPagerAdapter
            mViewPager.setOffscreenPageLimit(TYPES.length);

            mViewPager.setAdapter(tabAdapter);

//            setupTab();

            TabLayout mTablayout = findViewById(R.id.tablayout);

            mTablayout.setupWithViewPager(mViewPager);

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
//                    setupFavoriteFab(tabAdapter.getMeditacao(mViewPager.getCurrentItem()));
                    setupFABs();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    public void setupTab(int tabDefault) {
        if (tabAdapter != null && mViewPager != null) {

            if (tabAdapter.getMeditacao(tabDefault) != null && tabDefault < TYPES.length) {

                mViewPager.setCurrentItem(tabDefault);

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
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_favorites_activity:
                startActivityForResult(new Intent(this, FavoritesActivity.class)
                        , FAVORITES_ACTIVITY_CODE);
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
                break;
            case R.id.action_speakout:
                if (tts != null) {
                    if (tts.isSpeaking()) {
                        tts.stop();
                        changeMenuItemIcon(item, R.drawable.ic_baseline_play_circle_outline_24px);
                    } else {
                        int i = mViewPager.getCurrentItem();
                        speakOut(tabAdapter.getMeditacao(i));
                        changeMenuItemIcon(item, R.drawable.ic_baseline_stop_24px);
                        menuItem = item;
                    }
                }

                break;

        }
        return false;
    }

    private void changeMenuItemIcon(MenuItem item, int id) {
        invalidateOptionsMenu();
        item.setIcon(ContextCompat.getDrawable(this, id));
    }

    private void setupDatePicker() {
        MeditacaoDBAdapter mdba = new MeditacaoDBAdapter(this);
        long[] dates = mdba.buscaDataMinMax(TYPES[mViewPager.getCurrentItem()]);
        if (dates != null) {
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


            mDateDialog.getDatePicker().setMinDate(dates[0]);
            mDateDialog.getDatePicker().setMaxDate(dia.getTimeInMillis());
            mDateDialog.setTitle("Qual dia?");
            mDateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", mDateDialog);
            mDateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Escolher", mDateDialog);
            mDateDialog.show();
        }
    }

    public void setupFABs() {
        final Meditacao meditacao = tabAdapter.getMeditacao(mViewPager.getCurrentItem());
        if ((meditacao != null)
                && (mViewPager != null)) {
            FloatingActionButton fab = findViewById(R.id.fab_share);

            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, Util.preparaCompartilhamento(meditacao));
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent,
                                getResources().getText(R.string.send_to)));
                        //Analytics
                        Bundle params = new Bundle();
                        params.putString("devotional_type", Meditacao.getNomeTipo(meditacao.getTipo()));
                        params.putString("devotional_date", meditacao.getData());
                        mFirebaseAnalytics.logEvent("share_devotional", params);
                    }
                });
            }

            fabFavorite = findViewById(R.id.fab_favorite);
            setupFavoriteFab(meditacao);

        }

    }

    private void setupFavoriteFab(Meditacao med) {
        final Meditacao meditacao = med;
        if (meditacao != null) {
            changeFavoriteFabIcon(meditacao.isFavorite());

            if (fabFavorite != null) {

                fabFavorite.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new updateFavoriteTask(MainActivity.this).execute(meditacao);
                    }
                });

            }
        }

    }

    public void changeFavoriteFabIcon(boolean isFavorite) {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_round_favorite_24px);
        } else {
            fabFavorite.setImageResource(R.drawable.ic_round_favorite_border_24px);
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(new Locale("pt", "BR"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public static class MyUndoListener implements View.OnClickListener {

        private Meditacao devotional;
        private MainActivity activity;

        MyUndoListener(MainActivity mainActivity, Meditacao meditacao) {
            devotional = meditacao;
            activity = mainActivity;
        }

        @Override
        public void onClick(View v) {
            new updateFavoriteTask(activity).execute(devotional);
        }
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

        dismissProgressDialog();

        if (mAdView != null) {
            mAdView.destroy();
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }


    private void setupAd() {
        Calendar hoje = Calendar.getInstance();
        if (Util.notShabbat(hoje)) {
            mAdView = findViewById(R.id.ad_view);
            AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice("B83B84C68C1C3930F91B91A13472E244")
//                    .addTestDevice("FC5AAA3D1C3842A79510C4C83BC27DD9")
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dia.set(year, month, day);
        updateMeditacoes();

        // Analytics
        Bundle params = new Bundle();
        params.putString("new_date", dia.toString());
        mFirebaseAnalytics.logEvent("change_date", params);
    }

    public void setDia(Meditacao devotional) {
        int year = Integer.valueOf(devotional.getData().substring(0, 4));
        int month = Integer.valueOf(devotional.getData().substring(5, 7)) - 1;
        int day = Integer.valueOf(devotional.getData().substring(8));
        this.dia.set(year, month, day);
        updateMeditacoes();
        setupTab(devotional.getTipo() - 1);
    }

    private void updateMeditacoes() {
        new ProcessaMeditacoesTask(this, dia).execute(TYPES);
    }

    private void setupTTS() {
        try {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("TTS", "Oops! The function is not available in your device.");
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_DATA_CHECK_CODE:
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    // success, create the TTS instance
                    tts = new TextToSpeech(this, this);

                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
//                Log.i("speech", "started");
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    // Stuff that updates the UI
                                    changeMenuItemIcon(menuItem, R.drawable.ic_baseline_play_circle_outline_24px);
                                }
                            });

                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.e("speech", "error");
                        }
                    });
                } else {
                    // missing data, install it
                    try {
                        Intent installIntent = new Intent();
                        installIntent.setAction(
                                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(installIntent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case FAVORITES_ACTIVITY_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Meditacao devotional = data.getParcelableExtra("devotional");
                    setDia(devotional);
                }
                break;
        }
    }

    private static class updateFavoriteTask extends AsyncTask<Meditacao, Void, Meditacao> {

        private WeakReference<MainActivity> wr;

        updateFavoriteTask(MainActivity mainActivity) {
            wr = new WeakReference<>(mainActivity);
        }

        @Override
        protected Meditacao doInBackground(Meditacao... meditacoes) {
            meditacoes[0].toogleFavorite();
            MeditacaoDBAdapter meditacaoDBAdapter = new MeditacaoDBAdapter(wr.get());
            meditacaoDBAdapter.updateDevotionalFavorite(meditacoes[0]);
            return meditacoes[0];
        }

        @Override
        protected void onPostExecute(Meditacao meditacao) {
            if (!wr.get().isFinishing()) {

                wr.get().changeFavoriteFabIcon(meditacao.isFavorite());

                Snackbar mySnack = Snackbar.make(wr.get().getCoordnatorLayout()
                        , "Salvo!", Snackbar.LENGTH_SHORT);

                mySnack.setAction("Desfazer", new MyUndoListener(wr.get(), meditacao));
                mySnack.show();
            }
            super.onPostExecute(meditacao);

        }
    }

    private void speakOut(Meditacao meditacao) {
        int pitch = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getInt(SettingsFragment.KEY_TTS_PITCH, 4);
        tts.setPitch(pitch * .25f);

        int speechRate = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getInt(SettingsFragment.KEY_TTS_RATE, 2);
        tts.setSpeechRate(speechRate * .5f);

        HashMap<String, String> mParams = new HashMap<>();
        mParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "DEVOTIONAL");
        tts.speak(prepareTextToSpeak(meditacao), TextToSpeech.QUEUE_FLUSH, mParams);

        Bundle bundle = new Bundle();
        bundle.putInt("tts_devotional", mViewPager.getCurrentItem() + 1);
        mFirebaseAnalytics.logEvent("play_tts", bundle);

    }

    private String prepareTextToSpeak(Meditacao meditacao) {

        if (meditacao == null) {
            return "Texto indisponível no momento. Por favor tente novamente mais tarde.";
        }

        String tmpOut = Meditacao.getDevotionalName(meditacao.getTipo()) + "... " +
                meditacao.getTitulo() + "... " +
                meditacao.getDataPorExtenso() + "... " +
                meditacao.getTextoBiblico() + "... " +
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
