package com.silas.meditacao.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.silas.guiaes.activity.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {

    private static final String TITULO = "titulo";
    private static final String TEXTO_BIBLICO = "texto_biblico";
    private static final String DATA = "data";
    private static final String TEXTO = "texto";
    private static final String TIPO = "tipo";


    private String mTitulo;
    private String mTextoBiblico;
    private String mData;
    private String mTexto;
    private int mTipo;

    public ContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param titulo       Parameter 1.
     * @param textoBiblico Parameter 2.
     * @return A new instance of fragment ContentFragment.
     */

    public static ContentFragment newInstance(String titulo, String textoBiblico, String data, String texto, int tipo) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString(TITULO, titulo);
        args.putString(TEXTO_BIBLICO, textoBiblico);
        args.putString(DATA, data);
        args.putString(TEXTO, texto);
        args.putInt(TIPO, tipo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitulo = getArguments().getString(TITULO);
            mTextoBiblico = getArguments().getString(TEXTO_BIBLICO);
            mData = getArguments().getString(DATA);
            mTexto = getArguments().getString(TEXTO);
            mTipo = getArguments().getInt(TIPO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dia_meditacao, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            TextView tvTitulo = (TextView) view.findViewById(R.id.tvTitulo);
            tvTitulo.setText(mTitulo);

            TextView tvTextoBiblico = (TextView) view.findViewById(R.id.tvTextoBiblico);
            tvTextoBiblico.setText(mTextoBiblico);

            TextView tvData = (TextView) view.findViewById(R.id.tvData);
            tvData.setText(mData);

            TextView tvTexto = (TextView) view.findViewById(R.id.tvTexto);
            tvTexto.setText(mTexto);
        }
    }
}
