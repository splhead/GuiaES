package com.silas.meditacao.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.io.ExtraiMeditacao;
import com.silas.meditacao.io.HTTPCliente;
import com.silas.meditacao.models.Meditacao;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SwipeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SwipeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class SwipeTabFragment extends Fragment {
    final HTTPCliente client = HTTPCliente.getInstace(getActivity());
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TIPO = "tipo";
    public static final String DATA = "data";

    private MeditacaoDBAdapter mdba;
    private Meditacao meditacao;

    // TODO: Rename and change types of parameters
    private int iTipo;
    private String sData;

    private OnFragmentInteractionListener mListener;

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

    public SwipeTabFragment() {
        // Required empty public constructor
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mdba = new MeditacaoDBAdapter(getActivity());
        try {
            meditacao = mdba.buscaMeditacao(sData, iTipo);
            if(meditacao != null) {
                TextView tvTitulo = (TextView) getView().findViewById(R.id.tvTitulo);
                TextView tvData = (TextView) getView().findViewById(R.id.tvData);
                TextView tvTextoBiblico = (TextView) getView().findViewById(R.id.tvTextoBiblico);
                TextView tvTexto = (TextView) getView().findViewById(R.id.tvTexto);
                TextView tvLinks = (TextView) getView().findViewById(R.id.tvLinks);
                tvLinks.setMovementMethod(LinkMovementMethod.getInstance());

                tvTitulo.setText(meditacao.getTitulo());
                tvData.setText(revertData(meditacao.getData()));
                tvTextoBiblico.setText(meditacao.getTextoBiblico());
                tvTexto.setText(meditacao.getTexto());
            } else {
                baixaMeditacoes();
            }


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private HashMap<Integer,String> getURLs() {
        String url = "http://iasdcolonial.org.br/index.php/"; //
        HashMap<Integer,String> urls = new HashMap<Integer,String>();
        //adultos
        urls.put(Meditacao.ADULTO, url + "meditacao-diaria/mensal");
        //mulher
        urls.put(Meditacao.MULHER, url + "meditacao-da-mulher/mensal");
        //juvenil
        urls.put(Meditacao.JUVENIL, url + "inspiracao-juvenil/mensal");

        return urls;
    }

    private void baixaMeditacoes() {
        HashMap<Integer,String> urls = getURLs();
        Iterator it = urls.entrySet().iterator();

        while (it.hasNext()) {
            final Map.Entry<Integer,String> par = (Map.Entry<Integer,String>) it.next();

            client.get(par.getValue(),
                    null, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                            Toast.makeText(getActivity(), "ZICA", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {

                            ExtraiMeditacao e = new ExtraiMeditacao(getActivity());
                            e.processaExtracao(s, par.getKey());

                        }
                    });
        }

    }

    private String revertData(String data) {
        //yyyy-MM-dd
        String mes[] = new String[]{"janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
        return data.substring(8) + " de "
                + mes[Integer.parseInt(data.substring(5,7))]
                + " de " + data.substring(0,4);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_dia_meditacao, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
