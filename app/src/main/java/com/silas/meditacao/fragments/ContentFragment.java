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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public ContentFragment() {
        // Required empty public constructor
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

    public int getTipo() {
        return tipo;
    }

    public static ContentFragment newInstance(Calendar dia, int tipo) {
        ContentFragment fragment = new ContentFragment();
        fragment.setDia(dia);
        fragment.setTipo(tipo);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdba = new MeditacaoDBAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment, container, false);
        searchOrDownload(view);
        return view;
    }


    private void searchOrDownload(View view) {
        String sDia = sdf.format(dia.getTime());
        try {
            meditacao = mdba.buscaMeditacao(sDia, tipo);
            Calendar hoje = Calendar.getInstance();
//            boolean mesAtual = (dia.get(Calendar.MONTH) == hoje.get(Calendar.MONTH));

            if(meditacao == null && Util.internetDisponivel(getActivity())
                    ) {
                new ProcessaMeditacoesTask(getActivity(), this, dia).execute(tipo);
            } else {
                setupContent(view,meditacao);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupContent(View view, Meditacao meditacao) {
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
    public void onUpdate(Calendar dia, int tipo) {
        this.setDia(dia);
        this.setTipo(tipo);
        searchOrDownload(getView());
    }
}
