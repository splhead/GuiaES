package com.silas.meditacao.fragments;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.models.Meditacao;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {

    private Meditacao meditacao;
    private TextView tvTitulo, tvTextoBiblico, tvData, tvTexto, tvLinks;
    private ImageView ivErro;

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

        setupContent();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment, container, false);
        tvTitulo = view.findViewById(R.id.tvTitulo);
        tvTextoBiblico = view.findViewById(R.id.tvTextoBiblico);
        tvData = view.findViewById(R.id.tvData);
        tvTexto = view.findViewById(R.id.tvTexto);
        tvLinks = view.findViewById(R.id.tvLinks);
        ivErro = view.findViewById(R.id.iVerro);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("meditacao", meditacao);
        super.onSaveInstanceState(outState);
    }

    private void setupContent() {
        if (meditacao != null) {
            tvTitulo.setText(String.format("%s%s",getText(R.string.new_line), meditacao.getTitulo().toUpperCase()));
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

    private void fixLinksColor() {
        if (PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(Preferences.DARK_THEME, false)) {
            tvLinks.setLinkTextColor(
                    getResources().getColor(R.color.textColorPrimary)
            );
        }
    }
}
