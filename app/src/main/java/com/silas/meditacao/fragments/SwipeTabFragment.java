package com.silas.meditacao.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.models.Meditacao;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {link SwipeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SwipeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class SwipeTabFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TIPO = "tipo";
    public static final String DATA = "data";

    private MeditacaoDBAdapter mdba;
    private Meditacao meditacao;

    private int iTipo;
    private String sData;

//    private OnFragmentInteractionListener mListener;

    public SwipeTabFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pTipo Parameter 1.
     * @param pData Parameter 2.
     * @return A new instance of fragment SwipeTabFragment.
     */

    public static SwipeTabFragment newInstance(int pTipo, String pData) {
        SwipeTabFragment fragment = new SwipeTabFragment();
        Bundle args = new Bundle();
        args.putInt(TIPO, pTipo);
        args.putString(DATA, pData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            iTipo = getArguments().getInt(TIPO);
            sData = getArguments().getString(DATA);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mdba = new MeditacaoDBAdapter(getActivity());
        try {
            TextView tvTitulo = (TextView) getView().findViewById(R.id.tvTitulo);
            TextView tvData = (TextView) getView().findViewById(R.id.tvData);
            TextView tvTextoBiblico = (TextView) getView().findViewById(R.id.tvTextoBiblico);
            TextView tvTexto = (TextView) getView().findViewById(R.id.tvTexto);
            TextView tvLinks = (TextView) getView().findViewById(R.id.tvLinks);
            tvLinks.setMovementMethod(LinkMovementMethod.getInstance());

            meditacao = mdba.buscaMeditacao(sData, iTipo);
            if(meditacao != null) {

                tvTitulo.setText(meditacao.getTitulo());
                tvData.setText(revertData(meditacao.getData()));
                tvTextoBiblico.setText(meditacao.getTextoBiblico());
                tvTexto.setText(meditacao.getTexto());

            } else {
                tvTitulo.setText("");
                tvData.setText("");
                tvTextoBiblico.setText("");
                tvTexto.setText("");
            }


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String preparaCompartilhamento(Meditacao meditacao) {
        if (meditacao == null) {
            return "";
        }
        StringBuilder saida = new StringBuilder();
        switch (meditacao.getTipo()) {
            case Meditacao.ADULTO:
                saida.append("Meditação Matinal\n\n");
                break;
            case Meditacao.MULHER:
                saida.append("Meditação da Mulher\n\n");
                break;
            case Meditacao.JUVENIL:
                saida.append("Inspiração Juvenil\n\n");
                break;
        }

        saida.append(meditacao.getTitulo() + "\n\n");
        saida.append(meditacao.getTextoBiblico() + "\n\n");
        saida.append(revertData(meditacao.getData()) + "\n\n");
        saida.append(meditacao.getTexto());
        return saida.toString();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dia_meditacao, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, preparaCompartilhamento(meditacao));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }
        return super.onOptionsItemSelected(item);
    }

    private String revertData(String data) {
        //yyyy-MM-dd
        String saida;
        String mes[] = new String[]{"janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
        saida = data.substring(8) + " de "
                + mes[Integer.parseInt(data.substring(5, 7)) - 1]
                + " de " + data.substring(0,4);
        return saida;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dia_meditacao, container, false);
    }

    /*
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/
   /* @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   /* public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }*/

}
