package com.silas.meditacao.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.activity.AboutActivity;
import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.activity.PreferencesActivity;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabAdapter;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.io.ProcessaMeditacoesTask;
import com.silas.meditacao.io.Util;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DiaMeditacaoFragment extends Fragment implements Toolbar.OnMenuItemClickListener,
        DatePickerDialog.OnDateSetListener {

    private List<Meditacao> mTabs = new ArrayList<>();
    private Calendar dia;
    private ViewPager mViewPager;
    private Updatable mCallback;
    private int mesAnterior;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {

            MeditacaoDBAdapter mdba = new MeditacaoDBAdapter(getActivity());

            String sDia = args.getString(MainActivity.DIA);
            mesAnterior = args.getInt(MainActivity.MES_ANTERIOR);
            int mesAtual = args.getInt(MainActivity.MES_ATUAL);

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
                            if (Util.internetDisponivel(getActivity())) {
                                //noinspection unchecked
                                new ProcessaMeditacoesTask(getActivity(), mCallback, mesAnterior)
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dia_meditacao_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setOnMenuItemClickListener(this);
        if (!PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(Preferences.DARK_THEME, false)) {
            mToolbar.setPopupTheme(R.style.Theme_Light_PopupOverlay);
        }
        mToolbar.inflateMenu(R.menu.main);

        Calendar hoje = Calendar.getInstance();

        if (notShabbat(hoje)) {
            // if not Shabbat load advertise
            mAdView = (AdView) view.findViewById(R.id.ad_view);

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("1DDC6B87A119F01DE92D910C6F5B9F5C")
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        mViewPager = (ViewPager) view.findViewById(R.id.pager);

        TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager());
        tabAdapter.setList(mTabs);
        mViewPager.setAdapter(tabAdapter);

        TabLayout mTablayout = (TabLayout) view.findViewById(R.id.tablayout);

        mTablayout.setupWithViewPager(mViewPager);

        //        tira a imagem de erro do fundo

        if (mTabs.size() > 0) {
            ImageView ivErro = (ImageView) view.findViewById(R.id.iVerro);
            ivErro.setVisibility(View.GONE);
        }

    }

    private boolean notShabbat(Calendar hoje) {
        return !((hoje.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && hoje.get(Calendar.HOUR_OF_DAY) > 17) ||
                (hoje.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && hoje.get(Calendar.HOUR_OF_DAY) < 18));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
//        Log.d("onMenuItemClick frag", item.toString());
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                Intent i = new Intent(getActivity(), AboutActivity.class);
                startActivity(i);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), PreferencesActivity.class);
                startActivity(intent);
                break;
            case R.id.action_date:
                dia = Calendar.getInstance();
                DatePickerDialog mDateDialog;
//                Preferences preferences = new Preferences(getActivity());
                if (!PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getBoolean(Preferences.DARK_THEME, false)) {
                    mDateDialog = new DatePickerDialog(getActivity(),
                            R.style.AppTheme_DialogTheme, this
                            , dia.get(Calendar.YEAR),
                            dia.get(Calendar.MONTH), dia.get(Calendar.DAY_OF_MONTH));
                } else {
                    mDateDialog = new DatePickerDialog(getActivity(),
                            R.style.AppTheme_DialogThemeInverse, this
                            , dia.get(Calendar.YEAR),
                            dia.get(Calendar.MONTH), dia.get(Calendar.DAY_OF_MONTH));
                }
                Meditacao meditacao = mTabs.get(mViewPager.getCurrentItem());
                MeditacaoDBAdapter mdba = new MeditacaoDBAdapter(getActivity());
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dia.set(year, month, day);
        mCallback.onUpdate(dia, mesAnterior);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            try {
                mCallback = (Updatable) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + "must implement Updatable");

            }
        }
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

    public interface Updatable {
        void onUpdate(Calendar c, int mesAnterior);
    }
}
