package com.silas.meditacao.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.models.Meditacao;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment implements Observer {

    private Meditacao meditacao;

    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance(Meditacao meditacao) {
        ContentFragment fragment = new ContentFragment();
        fragment.meditacao = meditacao;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupContent(view, meditacao);
    }

    @Override
    public void update(Observable observable, Object data) {
        View view = getView();
        if (data instanceof Meditacao) {
            Meditacao meditacao = (Meditacao) data;
            setupContent(view, meditacao);
        }
    }

    private void setupContent(View view, Meditacao meditacao) {
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
    }
}
