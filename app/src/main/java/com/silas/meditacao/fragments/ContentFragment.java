package com.silas.meditacao.fragments;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdSize;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.models.Meditacao;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {

    private static final String AD_LOADED_KEY = "ad_loaded";
    private Meditacao meditacao;
    private TextView tvTitulo, tvTextoBiblico, tvData, tvTexto, tvLinks;
    private Boolean adIsLoaded = false;

    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance(Meditacao m) {
        ContentFragment fragment = new ContentFragment();
        fragment.setMeditacao(m);
        return fragment;
    }

    public static ContentFragment newInstance(int type) {
        Meditacao meditacao = new Meditacao("", "", "", "", type);
        return newInstance(meditacao);
    }

    public Meditacao getMeditacao() {
        return meditacao;
    }

    public void setMeditacao(Meditacao meditacao) {
        this.meditacao = meditacao;
    }

    public void setAdIsLoaded(Boolean adIsLoaded) {
        this.adIsLoaded = adIsLoaded;
        updateTitlePadding();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            meditacao = savedInstanceState.getParcelable(Meditacao.DEVOTIONAL_KEY);
            adIsLoaded = savedInstanceState.getBoolean(AD_LOADED_KEY);
        }

        setupContent();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvTitulo = view.findViewById(R.id.tvTitulo);
        tvTextoBiblico = view.findViewById(R.id.tvTextoBiblico);
        tvData = view.findViewById(R.id.tvData);
        tvTexto = view.findViewById(R.id.tvTexto);
        tvLinks = view.findViewById(R.id.tvLinks);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(Meditacao.DEVOTIONAL_KEY, meditacao);
        outState.putBoolean(AD_LOADED_KEY, adIsLoaded);
        super.onSaveInstanceState(outState);
    }

    private void setupContent() {
        if (!meditacao.getTitulo().isEmpty()) {
//            tvTitulo.setText(String.format("%s%s",getText(R.string.new_line), meditacao.getTitulo().toUpperCase()));
            tvTitulo.setText(meditacao.getTitulo().toUpperCase());
            tvTextoBiblico.setText(meditacao.getTextoBiblico());
            tvData.setText(meditacao.getDataPorExtenso());
            tvTexto.setText(meditacao.getTexto());
            tvLinks.setMovementMethod(LinkMovementMethod.getInstance());
            fixLinksColor();
            updateTitlePadding();
        } else {
            tvTitulo.setText(getText(R.string.loading));
        }
    }

    public void update(Meditacao m) {
        setMeditacao(m);
        setupContent();
    }

    private void updateTitlePadding() {
        if (adIsLoaded) {
            int paddingTop = AdSize.SMART_BANNER.getHeightInPixels(getActivity());
            tvTitulo.setPadding(tvTitulo.getTotalPaddingLeft(), paddingTop
                    , tvTitulo.getTotalPaddingRight(), 16);
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
