package com.silas.meditacao.fragments;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.models.Meditacao;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {

    private Meditacao meditacao;
    private TextView tvTitulo, tvTextoBiblico, tvData, tvTexto, tvLinks;
    private ImageView ivErro;
    private AdView mAdView;

    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance(Meditacao m) {
        ContentFragment fragment = new ContentFragment();
        fragment.setMeditacao(m);
        return fragment;
    }

    public Meditacao getMeditacao() {
        return meditacao;
    }

    public void setMeditacao(Meditacao meditacao) {
        this.meditacao = meditacao;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            meditacao = savedInstanceState.getParcelable("meditacao");
        }

        setupAd();
        setupContent();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment, container, false);
        tvTitulo = view.findViewById(R.id.tvTitulo);
        tvTextoBiblico = view.findViewById(R.id.tvTextoBiblico);
        tvData = view.findViewById(R.id.tvData);
        tvTexto = view.findViewById(R.id.tvTexto);
        tvLinks = view.findViewById(R.id.tvLinks);
        ivErro = view.findViewById(R.id.iVerro);
        mAdView = view.findViewById(R.id.ad_view);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("meditacao", meditacao);
        super.onSaveInstanceState(outState);
    }

    private void setupContent() {
        if (meditacao != null) {
            tvTitulo.setText(meditacao.getTitulo().toUpperCase());
            tvTextoBiblico.setText(meditacao.getTextoBiblico());
            tvData.setText(meditacao.getDataPorExtenso());
            tvTexto.setText(meditacao.getTexto());
            tvLinks.setMovementMethod(LinkMovementMethod.getInstance());
            fixLinksColor();
            ivErro.setVisibility(View.GONE);
        }
    }

    public void update(Meditacao m) {
        setMeditacao(m);
        setupContent();
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
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("1DDC6B87A119F01DE92D910C6F5B9F5C")
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }
    }

    private void fixLinksColor() {
        if (PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(Preferences.DARK_THEME, false)) {
            tvLinks.setLinkTextColor(
                    getResources().getColor(R.color.textColorPrimary)
            );
        }
    }
}
