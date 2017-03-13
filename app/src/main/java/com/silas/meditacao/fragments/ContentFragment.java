package com.silas.meditacao.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.interfaces.Updateable;
import com.silas.meditacao.io.ProcessaMeditacoesTask;
import com.silas.meditacao.io.Util;
import com.silas.meditacao.models.Meditacao;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment implements Updateable{

    private Meditacao meditacao;
    private Calendar dia;
    private int tipo;
    private MeditacaoDBAdapter mdba;

    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance(Calendar dia, int tipo) {
        ContentFragment fragment = new ContentFragment();
        fragment.setDia(dia);
        fragment.setTipo(tipo);
        return fragment;
    }

    public void setDia(Calendar dia) {
        this.dia = dia;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Meditacao getMeditacao() {
        return meditacao;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            meditacao = savedInstanceState.getParcelable("meditacao");
        }
        mdba = new MeditacaoDBAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment, container, false);
        searchOrDownload(view);
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("meditacao", meditacao);
        super.onSaveInstanceState(outState);
    }

    private void searchOrDownload(View view) {
        if (meditacao == null) {
            try {
                meditacao = mdba.buscaMeditacao(dia, tipo);

                if (meditacao == null && Util.internetDisponivel(getActivity())) {
                    new ProcessaMeditacoesTask(getActivity(), this, dia).execute(tipo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setupContent(view);

    }

    private void setupContent(View view) {
        if (meditacao != null) {
            TextView tvTitulo = (TextView) view.findViewById(R.id.tvTitulo);
            tvTitulo.setText(meditacao.getTitulo());

            TextView tvTextoBiblico = (TextView) view.findViewById(R.id.tvTextoBiblico);
            tvTextoBiblico.setText(meditacao.getTextoBiblico());

            TextView tvData = (TextView) view.findViewById(R.id.tvData);
            tvData.setText(meditacao.getDataPorExtenso());

            TextView tvTexto = (TextView) view.findViewById(R.id.tvTexto);
            tvTexto.setText(meditacao.getTexto());

            TextView tvLinks = (TextView) view.findViewById(R.id.tvLinks);
            tvLinks.setMovementMethod(LinkMovementMethod.getInstance());

            ImageView ivErro = (ImageView) view.findViewById(R.id.iVerro);
            ivErro.setVisibility(View.GONE);
        } else {
            TextView tvTitulo = (TextView) view.findViewById(R.id.tvTitulo);
            tvTitulo.setText("");

            TextView tvTextoBiblico = (TextView) view.findViewById(R.id.tvTextoBiblico);
            tvTextoBiblico.setText("");

            TextView tvData = (TextView) view.findViewById(R.id.tvData);
            tvData.setText("");

            TextView tvTexto = (TextView) view.findViewById(R.id.tvTexto);
            tvTexto.setText("");

            TextView tvLinks = (TextView) view.findViewById(R.id.tvLinks);
            tvLinks.setMovementMethod(LinkMovementMethod.getInstance());

            ImageView ivErro = (ImageView) view.findViewById(R.id.iVerro);
            ivErro.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdate(Calendar dia) {
        this.setDia(dia);
        searchOrDownload(getView());
    }
}
