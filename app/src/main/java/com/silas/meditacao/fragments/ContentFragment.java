package com.silas.meditacao.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silas.guiaes.activity.R;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment, container, false);
        tvTitulo = (TextView) view.findViewById(R.id.tvTitulo);
        tvTextoBiblico = (TextView) view.findViewById(R.id.tvTextoBiblico);
        tvData = (TextView) view.findViewById(R.id.tvData);
        tvTexto = (TextView) view.findViewById(R.id.tvTexto);
        tvLinks = (TextView) view.findViewById(R.id.tvLinks);
        ivErro = (ImageView) view.findViewById(R.id.iVerro);

        return view;
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }

    /*private void searchOrDownload() {
        try {
            if (meditacao == null) {
                mdba = new MeditacaoDBAdapter(getActivity());
                meditacao = mdba.buscaMeditacao(dia, tipo);
            }

            if (meditacao == null && Util.internetDisponivel(getActivity())) {
                new ProcessaMeditacoesTask(getActivity(), this, dia).execute(tipo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("meditacao", meditacao);
        super.onSaveInstanceState(outState);
    }

    private void setupContent() {
        if (meditacao != null) {
            tvTitulo.setText(meditacao.getTitulo());
            tvTextoBiblico.setText(meditacao.getTextoBiblico());
            tvData.setText(meditacao.getDataPorExtenso());
            tvTexto.setText(meditacao.getTexto());
            tvLinks.setMovementMethod(LinkMovementMethod.getInstance());
            ivErro.setVisibility(View.GONE);
        }
    }

    public void update(Meditacao m) {
        setMeditacao(m);
        setupContent();
    }
}
